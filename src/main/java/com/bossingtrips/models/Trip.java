package com.bossingtrips.models;

import lombok.Data;
import org.apache.commons.text.WordUtils;

import java.time.LocalDateTime;

@Data
public class Trip {

    private int killCount;

    private int damageTaken;
    private int currentDamageTaken;

    private LocalDateTime tripStart;
    private LocalDateTime tripEnd;

    private String boss;

    // Essentially creates and starts a trip, trip time is started when first hit occurs
    public Trip(final String boss) {
        this.killCount = 0;
        this.damageTaken = 0;
        this.currentDamageTaken = 0;
        this.boss = getSystemBossName(boss);
    }

    public void startTrip() {
        if (tripStart != null) {
            return;
        }

        this.tripStart = LocalDateTime.now();
    }

    public void endTrip() {
        this.tripEnd = LocalDateTime.now();
    }

    private String getSystemBossName(String boss) {
        return boss.replaceAll(" ", "_").toLowerCase();
    }

    public String getHumanBossName() {
        return WordUtils.capitalizeFully(this.boss.replaceAll("_", " "));
    }

    public void damageTaken(int damage) {
        this.damageTaken += damage;
        this.currentDamageTaken += damage;
    }

    public void killCompleted() {
        this.currentDamageTaken = 0;
        this.killCount++;
    }
}
