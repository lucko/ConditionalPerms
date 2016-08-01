package me.lucko.conditionalperms.hooks;

import lombok.RequiredArgsConstructor;
import me.lucko.conditionalperms.ConditionalPerms;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HookManager {
    private final Plugin plugin;
    private final Map<Class<? extends AbstractHook>, AbstractHook> hooks = new HashMap<>();

    public void init() {
        final PluginManager pm = plugin.getServer().getPluginManager();

        for (Hook hook : Hook.values()) {
            try {
                if (pm.isPluginEnabled(hook.getPluginName())) {
                    AbstractHook ah = make(hook.getClazz(), plugin);
                    if (ah != null) {
                        ah.init();
                        plugin.getServer().getPluginManager().registerEvents(ah, plugin);
                        hooks.put(hook.getClazz(), ah);
                    }

                    plugin.getLogger().info("Hooked with " + hook.getPluginName() + "...");
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Exception thrown whilst hooking with " + hook.getPluginName() + "...");
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        for (AbstractHook ah : hooks.values()) {
            ah.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractHook> T get(Class<T> c) {
        if (!isHooked(c)) return null;
        return (T) hooks.get(c);
    }

    public <T extends AbstractHook> boolean isHooked(Class<T> c) {
        return hooks.containsKey(c);
    }

    private static AbstractHook make(Class<? extends AbstractHook> clazz, Plugin plugin) throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor(ConditionalPerms.class);
        constructor.setAccessible(true);
        return (AbstractHook) constructor.newInstance(plugin);
    }
}
