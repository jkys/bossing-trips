package com.bossingtrips;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BossingTripsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BossingTripsPlugin.class);
		RuneLite.main(args);
	}
}