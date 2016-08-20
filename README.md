# ConditionalPerms [![Build Status](https://ci.lucko.me/job/ConditionalPerms/badge/icon)](https://ci.lucko.me/job/ConditionalPerms/)
Define permissions that only apply when conditions are met

## Links
* **Development Builds** - <https://ci.lucko.me/job/ConditionalPerms/>

## Usage
ConditionalPerms works with any existing permissions plugin, and applies certain permissions only if a condition is met. There is no configuration. You setup your conditions using permission nodes.

For example, if you wanted to grant a user access to `essentials.fly`, but only in the `world_nether` world, you would give them the `cperms.in_world=world_nether.essentials.fly` permission.

If you wanted to grant them fly in all worlds except `world_nether`, you would set `cperms.!in_world=world_nether.essentials.fly`.

You can also chain nodes together, as they are applied recursively.
For example, granting access to `bans.banhammer` only when a user is in Creative mode and flying would be done using `cperms.is_flying.cperms.in_gamemode=creative.bans.banhammer`.

### Available conditions:
The general usage is `cperms.condition=parameter.your.node`, or if a parameter is not required, `cperms.condition.your.node`. You can negate permissions by adding a `!` to the start of the condition.

| Condition                  | Description                     | Parameters       |
|----------------------------|---------------------------------|------------------|
| in_gamemode                | If in a certain gamemode        | the gamemode     |
| in_world                   | If in a certain world           | the world        |
| is_placeholder             | If placeholder returns true     | the placeholder  |
| is_flying                  | If flying                       | n/a              |
| in_region                  | If in a WorldGuard region       | the region       |
| in_combat                  | If combat tagged                | n/a              |
| in_plot                    | If in a PlotSquared plot        | n/a              |
| in_own_plot                | If in own PlotSquared plot      | n/a              |
| facs_land_none             | If in a Factions wilderness     | n/a              |
| facs_land_warzone          | If in a Factions warzone        | n/a              |
| facs_land_safezone         | If in a Factions safezone       | n/a              |
| facs_land_ally             | If in an ally factions land     | n/a              |
| facs_land_neutral          | If in a neutral factions land   | n/a              |
| facs_land_enemy            | If in an enemy factions land    | n/a              |
| facs_land_truce            | If in a truce factions land     | n/a              |
| facs_land_own              | If in own factions land         | n/a              |
| towny_land_admin           | If in specified towny area      | n/a              |
| towny_land_enemy           | If in specified towny area      | n/a              |
| towny_land_locked          | If in specified towny area      | n/a              |
| towny_land_not_registered  | If in specified towny area      | n/a              |
| towny_land_off_world       | If in specified towny area      | n/a              |
| towny_land_outsider        | If in specified towny area      | n/a              |
| towny_land_plot_ally       | If in specified towny area      | n/a              |
| towny_land_plot_friend     | If in specified towny area      | n/a              |
| towny_land_plot_owner      | If in specified towny area      | n/a              |
| towny_land_town_ally       | If in specified towny area      | n/a              |
| towny_land_town_owner      | If in specified towny area      | n/a              |
| towny_land_town_resident   | If in specified towny area      | n/a              |
| towny_land_unclaimed       | If in specified towny area      | n/a              |
| towny_land_warzone         | If in specified towny area      | n/a              |


Supports (pretty much) any version of Factions, the recent WorldGuard versions, CombatTagPlus, PlotSquared, PlaceholderAPI and Towny.

More conditions coming soon. Feel free to PR new ones.

### More info
ConditionalPerms listens to various events, to refresh permissions when a condition changes. This means that there are no recurring update tasks, therefore no unnecessary refreshes, and recalculation only occurs when needed. However, for conditions such as is_placeholder, there are no events to listen to. Instead, an update task runs, and updates the users with non-event driven conditions assigned to them, once every 30 seconds.

This means that the plugin does not waste tick time updating players who don't even have any conditional permissions assigned to them.

The update logic can be seen in the main `ConditionalPerms` class.

You can reload all online users (for example after you give a new permission) using the **/cperms reload** command. The permission needed to use this is **conditionalperms.reload**.

You can toggle the plugins console debugging output using the **/cperms debug** command. The permission needed to use this is **conditionalperms.debug**.

All conditions are registered in the `Condition` enum, and extend `AbstractCondition`. Whenever the plugin needs to hook with another plugin/system to get data, it is done through a Hook instance. All hooks are registered in the `Hook` enum, and extend `AbstractHook`. Hooks are initialised when ConditionalPerms starts, if the corresponding plugin is present.

Conditions that require a hook to operate do not work if the corresponding plugin is not enabled on the server.

A modified version of FactionsFramework is shaded into this plugin when built, so we can support all versions of Factions.
