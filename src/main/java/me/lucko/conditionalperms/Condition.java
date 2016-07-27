package me.lucko.conditionalperms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.conditionalperms.conditions.bukkit.InGamemode;
import me.lucko.conditionalperms.conditions.bukkit.InWorld;
import me.lucko.conditionalperms.conditions.bukkit.IsFlying;
import me.lucko.conditionalperms.conditions.factions.InFactionsLand;
import me.lucko.conditionalperms.conditions.worldguard.InRegion;
import me.lucko.conditionalperms.hooks.FactionsHook;

@Getter
@AllArgsConstructor
enum Condition {

    IN_WORLD(new InWorld()),
    IN_GAMEMODE(new InGamemode()),
    IS_FLYING(new IsFlying()),

    IN_REGION(new InRegion()),

    FACS_LAND_NONE(new InFactionsLand(FactionsHook.FactionsRegion.NONE)),
    FACS_LAND_WARZONE(new InFactionsLand(FactionsHook.FactionsRegion.WARZONE)),
    FACS_LAND_SAFEZONE(new InFactionsLand(FactionsHook.FactionsRegion.SAFEZONE)),
    FACS_LAND_ALLY(new InFactionsLand(FactionsHook.FactionsRegion.ALLY)),
    FACS_LAND_NEUTRAL(new InFactionsLand(FactionsHook.FactionsRegion.NEUTRAL)),
    FACS_LAND_ENEMY(new InFactionsLand(FactionsHook.FactionsRegion.ENEMY)),
    FACS_LAND_TRUCE(new InFactionsLand(FactionsHook.FactionsRegion.TRUCE)),
    FACS_LAND_OWN(new InFactionsLand(FactionsHook.FactionsRegion.OWN));

    private AbstractCondition condition;

}
