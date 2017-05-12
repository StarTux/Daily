package com.winthier.daily;

import lombok.Value;

@Value
final class DailyTask {
    private final String id, description, command;
}
