package moe.kyokobot.bot.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberConfig implements DatabaseEntity {
    public MemberConfig() {

    }

    public MemberConfig(String id) {
        this.id = id;
    }

    private String id = "";
    private int points = 0;
    private int strikes = 0;
    private boolean hackban = false;
    private long tempban = 0;

    @Override
    public String getTableName() {
        return "members";
    }
}
