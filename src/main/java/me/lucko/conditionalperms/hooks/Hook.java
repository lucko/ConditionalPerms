package me.lucko.conditionalperms.hooks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.conditionalperms.hooks.impl.*;

@Getter
@AllArgsConstructor
enum Hook {

    COMBAT_TAB_PLUS("CombatTagPlus", CombatTagPlusHook.class),
    FACTIONS("Factions", FactionsHook.class),
    PLOT_SQUARED("PlotSquared", PlotSquaredHook.class),
    WORLD_GUARD("WorldGuard", WorldGuardHook.class),
    PLACEHOLDER_API("PlaceholderAPI", PlaceholderAPIHook.class),
    TOWNY("Towny", TownyHook.class);

    private final String pluginName;
    private final Class<? extends AbstractHook> clazz;
}
