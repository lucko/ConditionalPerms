package me.lucko.conditionalperms.conditions.plotsquared;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.events.PlayerEnterPlotEvent;
import me.lucko.conditionalperms.events.PlayerLeavePlotEvent;
import me.lucko.conditionalperms.hooks.impl.PlotSquaredHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InPlot extends AbstractCondition {
    public InPlot() {
        super(false);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return getPlugin().getHookManager().isHooked(PlotSquaredHook.class) &&
                getPlugin().getHookManager().get(PlotSquaredHook.class).isInPlot(player);
    }

    @EventHandler
    public void onPlotEnter(PlayerEnterPlotEvent e) {
        getPlugin().refreshPlayerDelay(5L, e.getPlayer());
    }

    @EventHandler
    public void onPlotLeave(PlayerLeavePlotEvent e) {
        getPlugin().refreshPlayerDelay(5L, e.getPlayer());
    }
}
