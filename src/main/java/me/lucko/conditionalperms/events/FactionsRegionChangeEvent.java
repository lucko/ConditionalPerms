package me.lucko.conditionalperms.events;

import lombok.Getter;
import me.lucko.conditionalperms.hooks.FactionsHook;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class FactionsRegionChangeEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final FactionsHook.FactionsRegion from;

    @Getter
    private final FactionsHook.FactionsRegion to;

    public FactionsRegionChangeEvent(Player who, FactionsHook.FactionsRegion from, FactionsHook.FactionsRegion to) {
        super(who);
        this.from = from;
        this.to = to;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
