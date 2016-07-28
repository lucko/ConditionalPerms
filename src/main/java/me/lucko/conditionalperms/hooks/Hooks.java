package me.lucko.conditionalperms.hooks;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Hooks {
    private final Plugin plugin;

    @Getter
    private WorldGuardHook worldGuardHook = null;

    @Getter
    private FactionsHook factionsHook = null;

    @Getter
    private CombatTagPlusHook combatTagPlusHook = null;

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
            plugin.getLogger().severe("Exception thrown whilst hooking with WorldGuard...");
            e.printStackTrace();
            worldGuardHook = null;
        }

        // Hook Factions
        try {
            if (pm.isPluginEnabled("Factions")) {
                factionsHook = new FactionsHook(plugin);
                plugin.getLogger().info("Hooked with Factions...");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Exception thrown whilst hooking with Factions...");
            e.printStackTrace();
            factionsHook = null;
        }

        // Hook CombatTagPlus
        try {
            if (pm.isPluginEnabled("CombatTagPlus")) {
                combatTagPlusHook = new CombatTagPlusHook(plugin);
                plugin.getLogger().info("Hooked with CombatTagPlus...");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Exception thrown whilst hooking with CombatTagPlus...");
            e.printStackTrace();
            combatTagPlusHook = null;
        }

    }

    public void shutdown() {
        // Unhook Factions
        try {
            if (factionsHook != null) {
                factionsHook.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
