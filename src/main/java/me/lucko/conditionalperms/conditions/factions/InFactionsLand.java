package me.lucko.conditionalperms.conditions.factions;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.ICondition;
import me.lucko.conditionalperms.events.FactionsRegionChangeEvent;
import me.lucko.conditionalperms.hooks.FactionsHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InFactionsLand implements ICondition {
    private ConditionalPerms plugin;
    private final FactionsHook.FactionsRegion r;

    public InFactionsLand(FactionsHook.FactionsRegion r) {
        this.r = r;
    }

    @Override
    public void init(ConditionalPerms plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return plugin.getHooks().getFactionsHook() != null &&
                plugin.getHooks().getFactionsHook().getRegion(player).equals(r);
    }

    @Override
    public boolean needsParameter() {
        return false;
    }

    @EventHandler
    public void onRegionChange(FactionsRegionChangeEvent e) {
        plugin.refreshPlayerDelay(1L, e.getPlayer());
    }
}