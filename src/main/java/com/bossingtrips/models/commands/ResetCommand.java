package com.bossingtrips.models.commands;

import com.bossingtrips.managers.TripManager;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

public class ResetCommand extends Command {
    Client client;
    TripManager tripManager;

    public ResetCommand(Client client, TripManager tripManager) {
        this.client = client;
        this.tripManager = tripManager;
    }
    @Override
    public void execute() {
        if (!tripManager.isOngoingTrip()) {
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "No trip currently.", null);
            return;
        }

        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("%s trip reset", tripManager.getPrettyBossName()), null);

        tripManager.resetTrip();
    }
}
