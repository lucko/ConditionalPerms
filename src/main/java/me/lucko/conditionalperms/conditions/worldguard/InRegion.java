package me.lucko.conditionalperms.conditions.worldguard;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.ICondition;
import me.lucko.conditionalperms.events.WorldGuardRegionEnterEvent;
import me.lucko.conditionalperms.events.WorldGuardRegionLeaveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InRegion implements ICondition {
    private ConditionalPerms plugin;

    @Override
    public void init(ConditionalPerms plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return plugin.getHooks().getWorldGuardHook() != null &&
                plugin.getHooks().getWorldGuardHook().getRegions(player).contains(parameter.toLowerCase());
    }

    @Override
    public boolean needsParameter() {
        return true;
    }

    @EventHandler
    public void onRegionEnter(WorldGuardRegionEnterEvent e) {
        plugin.refreshPlayerDelay(1L, e.getPlayer());
    }

    @EventHandler
    public void onRegionLeave(WorldGuardRegionLeaveEvent e) {
        plugin.refreshPlayerDelay(1L, e.getPlayer());
    }
}
