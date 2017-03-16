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

package me.lucko.conditionalperms.conditions.towny;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.events.PlayerTownyRegionChangeEvent;
import me.lucko.conditionalperms.hooks.impl.TownyHook;
import me.lucko.conditionalperms.utils.TownyRegion;
import me.lucko.helper.Events;
import me.lucko.helper.utils.Terminable;

import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class InTownyRegion extends AbstractCondition {
    private final TownyRegion r;

    public InTownyRegion(TownyRegion r) {
        super(false, TownyHook.class);
        this.r = r;
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        TownyRegion region = getPlugin().getHookManager().get(TownyHook.class).getRegion(player);
        return region != null && region.equals(r);
    }

    @Override
    public void bind(Consumer<Terminable> consumer) {
        Events.subscribe(PlayerTownyRegionChangeEvent.class)
                .handler(e -> getPlugin().refreshPlayer(e.getPlayer(), 5L))
                .register(consumer);
    }
}
