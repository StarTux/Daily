package com.winthier.daily;

import io.github.feydk.quests.QuestCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class QuestsHandler implements Listener {
    final DailyPlugin plugin;

    @EventHandler
    public void onQuestCompleted(QuestCompletedEvent event) {
        plugin.giveDaily(event.getPlayer(), "Quest");
    }
}
