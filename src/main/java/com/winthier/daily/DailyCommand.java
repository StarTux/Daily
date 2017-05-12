package com.winthier.daily;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class DailyCommand implements CommandExecutor {
    private final DailyPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player ? (Player)sender : null;
        if (player == null) return false;
        if (args.length == 0) {
            player.sendMessage("");
            Msg.send(player, "&a&lDaily Tasks");
            Msg.send(player, " %s", plugin.getConfig().getString("CommandHelp"));
            Msg.send(player, " You have &a%d Daily Points&r.", plugin.getScore(player));
            player.sendMessage("");
            plugin.showProgress(player);
            player.sendMessage("");
            plugin.showTickBoxes(player);
            player.sendMessage("");
        }
        return true;
    }

}
