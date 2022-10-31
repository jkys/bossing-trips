package com.bossingtrips.models;

import lombok.Data;

@Data
public class TripInfo {

    private int killCount;
    private int totalDamageTaken;
    private int currentDamageTaken;

    private int totalDamageGiven;
    private int currentDamageGiven;
    private boolean isOngoingTrip;

    public TripInfo() {
       resetTrip();
       this.isOngoingTrip = true;
    }

    public boolean isOngoingTrip() {
        return isOngoingTrip;
    }

    public void resetTrip() {
        this.killCount = 0;

        this.totalDamageTaken = 0;
        this.currentDamageTaken = 0;

        this.totalDamageGiven = 0;
        this.currentDamageGiven = 0;
    }

    public void endTrip() {
        resetTrip();
        this.isOngoingTrip = false;
    }

    public void damageTaken(int damage) {
        this.currentDamageTaken += damage;
        this.isOngoingTrip = true;
    }

    public void damageGiven(int damage) {
        this.currentDamageGiven += damage;
        this.isOngoingTrip = true;
    }

    public void killCompleted() {
        this.totalDamageTaken += this.currentDamageTaken;
        this.currentDamageTaken = 0;

        this.totalDamageGiven += this.currentDamageGiven;
        this.currentDamageGiven = 0;

        this.isOngoingTrip = true;
        this.killCount++;
    }
}
