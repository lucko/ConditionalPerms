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

import com.wasteofplastic.acidisland.ASkyBlock;
import com.wasteofplastic.acidisland.Island;
import com.wasteofplastic.acidisland.events.ASkyBlockEvent;
import com.wasteofplastic.acidisland.events.IslandEnterEvent;
import com.wasteofplastic.acidisland.events.IslandExitEvent;
import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.hooks.AbstractHook;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class AcidIslandHook extends AbstractHook {

    private final ASkyBlock acidisland;

    AcidIslandHook(ConditionalPerms plugin) {
        super(plugin);
        acidisland = (ASkyBlock) getPlugin().getServer().getPluginManager().getPlugin("AcidIsland");
    }

    public boolean isInIsland(Player player) {
        Island island = acidisland.getGrid().getIslandAt(player.getLocation());
        return island != null;
    }

    public boolean isInOwnIsland(Player player) {
        Island island = acidisland.getGrid().getIslandAt(player.getLocation());
        return island != null && island.getOwner().equals(player.getUniqueId());
    }

    public boolean isIslandMember(Player player) {
        Island island = acidisland.getGrid().getIslandAt(player.getLocation());
        return island != null && island.getMembers().contains(player.getUniqueId());
    }

    @Override
    public void setup(TerminableConsumer consumer) {
        Events.merge(ASkyBlockEvent.class, IslandEnterEvent.class, IslandExitEvent.class)
                .handler(e -> getPlugin().refreshPlayer(Bukkit.getPlayer(e.getPlayer()), 1L))
                .bindWith(consumer);
    }
}
