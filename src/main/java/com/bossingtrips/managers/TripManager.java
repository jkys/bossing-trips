package com.bossingtrips.managers;

import com.bossingtrips.models.Trip;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;

import javax.inject.Singleton;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Singleton
@Getter
@NoArgsConstructor
public class TripManager {
    private Trip trip;

    public void createNewTrip(@NonNull final String bossName) {
        this.trip = new Trip(bossName);
    }

    public void startNewTrip() {
        this.trip = new Trip(trip.getBoss());
    }

    public void bossHit() {
        this.trip.startTrip();
    }

    // Trip is considered "going" when it is initialized, not just when first hit occurs
    public boolean isTripOngoing() {
        if (trip == null) {
            return false;
        }

        return trip.getTripEnd() == null;
    }

    // Trip starts when first hit occurs
    public boolean hasTripStarted() {
        return trip != null && trip.getTripStart() != null;
    }

    public void endTrip(Client client) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("%s trip ended", trip.getHumanBossName()), null);

        printTripInformation(client);

        trip.endTrip();
    }

    public void printTripInformation(Client client) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("--- %s Trip ---", trip.getHumanBossName()), null);
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("Kills: %d ", trip.getKillCount()), null);

        final int averageDamage = trip.getKillCount() == 0 ? 0 : trip.getDamageTaken()/trip.getKillCount();
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("Damage Taken (avg): %d ", averageDamage), null);

        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", String.format("Trip Duration: %s", getHumanReadableTripDuration()), null);
    }

    public void completedKill() {
        trip.killCompleted();
    }

    public void damageTaken(final int damageTaken) {
        trip.damageTaken(damageTaken);
    }

    private String getHumanReadableTripDuration() {
        if (trip.getTripStart() == null) {
            return String.format("%02d:%02d:%02d", 0, 0, 0);
        }

        LocalDateTime lastKnownTripTime = isTripOngoing() ? LocalDateTime.now() : trip.getTripEnd();
        LocalDateTime tempDateTime = LocalDateTime.from(trip.getTripStart());

        long hours = tempDateTime.until(lastKnownTripTime, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(lastKnownTripTime, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes(minutes);

        long seconds = tempDateTime.until(lastKnownTripTime, ChronoUnit.SECONDS);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
