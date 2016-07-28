package me.lucko.conditionalperms.hooks.impl;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.lucko.conditionalperms.events.PlayerEnterRegionEvent;
import me.lucko.conditionalperms.events.PlayerLeaveRegionEvent;
import me.lucko.conditionalperms.hooks.AbstractHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class WorldGuardHook extends AbstractHook {
    private WorldGuardPlugin worldGuard;
    private Map<UUID, Set<String>> regions = new HashMap<>();

    public WorldGuardHook(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void init() {
        worldGuard = (WorldGuardPlugin) getPlugin().getServer().getPluginManager().getPlugin("WorldGuard");
    }

    public Set<String> getRegions(Player player) {
        final Set<String> r = new HashSet<>();
        for (String s : regions.get(player.getUniqueId())) {
            r.add(s.toLowerCase());
        }
        return r;
    }

    private Set<String> getRegions(Location location) {
        final RegionContainer container = worldGuard.getRegionContainer();
        final RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(location);

        if (set.size() == 0) {
            return Collections.emptySet();
        }

        final Set<String> regions = new HashSet<>();
        for (ProtectedRegion r : set.getRegions()) {
            regions.add(r.getId());
        }

        return regions;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        regions.put(e.getPlayer().getUniqueId(), new HashSet<>(getRegions(e.getPlayer().getLocation())));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        regions.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
                e.getFrom().getBlockY() == e.getTo().getBlockY() &&
                e.getFrom().getBlockZ() == e.getTo().getBlockZ()) {
            return;
        }

        final Set<String> previouslyIn = regions.get(e.getPlayer().getUniqueId());
        final Set<String> now = getRegions(e.getPlayer().getLocation());

        for (String s : previouslyIn) {
            if (!now.contains(s)) {
                // Fire RegionLeaveEvent
                worldGuard.getServer().getPluginManager().callEvent(new PlayerLeaveRegionEvent(e.getPlayer(), s));
            }
        }

        for (String s : now) {
            if (!previouslyIn.contains(s)) {
                // Fire RegionEnterEvent
                worldGuard.getServer().getPluginManager().callEvent(new PlayerEnterRegionEvent(e.getPlayer(), s));
            }
        }

        previouslyIn.clear();
        previouslyIn.addAll(now);
    }
}
