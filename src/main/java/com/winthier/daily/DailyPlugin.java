package com.winthier.daily;

import com.winthier.daily.sql.SQLLog;
import com.winthier.daily.sql.SQLScore;
import com.winthier.sql.SQLDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class DailyPlugin extends JavaPlugin {
    @Getter private static DailyPlugin instance;
    private List<Reward> rewards = null;
    private VaultHandler vaultHandler = null;
    private SQLDatabase db;
    private final List<DailyTask> dailyTasks = Arrays.asList(
        new DailyTask("Quest", "Complete one quest", "/q"),
        new DailyTask("SkillLevel", "Level up one skill", "/sk")
        );

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        db = new SQLDatabase(this);
        db.registerTables(SQLScore.class, SQLLog.class);
        db.createAllTables();
        getCommand("dailyadmin").setExecutor(new AdminCommand(this));
        getCommand("daily").setExecutor(new DailyCommand(this));
        // Handlers
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHandler = new VaultHandler();
        } else {
            getLogger().warning("Vault not found!");
        }
        if (getServer().getPluginManager().getPlugin("Quests") != null) {
            getServer().getPluginManager().registerEvents(new QuestsHandler(this), this);
        } else {
            getLogger().warning("Quests not found!");
        }
        if (getServer().getPluginManager().getPlugin("Skills") != null) {
            getServer().getPluginManager().registerEvents(new SkillsHandler(this), this);
        } else {
            getLogger().warning("Skills not found!");
        }
    }

    @Override
    public void onDisable() {
        flushCache();
        instance = null;
    }

    void flushCache() {
        reloadConfig();
        rewards = null;
    }

    public List<Reward> getRewards() {
        if (rewards == null) {
            rewards = new ArrayList<>();
            ConfigurationSection section = getConfig().getConfigurationSection("rewards");
            for (String key: section.getKeys(false)) {
                int score = Integer.parseInt(key);
                Reward reward = Reward.of(score, section.getConfigurationSection(key));
                rewards.add(reward);
            }
            Collections.sort(rewards);
        }
        return rewards;
    }

    public Reward getReward(int score) {
        for (Reward reward: getRewards()) {
            if (score == reward.getScore()) return reward;
            if (score < reward.getScore()) return null;
        }
        return null;
    }

    boolean giveDaily(Player player, String dailyName) {
        if (SQLLog.existsToday(player, dailyName)) return false;
        SQLLog.store(player, dailyName);
        int score = SQLScore.addScore(player, 1);
        Reward reward = getReward(score);
        if (reward != null) {
            giveReward(player, reward);
        }
        return true;
    }

    void giveReward(Player player, Reward reward) {
        SQLLog.store(player, "Reward-" + reward.getScore());
        reward.give(player);
        Msg.raw(player,
                "Congratulations! You win the reward ",
                Msg.button(ChatColor.GREEN,
                           reward.getTitle(),
                           "&a" + reward.getTitle() + "\n&oReward\n" + reward.getDescription(),
                           "/daily"),
                Msg.format("&r."));
    }

    public int getScore(Player player) {
        return SQLScore.getScore(player);
    }

    void showProgress(Player player) {
        String dotSymbol = getConfig().getString("ProgressBar.Dot", "-");
        String rewardSymbol = getConfig().getString("ProgressBar.Reward", "O");
        String arrowHeadSymbol = getConfig().getString("ProgressBar.ArrowHead", ">");
        ChatColor completedColor = ChatColor.valueOf(getConfig().getString("ProgressBar.Completed", "yellow").toUpperCase());
        ChatColor uncompletedColor = ChatColor.valueOf(getConfig().getString("ProgressBar.Uncompleted", "dark_gray").toUpperCase());
        int showLeft = getConfig().getInt("ProgressBar.ShowLeft", 20);
        int showTotal = getConfig().getInt("ProgressBar.ShowTotal", 80);
        int score = getScore(player);
        int min = Math.max(0, score - showLeft);
        int max = min + showTotal;
        List<Object> json = new ArrayList<>();
        json.add(" ");
        for (int i = min; i <= max; ++i) {
            boolean completed = score >= i;
            Reward reward = getReward(i);
            ChatColor color = completed ? completedColor : uncompletedColor;
            if (reward == null) {
                json.add(Msg.button(color, dotSymbol, null, null));
            } else {
                if (completed) {
                    json.add(Msg.button(completedColor, rewardSymbol,
                                        "&9" + reward.getTitle() + "\n" + reward.getScore() + " Points &9(Completed)\n&o" + reward.getDescription(),
                                        null));
                } else {
                    json.add(Msg.button(uncompletedColor, rewardSymbol,
                                        "&a" + reward.getTitle() + "\n" + reward.getScore() + " Points\n&o" + reward.getDescription(),
                                        null));
                }
            }
        }
        json.add(Msg.button(uncompletedColor, arrowHeadSymbol, "&8More to come", null));
        Msg.raw(player, json);
    }

    void showTickBoxes(Player player) {
        for (DailyTask task: dailyTasks) {
            List<Object> json = new ArrayList<>();
            json.add(" ");
            if (SQLLog.existsToday(player, task.getId())) {
                json.add(Msg.button(ChatColor.GREEN, "[x]", "&aCompleted\nCome back tomorrow :)", null));
            } else {
                json.add(Msg.button(ChatColor.RED, "[&0_&c]", "&cNot completed\nGet another daily point.", null));
            }
            json.add(" ");
            json.add(Msg.button(ChatColor.WHITE,
                                task.getDescription(),
                                "&a" + task.getCommand(),
                                task.getCommand()));
            Msg.raw(player, json);
        }
    }
}
