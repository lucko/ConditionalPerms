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

package me.lucko.conditionalperms.conditions.bukkit;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.helper.Events;
import me.lucko.helper.utils.Terminable;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.util.function.Consumer;

public class InGamemode extends AbstractCondition {
    public InGamemode() {
        super(true);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        try {
            GameMode g = GameMode.valueOf(parameter.toUpperCase());
            return player.getGameMode() == g;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void bind(Consumer<Terminable> consumer) {
        Events.subscribe(PlayerGameModeChangeEvent.class)
                .handler(e -> getPlugin().refreshPlayer(e.getPlayer(), 1L))
                .register(consumer);
    }
}
