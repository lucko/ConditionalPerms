package me.lucko.conditionalperms.conditions.worldguard;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.events.WorldGuardRegionEnterEvent;
import me.lucko.conditionalperms.events.WorldGuardRegionLeaveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InRegion extends AbstractCondition {
    public InRegion() {
        super(true);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return getPlugin().getHooks().getWorldGuardHook() != null &&
                getPlugin().getHooks().getWorldGuardHook().getRegions(player).contains(parameter.toLowerCase());
    }

    @EventHandler
    public void onRegionEnter(WorldGuardRegionEnterEvent e) {
        getPlugin().refreshPlayerDelay(1L, e.getPlayer());
    }

    @EventHandler
    public void onRegionLeave(WorldGuardRegionLeaveEvent e) {
        getPlugin().refreshPlayerDelay(1L, e.getPlayer());
    }
}
