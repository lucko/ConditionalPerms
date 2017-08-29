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

package me.lucko.conditionalperms.hooks;

import lombok.RequiredArgsConstructor;

import me.lucko.conditionalperms.ConditionalPerms;

import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class HookManager {
    private final ConditionalPerms plugin;
    private final Map<Class<? extends AbstractHook>, AbstractHook> hooks = new HashMap<>();

    public void init() {
        PluginManager pm = plugin.getServer().getPluginManager();

        for (Hook hook : Hook.values()) {
            try {
                if (pm.isPluginEnabled(hook.getPluginName())) {
                    AbstractHook ah = make(hook.getClazz(), plugin);
                    if (ah != null) {
                        plugin.bindComposite(ah);
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

    public <T extends AbstractHook> T get(Class<T> c) {
        AbstractHook ret = hooks.get(c);
        return ret == null ? null : c.cast(ret);
    }

    public boolean isHooked(Class<? extends AbstractHook> c) {
        return hooks.containsKey(c);
    }

    private static AbstractHook make(Class<? extends AbstractHook> clazz, ConditionalPerms plugin) throws Exception {
        Constructor constructor = clazz.getDeclaredConstructor(ConditionalPerms.class);
        constructor.setAccessible(true);
        return (AbstractHook) constructor.newInstance(plugin);
    }
}
