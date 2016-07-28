package me.lucko.conditionalperms.hooks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public abstract class AbstractHook implements Listener {

    @Getter(AccessLevel.PROTECTED)
    private final Plugin plugin;

    public void init() {

    }

    public void shutdown() {

    }

}
