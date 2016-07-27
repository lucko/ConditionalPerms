package me.lucko.conditionalperms.hooks;

import me.lucko.conditionalperms.events.FactionsRegionChangeEvent;
import me.markeh.factionsframework.FactionsFramework;
import me.markeh.factionsframework.entities.FPlayer;
import me.markeh.factionsframework.entities.FPlayers;
import me.markeh.factionsframework.entities.Faction;
import me.markeh.factionsframework.entities.Factions;
import me.markeh.factionsframework.enums.Rel;
import me.markeh.factionsframework.layer.EventsLayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FactionsHook implements Listener {
    private final Plugin plugin;
    private Map<UUID, FactionsHook.FactionsRegion> regions = new HashMap<>();

    FactionsHook(Plugin plugin) {
        this.plugin = plugin;

        FactionsFramework.load(plugin);
        // We don't need events
        HandlerList.unregisterAll(EventsLayer.get());
        FactionsFramework.get();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public FactionsRegion getRegion(Player player) {
        final FPlayer p = FPlayers.getBySender(player);
        if (p == null) return null;

        final Faction factionAt = p.getFactionAt();
        if (factionAt == null) {
            return FactionsRegion.NONE;
        }

        if (factionAt.isNone()) {
            return FactionsRegion.NONE;
        }

        if (factionAt.getId().equals(p.getFaction().getId())) {
            return FactionsRegion.OWN;
        }

        if (factionAt.getId().equals(Factions.getWarZone(player.getWorld()).getId())) {
            return FactionsRegion.WARZONE;
        }

        if (factionAt.getId().equals(Factions.getSafeZone(player.getWorld()).getId())) {
            return FactionsRegion.SAFEZONE;
        }

        final Rel rel = factionAt.getRelationTo(p.getFaction());

        if (rel == Rel.ALLY) {
            return FactionsRegion.ALLY;
        }

        if (rel == Rel.ENEMY) {
            return FactionsRegion.ENEMY;
        }

        if (rel == Rel.TRUCE) {
            return FactionsRegion.TRUCE;
        }

        if (rel == Rel.NEUTRAL) {
            return FactionsRegion.NEUTRAL;
        }

        return null;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        regions.put(e.getPlayer().getUniqueId(), getRegion(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        regions.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getChunk().getX() == e.getTo().getChunk().getX() &&
                e.getFrom().getChunk().getZ() == e.getTo().getChunk().getZ()) {
            return;
        }

        final FactionsHook.FactionsRegion from = regions.get(e.getPlayer().getUniqueId());
        final FactionsHook.FactionsRegion to = getRegion(e.getPlayer());

        if (from.equals(to)) {
            return;
        }

        plugin.getServer().getPluginManager().callEvent(new FactionsRegionChangeEvent(e.getPlayer(), from, to));
        regions.put(e.getPlayer().getUniqueId(), to);
    }

    public enum FactionsRegion {
        NONE, // Wilderness
        WARZONE,
        SAFEZONE,
        ALLY, // In another factions land (that they're an ally to)
        NEUTRAL, // In another factions land (that they're neutral to)
        ENEMY, // In another factions land (that they're an enemy to)
        TRUCE, // In another factions land (that they're in a truce to)
        OWN // In their own factions land
    }

}
