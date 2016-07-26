package me.lucko.conditionalperms.conditions.bukkit;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.ICondition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class InWorld implements ICondition {
    private ConditionalPerms plugin;

    @Override
    public void init(ConditionalPerms plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return player.getWorld().getName().equalsIgnoreCase(parameter);
    }

    @Override
    public boolean needsParameter() {
        return true;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        plugin.refreshPlayer(e.getPlayer());
    }
}
