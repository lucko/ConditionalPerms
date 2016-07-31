package me.lucko.conditionalperms;

import lombok.Getter;
import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.hooks.HookManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.regex.Pattern;

public class ConditionalPerms extends JavaPlugin implements Listener {
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.");
    private static final Pattern EQUALS_PATTERN = Pattern.compile("=");

    private final Map<UUID, PermissionAttachment> attachments = new HashMap<>();

    @Getter
    private final HookManager hookManager = new HookManager(this);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        for (Condition condition : Condition.values()) {
            condition.getCondition().init(this);
        }

        hookManager.init();
    }

    @Override
    public void onDisable() {
        hookManager.shutdown();
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        attachments.put(e.getPlayer().getUniqueId(), e.getPlayer().addAttachment(this));
        refreshPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        refreshPlayer(e.getPlayer());
        refreshPlayerDelay(20L, e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        p.removeAttachment(attachments.get(p.getUniqueId()));
        attachments.remove(p.getUniqueId());
    }

    public void refreshPlayerDelay(long delay, final Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                refreshPlayer(player);
            }
        }.runTaskLater(this, delay);
    }

    public void refreshPlayer(Player player) {
        final PermissionAttachment attachment = attachments.get(player.getUniqueId());
        if (attachment == null) return;

        // Clear existing applied permissions
        for (String p : attachment.getPermissions().keySet()) {
            attachment.unsetPermission(p);
        }

        // process recursively so you can chain permissions together
        boolean work = true;
        final List<String> applied = new ArrayList<>();
        while (work) {
            work = false;

            for (PermissionAttachmentInfo pa : player.getEffectivePermissions()) {
                // Don't apply negative permissions
                if (!pa.getValue()) {
                    continue;
                }

                if (!pa.getPermission().startsWith("cperms.")) {
                    continue;
                }

                if (applied.contains(pa.getPermission())) {
                    continue;
                }

                final List<String> parts = Arrays.asList(DOT_PATTERN.split(pa.getPermission()));
                if (parts.size() <= 2) {
                    continue;
                }

                String conditionPart = parts.get(1);

                boolean negated = conditionPart.startsWith("!");
                if (negated) {
                    conditionPart = conditionPart.substring(1);
                }

                String parameter = null;
                if (conditionPart.contains("=")) {
                    final String[] parameterSplit = EQUALS_PATTERN.split(conditionPart, 2);
                    conditionPart = parameterSplit[0];
                    parameter = parameterSplit[1];
                }

                Condition condition = null;
                for (Condition i : Condition.values()) {
                    if (i.name().equalsIgnoreCase(conditionPart)) {
                        condition = i;
                        break;
                    }
                }

                if (condition == null) {
                    continue;
                }

                final AbstractCondition c = condition.getCondition();
                if (c.isParameterNeeded() && parameter == null) {
                    continue;
                }

                final boolean shouldApply = c.shouldApply(player, parameter);
                if (negated == shouldApply) {
                    continue;
                }

                final String toApply = StringUtils.join(parts.subList(2, parts.size()), ".");
                attachment.setPermission(toApply, true);
                work = true;
                applied.add(pa.getPermission());
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "Running version &bv" + getDescription().getVersion() + "&7.");
            if (sender.hasPermission("conditionalperms.reload")) {
                sendMessage(sender, "Use &b/cperms reload &7 to refresh all online users.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("conditionalperms.reload")) {
            for (Player p : getServer().getOnlinePlayers()) {
                refreshPlayer(p);
            }
            sendMessage(sender, "&7All online users were refreshed.");
            return true;
        }

        sendMessage(sender, "&7Unknown sub command.");
        return true;
    }

    private static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&l[&fConditionalPerms&8&l] &7" + message));
    }
}
