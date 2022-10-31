# Bossing Trips
This plugin is to be able to track kill count, damage taken, and damage given during a trip in RuneScape.

## Commands
### Start [npc_name]

The start command begins a trip session, whenever a kill for this npc is encountered in the area, the kill count is incremented.

The trip ends when the user manually ends the trip, reset the trip, or dies/teleports away.
```
> start [npc_name]
> start general graardor
> start general_graardor
```

### End
The end command ends a trip, it also prints out the information about the trip to the user, no more kills are recorded after this occurs

```
> end
```

### Reset
The reset command reset the kills and damage during a trip all back to zero, any more kills after this will continue to be tracked
```
> reset
```

### Get
The get command prints out the current information about the trip for the user
```
> get
```