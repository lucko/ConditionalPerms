package me.lucko.conditionalperms.hooks;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Hooks {
    private final Plugin plugin;

    @Getter
    private WorldGuardHook worldGuardHook = null;

    public Hooks(Plugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        final PluginManager pm = plugin.getServer().getPluginManager();

        // Hook WorldGuard
        try {
            if (pm.isPluginEnabled("WorldGuard")) {
                worldGuardHook = new WorldGuardHook(plugin);
                plugin.getLogger().info("Hooked with WorldGuard...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            worldGuardHook = null;
        }

    }
}
