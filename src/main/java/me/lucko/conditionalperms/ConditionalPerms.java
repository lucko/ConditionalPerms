/*
 * Copyright (c) 2017 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.conditionalperms;

import lombok.Getter;
import lombok.Setter;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.hooks.AbstractHook;
import me.lucko.conditionalperms.hooks.HookManager;
import me.lucko.helper.Events;
import me.lucko.helper.Scheduler;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.terminable.CompositeTerminable;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.utils.Color;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConditionalPerms extends ExtendedJavaPlugin implements CompositeTerminable {
    private static final Splitter DOT_SPLIT = Splitter.on('.').omitEmptyStrings().trimResults();
    private static final Splitter EQUALS_SPLIT = Splitter.on('=').omitEmptyStrings().trimResults().limit(2);

    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    /**
     * Used to stop any listeners in hooks firing for players who do not have any conditional permissions assigned.
     */
    @Getter
    private final Multimap<UUID, Class<? extends AbstractHook>> neededHooks = HashMultimap.create();

    @Getter
    private HookManager hookManager;

    @Getter
    @Setter
    private boolean debug = false;

    public void debug(String s) {
        if (debug) getLogger().info("[DEBUG] " + s);
    }

    @Override
    public void onEnable() {
        bindTerminable(this);

        for (Condition condition : Condition.values()) {
            condition.getCondition().init(this);
        }

        hookManager = new HookManager(this);
        hookManager.init();
    }

    @Override
    public void bind(Consumer<Terminable> consumer) {
        Events.subscribe(PlayerLoginEvent.class)
                .handler(e -> attachments.put(e.getPlayer().getUniqueId(), e.getPlayer().addAttachment(this)))
                .register(consumer);

        Events.subscribe(PlayerJoinEvent.class)
                .handler(e -> {
                   refreshPlayer(e.getPlayer());
                   refreshPlayer(e.getPlayer(), 20L);
                })
                .register(consumer);

        Events.subscribe(PlayerQuitEvent.class)
                .handler(e -> e.getPlayer().removeAttachment(attachments.remove(e.getPlayer().getUniqueId())))
                .register(consumer);
    }

    public void refreshPlayer(final Player player, long delay) {
        Scheduler.runLaterSync(() -> refreshPlayer(player), delay);
    }

    public void refreshPlayer(Player player) {
        debug("Processing permissions for player " + player.getName() + ".");
        final PermissionAttachment attachment = attachments.get(player.getUniqueId());
        if (attachment == null) {
            debug("Aborting, permission attachment is null.");
            return;
        }

        // Clear existing applied permissions
        for (String p : attachment.getPermissions().keySet()) {
            attachment.unsetPermission(p);
        }
        neededHooks.removeAll(player.getUniqueId());

        // process recursively so you can chain permissions together
        boolean work = true;
        final List<String> applied = new ArrayList<>();
        while (work) {
            work = false;

            for (PermissionAttachmentInfo pa : player.getEffectivePermissions()) {
                // Don't apply negative permissions
                if (!pa.getValue()) continue;

                // check we're handling a cperms node
                if (!pa.getPermission().startsWith("cperms.")) continue;

                // don't re-apply permissions
                if (applied.contains(pa.getPermission())) continue;

                debug("Processing conditional permission: " + pa.getPermission());
                final List<String> parts = DOT_SPLIT.splitToList(pa.getPermission());
                if (parts.size() <= 2) {
                    debug("Aborting, permission does not contain a node to apply.");
                    continue;
                }

                String conditionPart = parts.get(1);

                boolean negated = conditionPart.startsWith("!");
                if (negated) {
                    debug("Condition is negated.");
                    conditionPart = conditionPart.substring(1);
                }

                String parameter = null;
                if (conditionPart.contains("=")) {
                    final List<String> parameterSplit = EQUALS_SPLIT.splitToList(conditionPart);
                    conditionPart = parameterSplit.get(0);
                    parameter = parameterSplit.get(1);
                    debug("Found parameter: " + parameter);
                }

                Condition condition = null;
                for (Condition i : Condition.values()) {
                    if (i.name().equalsIgnoreCase(conditionPart)) {
                        condition = i;
                        debug("Found condition " + condition.name() + ".");
                        break;
                    }
                }

                if (condition == null) {
                    debug("Aborting, could not find a condition that matches " + conditionPart + ".");
                    continue;
                }

                final AbstractCondition c = condition.getCondition();
                if (c.isHookNeeded() && !hookManager.isHooked(c.getNeededHook())) {
                    debug("Aborting, condition " + condition.name() + " requires hook " + c.getNeededHook().getSimpleName() + " to function.");
                    continue;
                }

                if (c.isParameterNeeded() && parameter == null) {
                    debug("Aborting, condition " + condition.name() + " requires a parameter, but one was not given.");
                    continue;
                }

                // register that the hook is needed before checking if the condition is met. they might meet the condition at a later time.
                if (c.isHookNeeded()) {
                    neededHooks.put(player.getUniqueId(), c.getNeededHook());
                }

                final boolean shouldApply = c.shouldApply(player, parameter);
                if (negated == shouldApply) {
                    debug("Player did not meet the conditions required for this permission to be applied.");
                    continue;
                }

                final String toApply = parts.subList(2, parts.size()).stream().collect(Collectors.joining("."));
                attachment.setPermission(toApply, true);
                debug("Applying permission " + pa.getPermission() + " --> " + toApply + " for player " + player.getName() + ".");

                work = true;
                applied.add(pa.getPermission());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            msg(sender, "Running version &bv" + getDescription().getVersion() + "&7.");
            if (sender.hasPermission("conditionalperms.reload")) {
                msg(sender, "--> &b/cperms reload&7 to refresh all online users.");
                msg(sender, "--> &b/cperms reload <username>&7 to refresh a specific user.");
            }
            if (sender.hasPermission("conditionalperms.debug")) {
                msg(sender, "--> &b/cperms debug&7 to toggle debug mode.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("conditionalperms.reload")) {
            if (args.length > 1) {
                final Player p = getServer().getPlayer(args[1]);
                if (p == null) {
                    msg(sender, "&7Player '" + args[1] + "' is not online.");
                } else {
                    refreshPlayer(p);
                    msg(sender, "&7Player &b" + p.getName() + " &7had their permissions refreshed.");
                }
            } else {
                for (Player p : getServer().getOnlinePlayers()) {
                    refreshPlayer(p);
                }
                msg(sender, "&7All online users were refreshed.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("debug") && sender.hasPermission("conditionalperms.debug")) {
            debug = !debug;
            msg(sender, "&7Set debug to &b" + debug + "&7.");
            return true;
        }

        msg(sender, "&7Unknown sub command.");
        return true;
    }

    private static void msg(CommandSender sender, String message) {
        sender.sendMessage(Color.colorize("&8&l[&fConditionalPerms&8&l] &7" + message));
    }

    // for maven shade
    private void ensureLoad() {
        me.markeh.factionsframework.layer.layer_1_6.Command_1_6.class.getName();
        me.markeh.factionsframework.layer.layer_1_8.Command_1_8.class.getName();
        me.markeh.factionsframework.layer.layer_2_6.Command_2_6.class.getName();
        me.markeh.factionsframework.layer.layer_2_7.Command_2_7.class.getName();
        me.markeh.factionsframework.layer.layer_2_8_2.Command_2_8_2.class.getName();
        me.markeh.factionsframework.layer.layer_2_8_6.Command_2_8_6.class.getName();
        me.markeh.factionsframework.layer.layer_2_8_8.Command_2_8_8.class.getName();
        me.markeh.factionsframework.layer.layer_2_8_16.Command_2_8_16.class.getName();
    }
}
