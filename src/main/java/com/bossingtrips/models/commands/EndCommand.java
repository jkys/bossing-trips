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
        if (!tripManager.isTripOngoing()) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "There is no trip to end.", null);
            return;
        }

        tripManager.endTrip(client);
    }
}
