package me.lucko.conditionalperms.conditions.factions;

import me.lucko.conditionalperms.AbstractCondition;
import me.lucko.conditionalperms.events.FactionsRegionChangeEvent;
import me.lucko.conditionalperms.hooks.FactionsHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InFactionsLand extends AbstractCondition {
    private final FactionsHook.FactionsRegion r;

    public InFactionsLand(FactionsHook.FactionsRegion r) {
        super(false);
        this.r = r;
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return getPlugin().getHooks().getFactionsHook() != null &&
                getPlugin().getHooks().getFactionsHook().getRegion(player).equals(r);
    }

    @EventHandler
    public void onRegionChange(FactionsRegionChangeEvent e) {
        getPlugin().refreshPlayerDelay(5L, e.getPlayer());
    }
}