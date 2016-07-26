package me.lucko.conditionalperms;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface ICondition extends Listener {

    void init(ConditionalPerms plugin);
    boolean shouldApply(Player player, String parameter);
    boolean needsParameter();

}
