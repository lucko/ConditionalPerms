package me.lucko.conditionalperms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.conditions.bukkit.InGamemode;
import me.lucko.conditionalperms.conditions.bukkit.InWorld;
import me.lucko.conditionalperms.conditions.bukkit.IsFlying;
import me.lucko.conditionalperms.conditions.factions.InFactionsLand;
import me.lucko.conditionalperms.conditions.worldguard.InRegion;
import me.lucko.conditionalperms.utils.FactionsRegion;

@Getter
@AllArgsConstructor
enum Condition {

    IN_WORLD(new InWorld()),
    IN_GAMEMODE(new InGamemode()),
    IS_FLYING(new IsFlying()),

    IN_REGION(new InRegion()),

    FACS_LAND_NONE(new InFactionsLand(FactionsRegion.NONE)),
    FACS_LAND_WARZONE(new InFactionsLand(FactionsRegion.WARZONE)),
    FACS_LAND_SAFEZONE(new InFactionsLand(FactionsRegion.SAFEZONE)),
    FACS_LAND_ALLY(new InFactionsLand(FactionsRegion.ALLY)),
    FACS_LAND_NEUTRAL(new InFactionsLand(FactionsRegion.NEUTRAL)),
    FACS_LAND_ENEMY(new InFactionsLand(FactionsRegion.ENEMY)),
    FACS_LAND_TRUCE(new InFactionsLand(FactionsRegion.TRUCE)),
    FACS_LAND_OWN(new InFactionsLand(FactionsRegion.OWN)), ;

    private AbstractCondition condition;

}
