/*
 * Copyright (c) 2017 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
