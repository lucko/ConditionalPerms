package me.lucko.conditionalperms.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class WorldGuardRegionLeaveEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final String region;

    public WorldGuardRegionLeaveEvent(Player who, String region) {
        super(who);
        this.region = region;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
