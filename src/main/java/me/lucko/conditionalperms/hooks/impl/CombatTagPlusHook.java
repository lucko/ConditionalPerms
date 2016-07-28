package me.lucko.conditionalperms.hooks.impl;

import me.lucko.conditionalperms.events.PlayerEnterCombatEvent;
import me.lucko.conditionalperms.events.PlayerLeaveCombatEvent;
import me.lucko.conditionalperms.hooks.AbstractHook;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CombatTagPlusHook extends AbstractHook {
    private TagManager manager;
    private final Set<UUID> taggedPlayers = new HashSet<>();

    CombatTagPlusHook(Plugin plugin) {
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
            taggedPlayers.add(e.getVictim().getUniqueId());
        }
        if (e.getAttacker() != null) {
            taggedPlayers.add(e.getAttacker().getUniqueId());
        }

        // Pass on CombatTagPlus events if the hook is enabled.
        Bukkit.getPluginManager().callEvent(new PlayerEnterCombatEvent(e.getPlayer(), e.getVictim(), e.getAttacker()));
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
                Player player = Bukkit.getPlayer(u);
                if (u == null) continue;

                if (!isTagged(player)) {
                    untag.add(player);
                }
            }

            for (Player p : untag) {
                taggedPlayers.remove(p.getUniqueId());
                Bukkit.getPluginManager().callEvent(new PlayerLeaveCombatEvent(p));
            }
        }
    }
}
