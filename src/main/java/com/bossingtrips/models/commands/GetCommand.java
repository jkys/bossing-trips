package com.bossingtrips.models.commands;

import com.bossingtrips.managers.TripManager;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

public class GetCommand extends Command {
    Client client;
    TripManager tripManager;

    public GetCommand(Client client, TripManager tripManager) {
        this.client = client;
        this.tripManager = tripManager;
    }

    @Override
    public void execute() {
        if (!tripManager.isTripOngoing()) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "No trip currently.", null);
            return;
        }

        tripManager.printTripInformation(client);
    }
}
