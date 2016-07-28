package me.lucko.conditionalperms.conditions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lucko.conditionalperms.ConditionalPerms;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public abstract class AbstractCondition implements Listener {

    @Getter(AccessLevel.PROTECTED)
    private ConditionalPerms plugin = null;

    @Getter
    private final boolean parameterNeeded;

    public void init(ConditionalPerms plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public abstract boolean shouldApply(Player player, String parameter);

}
