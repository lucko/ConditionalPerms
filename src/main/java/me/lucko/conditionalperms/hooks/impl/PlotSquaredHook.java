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

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.plotsquared.bukkit.events.PlayerLeavePlotEvent;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.hooks.AbstractHook;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PlotSquaredHook extends AbstractHook {
    PlotSquaredHook(ConditionalPerms plugin) {
        super(plugin);
    }

    public boolean isInPlot(Player player) {
        final PlotPlayer pp = PlotPlayer.wrap(player);
        final Plot plot = pp.getCurrentPlot();
        return plot != null;
    }

    public boolean isInOwnPlot(Player player) {
        final PlotPlayer pp = PlotPlayer.wrap(player);
        final Plot plot = pp.getCurrentPlot();
        return plot != null && plot.isOwner(pp.getUUID());
    }

    /*
     * Pass on PlotSquared events if the hook is enabled.
     */
    @EventHandler
    public void onPlayerLeavePlot(PlayerLeavePlotEvent e) {
        if (!shouldCheck(getClass(), e.getPlayer().getUniqueId())) {
            return;
        }

        getPlugin().getServer().getPluginManager()
                .callEvent(new me.lucko.conditionalperms.events.PlayerLeavePlotEvent(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerEnterPlotEvent(PlayerEnterPlotEvent e) {
        if (!shouldCheck(getClass(), e.getPlayer().getUniqueId())) {
            return;
        }

        getPlugin().getServer().getPluginManager()
                .callEvent(new me.lucko.conditionalperms.events.PlayerEnterPlotEvent(e.getPlayer()));
    }
}
