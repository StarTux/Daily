package com.winthier.daily.sql;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.winthier.daily.DailyPlugin;
import com.winthier.daily.Util;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

@Entity
@Table(name = "logs")
@Getter
@Setter
@NoArgsConstructor
public class SQLLog {
    @Id Integer id;
    @NotNull Date time;
    @NotNull UUID playerUuid;
    @NotNull String playerName;
    @NotNull String dailyName;

    private SQLLog(Player player, String dailyName) {
        setTime(new Date());
        setPlayerUuid(player.getUniqueId());
        setPlayerName(player.getName());
        setDailyName(dailyName);
    }

    public static void store(Player player, String dailyName) {
        DailyPlugin.getInstance().getDatabase().save(new SQLLog(player, dailyName));
    }

    public static boolean existsToday(Player player, String dailyName) {
        List<SQLLog> logs = DailyPlugin.getInstance().getDatabase().find(SQLLog.class).where().eq("player_uuid", player.getUniqueId()).eq("daily_name", dailyName).findList();
        Date now = new Date();
        for (SQLLog log: logs) {
            if (Util.sameDay(now, log.getTime())) return true;
        }
        return false;
    }
}
