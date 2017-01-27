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

import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;
import net.minelink.ctplus.event.PlayerCombatTagEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CombatTagPlusHook extends AbstractHook {
    private final Set<UUID> taggedPlayers = new HashSet<>();
    private TagManager manager;

    CombatTagPlusHook(ConditionalPerms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        CombatTagPlus combatTagPlus = (CombatTagPlus) getPlugin().getServer().getPluginManager().getPlugin("CombatTagPlus");
        manager = combatTagPlus.getTagManager();
        getPlugin().getServer().getScheduler().runTaskTimer(getPlugin(), new CheckTagTask(), 0L, 20L);
    }

    public boolean isTagged(Player player) {
        return manager.isTagged(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerCombatTag(PlayerCombatTagEvent e) {
        if (e.getVictim() != null) {
            if (shouldCheck(getClass(), e.getVictim().getUniqueId())) {
                taggedPlayers.add(e.getVictim().getUniqueId());
            }
        }
        if (e.getAttacker() != null) {
            if (shouldCheck(getClass(), e.getVictim().getUniqueId())) {
                taggedPlayers.add(e.getVictim().getUniqueId());
            }
        }

        // Pass on CombatTagPlus events if the hook is enabled.
        getPlugin().getServer().getPluginManager()
                .callEvent(new PlayerEnterCombatEvent(e.getPlayer(), e.getVictim(), e.getAttacker()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        taggedPlayers.remove(e.getPlayer().getUniqueId());
    }

    // Not ideal, but there is no event for players leaving combat
    private class CheckTagTask implements Runnable {

        @Override
        public void run() {
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
}
