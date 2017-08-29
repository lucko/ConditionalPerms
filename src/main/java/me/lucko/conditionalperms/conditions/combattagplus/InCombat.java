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

package me.lucko.conditionalperms.conditions.combattagplus;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.events.PlayerEnterCombatEvent;
import me.lucko.conditionalperms.events.PlayerLeaveCombatEvent;
import me.lucko.conditionalperms.hooks.impl.CombatTagPlusHook;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.TerminableConsumer;

import org.bukkit.entity.Player;

public class InCombat extends AbstractCondition {
    public InCombat() {
        super(false, CombatTagPlusHook.class);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return getPlugin().getHookManager().get(CombatTagPlusHook.class).isTagged(player);
    }

    @Override
    public void setup(TerminableConsumer consumer) {
        Events.subscribe(PlayerEnterCombatEvent.class)
                .handler(e -> {
                    if (e.getVictim() != null) {
                        getPlugin().refreshPlayer(e.getVictim(), 1L);
                    }
                    if (e.getAttacker() != null) {
                        getPlugin().refreshPlayer(e.getAttacker(), 1L);
                    }
                })
                .bindWith(consumer);

        Events.subscribe(PlayerLeaveCombatEvent.class)
                .handler(e -> getPlugin().refreshPlayer(e.getPlayer(), 1L))
                .bindWith(consumer);
    }
}
