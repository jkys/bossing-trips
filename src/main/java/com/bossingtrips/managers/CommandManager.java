package com.bossingtrips.managers;

import com.bossingtrips.models.commands.Command;
import com.bossingtrips.models.commands.CommandType;
import com.bossingtrips.models.commands.EndCommand;
import com.bossingtrips.models.commands.GetCommand;
import com.bossingtrips.models.commands.StartCommand;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.CommandExecuted;

@Slf4j
public class CommandManager {
    public static Command getCommandData(CommandExecuted command, Client client, TripManager tripManager) {
        switch (convertCommandType(command.getCommand())) {
            case START:
                if (commandHasArgument(command)) {
                    String bossName = extractBossName(command);
                    return new StartCommand(client, tripManager, bossName);
                } else {
                    throw new IllegalArgumentException("Missing boss name, please pass name of boss to start trip.");
                }
            case END:
                return new EndCommand(client, tripManager);
            case GET:
                return new GetCommand(client, tripManager);
            default:
                return null;
        }
    }

    private static String extractBossName(CommandExecuted command) {

        if (command.getArguments().length > 1) {
            StringBuilder bossNameBuilder = new StringBuilder();
            for (String argument : command.getArguments()) {
                bossNameBuilder.append(argument);
                bossNameBuilder.append("_");
            }

            // Substring to remove the extra "_" for the last word in the boss name
            return bossNameBuilder.substring(0, bossNameBuilder.toString().length() - 1);
        } else {
            return command.getArguments()[0];
        }
    }

    private static boolean commandHasArgument(CommandExecuted command) {
        return command.getArguments() != null && command.getArguments().length >= 1 && command.getArguments()[0] != null;
    }

    private static CommandType convertCommandType(String command) {
        return CommandType.valueOf(command.toUpperCase());
    }
}
