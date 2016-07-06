package com.winthier.daily;

import com.winthier.skills.bukkit.event.SkillsLevelUpEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class SkillsHandler implements Listener {
    final DailyPlugin plugin;

    @EventHandler
    public void onSkillsLevelUp(SkillsLevelUpEvent event) {
        plugin.giveDaily(event.getPlayer(), "SkillLevel");
    }
}
