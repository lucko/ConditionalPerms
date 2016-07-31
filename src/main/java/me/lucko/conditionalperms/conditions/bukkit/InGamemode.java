package me.lucko.conditionalperms.conditions.bukkit;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class InGamemode extends AbstractCondition {
    public InGamemode() {
        super(true);
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

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
        getPlugin().refreshPlayer(e.getPlayer(), 1L);
    }
}
