package com.winthier.daily;

import com.winthier.daily.sql.*;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class AdminCommand implements CommandExecutor {
    final DailyPlugin plugin;
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player)sender : null;
        if (args.length == 2 && args[0].equalsIgnoreCase("testdaily")) {
            String dailyName = args[1];
            if (plugin.giveDaily(player, dailyName)) {
                sender.sendMessage("Success: " + dailyName);
            } else {
                sender.sendMessage("Fail: " + dailyName);
            }
            sender.sendMessage("Score: " + SQLScore.getScore(player));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("testreward")) {
            if (player == null) return false;
            int score = Integer.parseInt(args[1]);
            Reward reward = plugin.getReward(score);
            if (reward == null) {
                player.sendMessage("Reward not  found: " + score);
                return true;
            }
            plugin.giveReward(player, reward);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
            sender.sendMessage("Score: " + SQLScore.getScore(player));
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.rewards = null;
            sender.sendMessage("Configuration reloaded");
        }
        return true;
    }
}
