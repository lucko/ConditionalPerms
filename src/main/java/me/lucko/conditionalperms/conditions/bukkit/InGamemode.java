package me.lucko.conditionalperms.conditions.bukkit;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.ICondition;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class InGamemode implements ICondition {
    private ConditionalPerms plugin;

    @Override
    public void init(ConditionalPerms plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        try {
            GameMode g = GameMode.valueOf(parameter.toUpperCase());
            return player.getGameMode().equals(g);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public boolean needsParameter() {
        return true;
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        plugin.refreshPlayerDelay(1L, e.getPlayer());
    }
}
