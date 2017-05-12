package com.winthier.daily.sql;

import com.winthier.daily.DailyPlugin;
import com.winthier.daily.Util;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Entity @Getter @Setter @Table(name = "logs") @NoArgsConstructor
public final class SQLLog {
    @Id private Integer id;
    @Column(nullable = false) private Date time;
    @Column(nullable = false) private UUID playerUuid;
    @Column(nullable = false) private String playerName;
    @Column(nullable = false) private String dailyName;

    private SQLLog(Player player, String dailyName) {
        setTime(new Date());
        setPlayerUuid(player.getUniqueId());
        setPlayerName(player.getName());
        setDailyName(dailyName);
    }

    public static void store(Player player, String dailyName) {
        DailyPlugin.getInstance().getDb().save(new SQLLog(player, dailyName));
    }

    public static boolean existsToday(Player player, String dailyName) {
        List<SQLLog> logs = DailyPlugin.getInstance().getDb().find(SQLLog.class).where().eq("player_uuid", player.getUniqueId()).eq("daily_name", dailyName).findList();
        Date now = new Date();
        for (SQLLog log: logs) {
            if (Util.sameDay(now, log.getTime())) return true;
        }
        return false;
    }
}
