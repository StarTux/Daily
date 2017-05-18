package com.winthier.daily.sql;

import com.winthier.daily.DailyPlugin;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Entity @Getter @Setter @Table(name = "scores") @NoArgsConstructor
public final class SQLScore {
    @Id private Integer id;
    @Column(nullable = false) private UUID playerUuid;
    @Column(nullable = false) private String playerName;
    @Column(nullable = false) private Integer score;
    @Version private Date version;

    private SQLScore(Player player) {
        setPlayerUuid(player.getUniqueId());
        setPlayerName(player.getName());
        setScore(0);
    }

    public static int addScore(Player player, int points) {
        SQLScore score = DailyPlugin.getInstance().getDb().find(SQLScore.class).where().eq("player_uuid", player.getUniqueId()).findUnique();
        if (score == null) {
            score = new SQLScore(player);
            score.setScore(points);
        } else {
            score.setScore(score.getScore() + points);
        }
        score.setPlayerName(player.getName());
        DailyPlugin.getInstance().getDb().save(score);
        return score.getScore();
    }

    public static int getScore(UUID uuid) {
        SQLScore score = DailyPlugin.getInstance().getDb().find(SQLScore.class).where().eq("player_uuid", uuid).findUnique();
        if (score == null) return 0;
        return score.getScore();
    }

    public static int getScore(Player player) {
        return getScore(player.getUniqueId());
    }
}
