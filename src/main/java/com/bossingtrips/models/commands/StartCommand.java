package com.bossingtrips.models.commands;

import com.bossingtrips.managers.TripManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

@Slf4j
public class StartCommand extends Command {
    Client client;
    TripManager tripManager;
    private final String bossName;

    public StartCommand(Client client, TripManager tripManager, @NonNull String bossName) {
        this.client = client;
        this.tripManager = tripManager;
        this.bossName = bossName.toUpperCase();
    }

    @Override
    public void execute() {
        tripManager.startTrip(this.bossName);
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("%s trip started", tripManager.getPrettyBossName()), null);
    }
}
