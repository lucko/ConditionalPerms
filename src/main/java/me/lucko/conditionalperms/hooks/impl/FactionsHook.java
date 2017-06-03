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
import me.lucko.conditionalperms.events.PlayerFactionsRegionChangeEvent;
import me.lucko.conditionalperms.hooks.AbstractHook;
import me.lucko.conditionalperms.utils.FactionsRegion;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.Terminable;
import me.markeh.factionsframework.FactionsFramework;
import me.markeh.factionsframework.entities.FPlayer;
import me.markeh.factionsframework.entities.FPlayers;
import me.markeh.factionsframework.entities.Faction;
import me.markeh.factionsframework.entities.Factions;
import me.markeh.factionsframework.enums.Rel;
import me.markeh.factionsframework.layer.EventsLayer;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class FactionsHook extends AbstractHook {
    private Map<UUID, FactionsRegion> regions = new HashMap<>();

    FactionsHook(ConditionalPerms plugin) {
        super(plugin);

        FactionsFramework.load(getPlugin());
        // We don't need events
        HandlerList.unregisterAll(EventsLayer.get());
        FactionsFramework.get();
    }

    @Override
    public void bind(Consumer<Terminable> consumer) {
        consumer.accept(() -> {
            FactionsFramework.get().stop();
            return true;
        });

        Events.subscribe(PlayerLoginEvent.class)
                .handler(e -> regions.put(e.getPlayer().getUniqueId(), getRegion(e.getPlayer())))
                .register(consumer);

        Events.subscribe(PlayerQuitEvent.class)
                .handler(e -> regions.remove(e.getPlayer().getUniqueId()))
                .register(consumer);

        Events.subscribe(PlayerMoveEvent.class)
                .filter(Events.DEFAULT_FILTERS.ignoreSameChunk())
                .filter(e -> shouldCheck(FactionsHook.class, e.getPlayer().getUniqueId()))
                .handler(e -> {
                    FactionsRegion from = regions.get(e.getPlayer().getUniqueId());
                    FactionsRegion to = getRegion(e.getPlayer());

                    if (from.equals(to)) {
                        return;
                    }

                    getPlugin().getServer().getPluginManager().callEvent(new PlayerFactionsRegionChangeEvent(e.getPlayer(), from, to));
                    regions.put(e.getPlayer().getUniqueId(), to);
                })
                .register(consumer);
    }

    public FactionsRegion getRegion(Player player) {
        final FPlayer p = FPlayers.getBySender(player);
        if (p == null) return FactionsRegion.NONE;

        Faction factionAt = p.getFactionAt();
        if (factionAt == null) return FactionsRegion.NONE;
        if (factionAt.isNone()) return FactionsRegion.NONE;
        if (factionAt.getId().equals(p.getFaction().getId())) return FactionsRegion.OWN;
        if (factionAt.getId().equals(Factions.getWarZone(player.getWorld()).getId())) return FactionsRegion.WARZONE;
        if (factionAt.getId().equals(Factions.getSafeZone(player.getWorld()).getId())) return FactionsRegion.SAFEZONE;

        Rel rel = factionAt.getRelationTo(p.getFaction());
        if (rel == Rel.ALLY) return FactionsRegion.ALLY;
        if (rel == Rel.ENEMY) return FactionsRegion.ENEMY;
        if (rel == Rel.TRUCE) return FactionsRegion.TRUCE;
        if (rel == Rel.NEUTRAL) return FactionsRegion.NEUTRAL;

        return FactionsRegion.NONE;
    }
}
