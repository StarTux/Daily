package com.winthier.daily.sql;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.winthier.daily.DailyPlugin;
import com.winthier.daily.Util;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

@Entity
@Table(name = "scores")
@Getter
@Setter
@NoArgsConstructor
public class SQLScore {
    @Id Integer id;
    @NotNull UUID playerUuid;
    @NotNull String playerName;
    @NotNull Integer score;
    @Version Date version;

    private SQLScore(Player player) {
        setPlayerUuid(player.getUniqueId());
        setPlayerName(player.getName());
        setScore(0);
    }

    public static int addScore(Player player, int points) {
        SQLScore score = DailyPlugin.getInstance().getDatabase().find(SQLScore.class).where().eq("player_uuid", player.getUniqueId()).findUnique();
        if (score == null) {
            score = new SQLScore(player);
            score.setScore(points);
        } else {
            score.setScore(score.getScore() + points);
        }
        score.setPlayerName(player.getName());
        DailyPlugin.getInstance().getDatabase().save(score);
        return score.getScore();
    }

    public static int getScore(UUID uuid) {
        SQLScore score = DailyPlugin.getInstance().getDatabase().find(SQLScore.class).where().eq("player_uuid", uuid).findUnique();
        if (score == null) return 0;
        return score.getScore();
    }

    public static int getScore(Player player) {
        return getScore(player.getUniqueId());
    }
}
