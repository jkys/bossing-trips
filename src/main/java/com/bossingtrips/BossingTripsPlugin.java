package com.bossingtrips;

import com.bossingtrips.managers.CommandManager;
import com.bossingtrips.managers.TripManager;
import com.bossingtrips.models.TripInfo;
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

	@Override
	public void shutDown() {
		tripManager.endTrip();
	}

	/**
	 * Used to calculate how much damage the user and their team has dealt and received.
	 */
	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied) {
		if (!tripManager.isOngoingTrip()) {
			return;
		}

		String currentPlayer = client.getLocalPlayer().getName();
		String recipient = hitsplatApplied.getActor().getName();
		if (Objects.requireNonNull(recipient).equalsIgnoreCase(currentPlayer)) {
			int damageTaken = hitsplatApplied.getHitsplat().getAmount();
			tripManager.damageTaken(damageTaken);
		}

		// TODO: Need to find way to check who applied damage and apply that to the trip.
		if (tripManager.getPrettyBossName().equalsIgnoreCase(recipient)) {
			int damageTaken = hitsplatApplied.getHitsplat().getAmount();
			tripManager.damageGiven(damageTaken);
		}
	}

	/**
	 * This occurs when any command is interred into RuneLite, it will execute the overridden execute() function in each of the implementations of {@link Command}.
	 */
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

	/**
	 * This usually occurs when a user teleports away or is killed.
	 * <p>
	 * TODO: Look into other ways to possibly check if a trip should be reset.
	 */
	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState().equals(GameState.LOGGED_IN) && tripManager.isOngoingTrip()) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("%s trip completed automatically due to state change.", tripManager.getPrettyBossName()), null);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", tripManager.getTripInformation(), null);
			tripManager.resetTrip();
		}

	}

	/**
	 * Used to determine the amount of kills from a user and their team.
	 * @param npcDespawned {@link NpcDespawned}
	 */
	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned) {
		if (!tripManager.isOngoingTrip()) {
			return;
		}

		if (wasBossKilled(npcDespawned)) {
			TripInfo tripInfo = tripManager.getTripInfo();

			tripManager.completedKill();


			if (tripInfo.getCurrentDamageGiven() > 0) {
				String damageTakenMessage = String.format("%d damage taken this kill.", tripInfo.getCurrentDamageTaken());
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", damageTakenMessage, null);
			}


			String message = String.format("%s trip kill count is %d", tripManager.getPrettyBossName(), tripInfo.getKillCount());
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);

		}
	}

	private boolean wasBossKilled(NpcDespawned npcDespawned) {
		// TODO: May be worth checking the type of hit splat which was received
		String npcNameWithoutSpaces = npcDespawned.getNpc().getName().replaceAll(" ", "_");
		return npcDespawned.getActor().isDead() && tripManager.getCurrentBoss().equalsIgnoreCase(npcNameWithoutSpaces);
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
