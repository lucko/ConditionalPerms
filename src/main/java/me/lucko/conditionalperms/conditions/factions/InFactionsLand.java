package me.lucko.conditionalperms.conditions.factions;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.events.PlayerFactionsRegionChangeEvent;
import me.lucko.conditionalperms.hooks.impl.FactionsHook;
import me.lucko.conditionalperms.utils.FactionsRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class InFactionsLand extends AbstractCondition {
    private final FactionsRegion r;

    public InFactionsLand(FactionsRegion r) {
        super(false, FactionsHook.class);
        this.r = r;
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        return getPlugin().getHookManager().get(FactionsHook.class).getRegion(player).equals(r);
    }

    @EventHandler
    public void onRegionChange(PlayerFactionsRegionChangeEvent e) {
        getPlugin().refreshPlayer(e.getPlayer(), 5L);
    }
}