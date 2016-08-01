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
