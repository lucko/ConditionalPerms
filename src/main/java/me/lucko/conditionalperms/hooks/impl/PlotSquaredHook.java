package me.lucko.conditionalperms.hooks.impl;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import me.lucko.conditionalperms.hooks.AbstractHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

public class PlotSquaredHook extends AbstractHook {
    PlotSquaredHook(Plugin plugin) {
        super(plugin);
    }

    public boolean isInPlot(Player player) {
        PlotPlayer pp = PlotPlayer.wrap(player);
        final Plot plot = pp.getCurrentPlot();
        return plot != null;
    }

    public boolean isInOwnPlot(Player player) {
        PlotPlayer pp = PlotPlayer.wrap(player);
        final Plot plot = pp.getCurrentPlot();
        return plot != null && plot.isOwner(pp.getUUID());
    }

    /*
     * Pass on PlotSquared events if the hook is enabled.
     */
    @EventHandler
    public void onPlayerLeavePlot(PlayerLeavePlotEvent e) {
        Bukkit.getPluginManager().callEvent(new me.lucko.conditionalperms.events.PlayerLeavePlotEvent(e.getPlayer()));
    }

    @EventHandler
    public void onPlayerEnterPlotEvent(PlayerEnterPlotEvent e) {
        Bukkit.getPluginManager().callEvent(new me.lucko.conditionalperms.events.PlayerEnterPlotEvent(e.getPlayer()));
    }
}
