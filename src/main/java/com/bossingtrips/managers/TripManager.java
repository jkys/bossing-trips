package com.bossingtrips.managers;

import com.bossingtrips.models.TripInfo;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
@NoArgsConstructor
public class TripManager {
    final private HashMap<String, TripInfo> tripData = new HashMap<>();
    private String currentBoss;

    public String getCurrentBoss() {
        return this.currentBoss;
    }

    public String getPrettyBossName() {
        return WordUtils.capitalizeFully(this.currentBoss.replaceAll("_", " ").toLowerCase());
    }

    public boolean isOngoingTrip() {
        if (StringUtils.isEmpty(currentBoss)) {
            return false;
        }

        return tripData.get(currentBoss).isOngoingTrip();
    }


    /**
     * Starts a trip for a boss run, in the case that a trip is already ongoing, this will reset the trip.
     * @param bossName name of the boss to start a trip for (use _ in the case of spaces)
     */
    public void startTrip(@NonNull String bossName) {
        currentBoss = bossName.toUpperCase();
        tripData.put(currentBoss, new TripInfo());
    }

    public void endTrip() {
        TripInfo tripInfo = tripData.get(currentBoss);
        tripInfo.endTrip();
    }

    public TripInfo getTripInfo() {
        return tripData.getOrDefault(currentBoss, null);
    }

    public TripInfo completedKill() {
        return tripData.computeIfPresent(currentBoss, (notUsed, tripInfo) -> {
            tripInfo.killCompleted();
            return tripInfo;
        });
    }

    public void damageTaken(int damageTaken) {
        tripData.computeIfPresent(currentBoss, (notUsed, tripInfo) -> {
            tripInfo.damageTaken(damageTaken);
            return tripInfo;
        });
    }

    public void damageGiven(int damageTaken) {
        tripData.computeIfPresent(currentBoss, (notUsed, tripInfo) -> {
            tripInfo.damageGiven(damageTaken);
            return tripInfo;
        });
    }

    public void resetTrip() {
        TripInfo tripInfo = tripData.get(currentBoss);
        tripInfo.resetTrip();
    }

    public String getTripInformation() {
        if (!isOngoingTrip()) {
            return null;
        }

        TripInfo tripInfo = getTripInfo();

        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(String.format("Kills: %d ", tripInfo.getKillCount()));
        messageBuilder.append(String.format("Damage Taken: %d ", tripInfo.getTotalDamageTaken()));
        messageBuilder.append(String.format("Damage Given: %d ", tripInfo.getTotalDamageGiven()));

        if (tripInfo.getCurrentDamageGiven() != 0) {
            messageBuilder.append(String.format("Current Damage Given: %d ", tripInfo.getCurrentDamageGiven()));
            messageBuilder.append(String.format("Current Damage Taken: %d", tripInfo.getCurrentDamageTaken()));
        }

        return messageBuilder.toString();
    }
}
