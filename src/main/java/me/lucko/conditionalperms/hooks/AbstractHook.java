package me.lucko.conditionalperms.hooks;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.conditionalperms.ConditionalPerms;
import org.bukkit.event.Listener;

import java.util.UUID;

@AllArgsConstructor
public abstract class AbstractHook implements Listener {

    @Getter(AccessLevel.PROTECTED)
    private final ConditionalPerms plugin;

    public void init() {

    }

    public void shutdown() {

    }

    protected boolean shouldCheck(Class<? extends AbstractHook> clazz, UUID u) {
        return !(!plugin.getNeededHooks().containsKey(u) || !plugin.getNeededHooks().get(u).contains(clazz));
    }

}
