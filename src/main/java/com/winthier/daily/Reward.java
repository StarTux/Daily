package com.winthier.daily;

import java.util.Arrays;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@Getter
abstract class Reward implements Comparable<Reward> {
    private int score;
    private String title;
    private String description;

    abstract void give(Player player);

    void deserialize(ConfigurationSection config) { }

    @Override
    public int compareTo(Reward o) {
        return Integer.compare(score, o.score);
    }

    static Reward of(int score, ConfigurationSection config) {
        String type = config.getString("Type");
        Reward result;
        if ("ConsoleCommand".equals(type)) {
            result = new ConsoleCommandReward();
        } else if ("RankUp".equals(type)) {
            result = new RankUpReward();
        } else {
            throw new IllegalArgumentException("Unknown Reward Type: " + type);
        }
        result.title = type;
        result.description = type;
        result.deserialize(config);
        result.score = score;
        result.title = config.getString("Title", result.title);
        result.description = config.getString("Description", result.description);
        return result;
    }
}

class ConsoleCommandReward extends Reward {
    private String command;

    @Override
    void deserialize(ConfigurationSection config) {
        command = config.getString("Command");
        if (command == null) throw new NullPointerException("Command cannot be null");
    }

    @Override
    public void give(Player player) {
        String cmd = command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
        Msg.consoleCommand(cmd);
    }
}

class RankUpReward extends Reward {
    private String from, to;

    @Override
    void deserialize(ConfigurationSection config) {
        from = config.getString("From");
        to = config.getString("To");
        if (from == null) throw new NullPointerException("From cannot be null");
        if (to == null) throw new NullPointerException("To cannot be null");
    }

    @Override
    public void give(Player player) {
        VaultHandler vault = DailyPlugin.getInstance().getVaultHandler();
        if (Arrays.asList(vault.getPermission().getPlayerGroups(player)).contains(from)) {
            vault.getPermission().playerAddGroup(player, to);
            vault.getPermission().playerRemoveGroup(player, from);
        } else {
            DailyPlugin.getInstance().getLogger().warning(String.format("RankReward(%s->%s): Player %s not in from group; doing nothing.", from, to, player.getName()));
        }
    }
}
