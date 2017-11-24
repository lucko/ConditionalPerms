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

package me.lucko.conditionalperms;

import lombok.AllArgsConstructor;
import lombok.Getter;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.conditions.askyblock.InIsland;
import me.lucko.conditionalperms.conditions.askyblock.InOwnIsland;
import me.lucko.conditionalperms.conditions.askyblock.IsIslandMember;
import me.lucko.conditionalperms.conditions.bukkit.InGamemode;
import me.lucko.conditionalperms.conditions.bukkit.InWorld;
import me.lucko.conditionalperms.conditions.bukkit.IsFlying;
import me.lucko.conditionalperms.conditions.combattagplus.InCombat;
import me.lucko.conditionalperms.conditions.factions.InFactionsLand;
import me.lucko.conditionalperms.conditions.placeholderapi.IsPlaceholder;
import me.lucko.conditionalperms.conditions.plotsquared.InOwnPlot;
import me.lucko.conditionalperms.conditions.plotsquared.InPlot;
import me.lucko.conditionalperms.conditions.towny.InTownyRegion;
import me.lucko.conditionalperms.conditions.vanishnopacket.IsVanished;
import me.lucko.conditionalperms.conditions.worldguard.InRegion;
import me.lucko.conditionalperms.utils.FactionsRegion;
import me.lucko.conditionalperms.utils.TownyRegion;

@Getter
@AllArgsConstructor
enum Condition {

    IN_WORLD(new InWorld()),
    IN_GAMEMODE(new InGamemode()),
    IS_FLYING(new IsFlying()),

    IS_PLACEHOLDER(new IsPlaceholder()),

    IS_VANISHED(new IsVanished()),

    ASKYBLOCK_IN_ISLAND(new InIsland()),
    ASKYBLOCK_IN_OWN_ISLAND(new InOwnIsland()),
    ASKYBLOCK_IS_ISLAND_MEMBER(new IsIslandMember()),

    IN_REGION(new InRegion()),

    IN_COMBAT(new InCombat()),

    IN_PLOT(new InPlot()),
    IN_OWN_PLOT(new InOwnPlot()),

    FACS_LAND_NONE(new InFactionsLand(FactionsRegion.NONE)),
    FACS_LAND_WARZONE(new InFactionsLand(FactionsRegion.WARZONE)),
    FACS_LAND_SAFEZONE(new InFactionsLand(FactionsRegion.SAFEZONE)),
    FACS_LAND_ALLY(new InFactionsLand(FactionsRegion.ALLY)),
    FACS_LAND_NEUTRAL(new InFactionsLand(FactionsRegion.NEUTRAL)),
    FACS_LAND_ENEMY(new InFactionsLand(FactionsRegion.ENEMY)),
    FACS_LAND_TRUCE(new InFactionsLand(FactionsRegion.TRUCE)),
    FACS_LAND_OWN(new InFactionsLand(FactionsRegion.OWN)),

    TOWNY_LAND_ADMIN(new InTownyRegion(TownyRegion.ADMIN)),
    TOWNY_LAND_ENEMY(new InTownyRegion(TownyRegion.ENEMY)),
    TOWNY_LAND_LOCKED(new InTownyRegion(TownyRegion.LOCKED)),
    TOWNY_LAND_NOT_REGISTERED(new InTownyRegion(TownyRegion.NOT_REGISTERED)),
    TOWNY_LAND_OFF_WORLD(new InTownyRegion(TownyRegion.OFF_WORLD)),
    TOWNY_LAND_OUTSIDER(new InTownyRegion(TownyRegion.OUTSIDER)),
    TOWNY_LAND_PLOT_ALLY(new InTownyRegion(TownyRegion.PLOT_ALLY)),
    TOWNY_LAND_PLOT_FRIEND(new InTownyRegion(TownyRegion.PLOT_FRIEND)),
    TOWNY_LAND_PLOT_OWNER(new InTownyRegion(TownyRegion.PLOT_OWNER)),
    TOWNY_LAND_TOWN_ALLY(new InTownyRegion(TownyRegion.TOWN_ALLY)),
    TOWNY_LAND_TOWN_OWNER(new InTownyRegion(TownyRegion.TOWN_OWNER)),
    TOWNY_LAND_TOWN_RESIDENT(new InTownyRegion(TownyRegion.TOWN_RESIDENT)),
    TOWNY_LAND_UNCLAIMED(new InTownyRegion(TownyRegion.UNCLAIMED)),
    TOWNY_LAND_WARZONE(new InTownyRegion(TownyRegion.WARZONE));

    private AbstractCondition condition;

}
