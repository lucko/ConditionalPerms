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

package me.lucko.conditionalperms.hooks.impl;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.events.PlayerEnterCombatEvent;
import me.lucko.conditionalperms.events.PlayerLeaveCombatEvent;
import me.lucko.conditionalperms.hooks.AbstractHook;
import me.lucko.helper.Events;
import me.lucko.helper.Scheduler;
import me.lucko.helper.utils.Terminable;

import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;
import net.minelink.ctplus.event.PlayerCombatTagEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class CombatTagPlusHook extends AbstractHook implements Runnable {
    private final Set<UUID> taggedPlayers = new HashSet<>();
    private TagManager manager;

    CombatTagPlusHook(ConditionalPerms plugin) {
        super(plugin);
        CombatTagPlus combatTagPlus = (CombatTagPlus) getPlugin().getServer().getPluginManager().getPlugin("CombatTagPlus");
        manager = combatTagPlus.getTagManager();
    }

    public boolean isTagged(Player player) {
        return manager.isTagged(player.getUniqueId());
    }

    @Override
    public void bind(Consumer<Terminable> consumer) {
        Scheduler.runTaskRepeatingSync(this, 1L, 20L)
                .register(consumer);

        Events.subscribe(PlayerQuitEvent.class)
                .handler(e -> taggedPlayers.remove(e.getPlayer().getUniqueId()))
                .register(consumer);

        Events.subscribe(PlayerCombatTagEvent.class)
                .handler(e -> {
                    if (e.getVictim() != null) {
                        if (shouldCheck(CombatTagPlusHook.class, e.getVictim().getUniqueId())) {
                            taggedPlayers.add(e.getVictim().getUniqueId());
                        }
                    }
                    if (e.getAttacker() != null) {
                        if (shouldCheck(CombatTagPlusHook.class, e.getVictim().getUniqueId())) {
                            taggedPlayers.add(e.getVictim().getUniqueId());
                        }
                    }

                    // Pass on CombatTagPlus events if the hook is enabled.
                    getPlugin().getServer().getPluginManager().callEvent(new PlayerEnterCombatEvent(e.getPlayer(), e.getVictim(), e.getAttacker()));
                })
                .register(consumer);
    }

    @Override
    public void run() {
        // Ambient tag checking task
        // Not ideal, but there is no event for players leaving combat

        final Set<Player> untag = new HashSet<>();
        for (UUID u : taggedPlayers) {
            if (!shouldCheck(CombatTagPlusHook.class, u)) {
                continue;
            }

            Player player = getPlugin().getServer().getPlayer(u);
            if (u == null) continue;

            if (!isTagged(player)) {
                untag.add(player);
            }
        }

        for (Player p : untag) {
            taggedPlayers.remove(p.getUniqueId());
            getPlugin().getServer().getPluginManager().callEvent(new PlayerLeaveCombatEvent(p));
        }
    }
}
