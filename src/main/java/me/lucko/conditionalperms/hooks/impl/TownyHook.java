package me.lucko.conditionalperms.hooks.impl;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.PlayerCache;
import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.events.PlayerTownyRegionChangeEvent;
import me.lucko.conditionalperms.hooks.AbstractHook;
import me.lucko.conditionalperms.utils.TownyRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TownyHook extends AbstractHook {
    private Towny towny = null;
    private Map<UUID, TownyRegion> regions = new HashMap<>();

    public TownyHook(ConditionalPerms plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        towny = (Towny) getPlugin().getServer().getPluginManager().getPlugin("Towny");
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
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
            return;
        }

        if (!shouldCheck(getClass(), e.getPlayer().getUniqueId())) {
            return;
        }

        final TownyRegion from = regions.get(e.getPlayer().getUniqueId());
        final TownyRegion to = getRegion(e.getPlayer());

        if (from == null || to == null || from.equals(to)) {
            return;
        }

        getPlugin().getServer().getPluginManager().callEvent(new PlayerTownyRegionChangeEvent(e.getPlayer(), from, to));
        regions.put(e.getPlayer().getUniqueId(), to);
    }

    public TownyRegion getRegion(Player player) {
        try {
            PlayerCache.TownBlockStatus status = towny.getCache(player).getStatus();

            switch (status) {
                case ADMIN:
                    return TownyRegion.ADMIN;
                case ENEMY:
                    return TownyRegion.ENEMY;
                case LOCKED:
                    return TownyRegion.LOCKED;
                case NOT_REGISTERED:
                    return TownyRegion.NOT_REGISTERED;
                case OFF_WORLD:
                    return TownyRegion.OFF_WORLD;
                case OUTSIDER:
                    return TownyRegion.OUTSIDER;
                case PLOT_ALLY:
                    return TownyRegion.PLOT_ALLY;
                case PLOT_FRIEND:
                    return TownyRegion.PLOT_FRIEND;
                case PLOT_OWNER:
                    return TownyRegion.PLOT_OWNER;
                case TOWN_ALLY:
                    return TownyRegion.TOWN_ALLY;
                case TOWN_OWNER:
                    return TownyRegion.TOWN_OWNER;
                case TOWN_RESIDENT:
                    return TownyRegion.TOWN_RESIDENT;
                case UNCLAIMED_ZONE:
                    return TownyRegion.UNCLAIMED;
                case WARZONE:
                    return TownyRegion.WARZONE;
                default:
                    return null;
            }

        } catch (NullPointerException e) {
            return null;
        }
    }
}
