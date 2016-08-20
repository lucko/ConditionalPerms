package me.lucko.conditionalperms.conditions.towny;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.events.PlayerTownyRegionChangeEvent;
import me.lucko.conditionalperms.hooks.impl.TownyHook;
import me.lucko.conditionalperms.utils.TownyRegion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

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

    @EventHandler
    public void onRegionChange(PlayerTownyRegionChangeEvent e) {
        getPlugin().refreshPlayer(e.getPlayer(), 5L);
    }
}
