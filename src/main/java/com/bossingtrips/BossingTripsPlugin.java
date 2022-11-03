package com.bossingtrips;

import com.bossingtrips.managers.CommandManager;
import com.bossingtrips.managers.TripManager;
import com.bossingtrips.models.commands.Command;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "Bossing Trips"
)
public class BossingTripsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TripManager tripManager;

	@Inject
	private BossingTripsConfig config;

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
		if (!tripManager.isTripOngoing()) {
			return;
		}
		String recipient = hitsplatApplied.getActor().getName();
		if (Objects.requireNonNull(recipient).equalsIgnoreCase(client.getLocalPlayer().getName())) {
			int damageTaken = hitsplatApplied.getHitsplat().getAmount();
			tripManager.damageTaken(damageTaken);
		}

		// Currently just sets the start time of the trip
		if (tripManager.getTrip().getHumanBossName().equalsIgnoreCase(recipient)) {
			tripManager.bossHit();
		}
	}

	@Subscribe
	public void onCommandExecuted(CommandExecuted commandExecuted)
	{
		if (client.getGameState() != GameState.LOGGED_IN || commandExecuted == null) {
			return;
		}

		try {
			Command command = CommandManager.getCommandData(commandExecuted, client, tripManager);
			if (command == null) {
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Commands allowed: start [npc_name], get, reset, end", null);
				return;
			}

			command.execute();
		} catch (Exception e) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", e.getMessage(), null);
		}
	}
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState().equals(GameState.LOGGED_IN) && tripManager.hasTripStarted()) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("%s trip completed automatically due to state change.", tripManager.getTrip().getHumanBossName()), null);
			tripManager.endTrip(client);
			tripManager.startNewTrip();
		}

	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		if (!tripManager.isTripOngoing()) {
			return;
		}

		if (wasBossKilled(npcDespawned)) {
			int damageTaken = tripManager.getTrip().getCurrentDamageTaken();

			tripManager.completedKill();

			if (config.printOnKill()) {
				if (damageTaken > 0) {
					String damageTakenMessage = String.format("%d damage taken", damageTaken);
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", damageTakenMessage, null);
				}


				final int killCount = tripManager.getTrip().getKillCount();
				String message = String.format("%s kill count: %d", tripManager.getTrip().getHumanBossName(), killCount);
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
			}
		}
	}

	private boolean wasBossKilled(NpcDespawned npcDespawned) {
		// TODO: May be worth checking the type of hit splat which was received
		String npcNameWithoutSpaces = Objects.requireNonNull(npcDespawned.getNpc().getName()).replaceAll(" ", "_");
		return npcDespawned.getActor().isDead() && tripManager.getTrip().getBoss().equalsIgnoreCase(npcNameWithoutSpaces);
	}

	@Provides
	BossingTripsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BossingTripsConfig.class);
	}


	@Provides
	@Named("TripManager")
	TripManager provideTripManager()
	{
		return new TripManager();
	}
}
