package me.lucko.conditionalperms.utils;

public enum FactionsRegion {
    NONE, // Wilderness
    WARZONE,
    SAFEZONE,
    ALLY, // In another factions land (that they're an ally to)
    NEUTRAL, // In another factions land (that they're neutral to)
    ENEMY, // In another factions land (that they're an enemy to)
    TRUCE, // In another factions land (that they're in a truce to)
    OWN // In their own factions land
}
