package me.lucko.conditionalperms.conditions.combattagplus;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.events.PlayerEnterCombatEvent;
import me.lucko.conditionalperms.events.PlayerLeaveCombatEvent;
import me.lucko.conditionalperms.hooks.impl.CombatTagPlusHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InCombat extends AbstractCondition {
    public InCombat() {
        super(false);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return getPlugin().getHookManager().isHooked(CombatTagPlusHook.class) &&
                getPlugin().getHookManager().get(CombatTagPlusHook.class).isTagged(player);
    }

    @EventHandler
    public void onTag(PlayerEnterCombatEvent e) {
        if (e.getVictim() != null) {
            getPlugin().refreshPlayerDelay(1L, e.getVictim());
        }
        if (e.getAttacker() != null) {
            getPlugin().refreshPlayerDelay(1L, e.getAttacker());
        }
    }

    @EventHandler
    public void onUnTag(PlayerLeaveCombatEvent e) {
        getPlugin().refreshPlayerDelay(1L, e.getPlayer());
    }
}
