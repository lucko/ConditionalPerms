package me.lucko.conditionalperms.conditions.bukkit;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class IsFlying extends AbstractCondition {
    public IsFlying() {
        super(false);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return player.isFlying();
    }

    @EventHandler
    public void onFlyToggle(PlayerToggleFlightEvent e) {
        getPlugin().refreshPlayerDelay(1L, e.getPlayer());
    }
}
