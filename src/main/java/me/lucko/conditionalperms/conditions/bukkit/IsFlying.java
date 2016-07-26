package me.lucko.conditionalperms.conditions.bukkit;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.ICondition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class IsFlying implements ICondition {
    private ConditionalPerms plugin;

    @Override
    public void init(ConditionalPerms plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return player.isFlying();
    }

    @Override
    public boolean needsParameter() {
        return false;
    }

    @EventHandler
    public void onFlyToggle(PlayerToggleFlightEvent e) {
        plugin.refreshPlayerDelay(1L, e.getPlayer());
    }
}
