package me.lucko.conditionalperms.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerEnterCombatEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final Player victim;

    @Getter
    private final Player attacker;

    public PlayerEnterCombatEvent(Player who, Player victim, Player attacker) {
        super(who);
        this.victim = victim;
        this.attacker = attacker;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
