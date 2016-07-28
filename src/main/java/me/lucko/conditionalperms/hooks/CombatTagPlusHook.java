package me.lucko.conditionalperms.hooks;

import me.lucko.conditionalperms.events.PlayerLeaveCombatEvent;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;
import net.minelink.ctplus.event.PlayerCombatTagEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CombatTagPlusHook implements Listener {

    private CombatTagPlus combatTagPlus;
    private Set<UUID> taggedPlayers = new HashSet<>();

    CombatTagPlusHook(Plugin plugin) {
        combatTagPlus = (CombatTagPlus) plugin.getServer().getPluginManager().getPlugin("CombatTagPlus");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, new CheckTagTask(), 0L, 20L);
    }

    public boolean isTagged(Player player) {
        final TagManager manager = combatTagPlus.getTagManager();
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
                combatTagPlus.getServer().getPluginManager().callEvent(new PlayerLeaveCombatEvent(p));
            }
        }
    }
}
