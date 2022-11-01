package com.bossingtrips;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Bossing Trips")
public interface BossingTripsConfig extends Config {
    @ConfigItem(
            keyName = "print_on_kill",
            name = "Print on each kill",
            description = "Whether or not the kill count is printed each trip and damage taken (when there is any)."
    )
    default boolean printOnKill()
    {
        return true;
    }
}
