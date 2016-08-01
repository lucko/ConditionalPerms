package me.lucko.conditionalperms.conditions.placeholderapi;

import me.lucko.conditionalperms.conditions.AbstractCondition;
import me.lucko.conditionalperms.hooks.impl.PlaceholderAPIHook;
import org.bukkit.entity.Player;

public class IsPlaceholder extends AbstractCondition {
    public IsPlaceholder() {
        super(true, PlaceholderAPIHook.class);
    }

    @Override
    public boolean shouldApply(Player player, String parameter) {
        if (!parameter.startsWith("%") && !parameter.endsWith("%")) {
            parameter = "%" + parameter + "%";
        }

        try {
            return getPlugin().getHookManager().get(PlaceholderAPIHook.class).getResult(parameter, player);
        } catch (IllegalArgumentException e) {
            getPlugin().debug("Could not parse a result from placeholder " + parameter + ", with error '" + e.getMessage() + "'.");
            return false;
        }
    }
}
