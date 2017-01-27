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

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.events.PlayerEnterRegionEvent;
import me.lucko.conditionalperms.events.PlayerLeaveRegionEvent;
import me.lucko.conditionalperms.hooks.AbstractHook;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WorldGuardHook extends AbstractHook {
    private WorldGuardPlugin worldGuard;
    private Map<UUID, Set<String>> regions = new HashMap<>();

    WorldGuardHook(ConditionalPerms plugin) {
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

        if (!shouldCheck(getClass(), e.getPlayer().getUniqueId())) {
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
