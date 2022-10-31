package com.bossingtrips.models.commands;

import com.bossingtrips.managers.TripManager;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

public class EndCommand extends Command {
    Client client;
    TripManager tripManager;

    public EndCommand(Client client, TripManager tripManager) {
        this.client = client;
        this.tripManager = tripManager;
    }
    @Override
    public void execute() {
        if (!tripManager.isOngoingTrip()) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "No trip currently.", null);
            return;
        }

        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("%s trip ended", tripManager.getPrettyBossName()), null);
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", tripManager.getTripInformation(), null);

        tripManager.endTrip();
    }
}
