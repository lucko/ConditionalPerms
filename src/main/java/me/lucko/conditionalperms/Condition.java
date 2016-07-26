package me.lucko.conditionalperms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lucko.conditionalperms.conditions.bukkit.InGamemode;
import me.lucko.conditionalperms.conditions.bukkit.InWorld;
import me.lucko.conditionalperms.conditions.bukkit.IsFlying;
import me.lucko.conditionalperms.conditions.worldguard.InRegion;

@Getter
@AllArgsConstructor
enum Condition {

    IN_WORLD(new InWorld()),
    IN_GAMEMODE(new InGamemode()),
    IS_FLYING(new IsFlying()),

    IN_REGION(new InRegion());

    private ICondition condition;

}
