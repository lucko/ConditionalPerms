package me.lucko.conditionalperms.hooks.impl;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.hooks.AbstractHook;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends AbstractHook {
    public PlaceholderAPIHook(ConditionalPerms plugin) {
        super(plugin);
        getPlugin().getServer().getScheduler().runTaskTimer(getPlugin(), new UpdateTask(), 0L, 600L);
    }

    public boolean getResult(String placeholder, Player player) {
        getPlugin().debug("p:" + placeholder);
        String result = PlaceholderAPI.setPlaceholders(player, placeholder);

        getPlugin().debug("r:" + result);

        if (result.equalsIgnoreCase(placeholder)) {
            throw new IllegalArgumentException("Placeholder does not exist");
        }

        if (result.equalsIgnoreCase(PlaceholderAPIPlugin.booleanTrue())) {
            result = "true";
        }
        if (result.equalsIgnoreCase(PlaceholderAPIPlugin.booleanFalse())) {
            result = "false";
        }

        if (!result.equalsIgnoreCase("true") && !result.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("Result of placeholder is not a boolean");
        }

        return Boolean.parseBoolean(result);
    }

    // Not ideal, but there is obviously no events for placeholder output changing
    private class UpdateTask implements Runnable {

        @Override
        public void run() {
            for (Player p : getPlugin().getServer().getOnlinePlayers()) {
                if (!shouldCheck(PlaceholderAPIHook.class, p.getUniqueId())) {
                    continue;
                }

                getPlugin().refreshPlayer(p);
            }
        }
    }
}
