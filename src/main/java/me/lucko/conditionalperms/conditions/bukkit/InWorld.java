package me.lucko.conditionalperms.conditions.bukkit;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class InWorld extends AbstractCondition {
    public InWorld() {
        super(true);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return player.getWorld().getName().equalsIgnoreCase(parameter);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        getPlugin().refreshPlayer(e.getPlayer());
    }
}
