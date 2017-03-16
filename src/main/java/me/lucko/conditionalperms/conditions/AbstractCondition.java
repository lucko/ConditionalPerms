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

package me.lucko.conditionalperms.conditions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import me.lucko.conditionalperms.ConditionalPerms;
import me.lucko.conditionalperms.hooks.AbstractHook;
import me.lucko.helper.utils.CompositeTerminable;

import org.bukkit.entity.Player;

@RequiredArgsConstructor
public abstract class AbstractCondition implements CompositeTerminable {

    @Getter(AccessLevel.PROTECTED)
    private ConditionalPerms plugin = null;

    @Getter
    private final boolean parameterNeeded;

    @Getter
    private Class<? extends AbstractHook> neededHook = null;

    public AbstractCondition(boolean parameterNeeded, Class<? extends AbstractHook> neededHook) {
        this(parameterNeeded);
        this.neededHook = neededHook;
    }

    public void init(ConditionalPerms plugin) {
        this.plugin = plugin;
        plugin.bindTerminable(this);
    }

    public abstract boolean shouldApply(Player player, String parameter);

    public boolean isHookNeeded() {
        return neededHook != null;
    }

}
