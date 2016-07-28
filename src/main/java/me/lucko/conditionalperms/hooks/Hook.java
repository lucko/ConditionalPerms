package me.lucko.conditionalperms.hooks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.conditionalperms.hooks.impl.CombatTagPlusHook;
import me.lucko.conditionalperms.hooks.impl.FactionsHook;
import me.lucko.conditionalperms.hooks.impl.PlotSquaredHook;
import me.lucko.conditionalperms.hooks.impl.WorldGuardHook;

@Getter
@AllArgsConstructor
enum Hook {

    COMBAT_TAB_PLUS("CombatTagPlus", CombatTagPlusHook.class),
    FACTIONS("Factions", FactionsHook.class),
    PLOT_SQUARED("PlotSquared", PlotSquaredHook.class),
    WORLD_GUARD("WorldGuard", WorldGuardHook.class);

    private final String pluginName;
    private final Class<? extends AbstractHook> clazz;
}
