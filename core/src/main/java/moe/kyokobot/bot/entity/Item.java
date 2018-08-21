package moe.kyokobot.bot.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    @SerializedName("item_id")
    private int itemId = 0;
    private boolean consumable = false;
    @SerializedName("uses_remaining")
    private int usesRemaining = 0;
}
