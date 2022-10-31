package com.bossingtrips.models.commands;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Command {
    public abstract void execute();
}
