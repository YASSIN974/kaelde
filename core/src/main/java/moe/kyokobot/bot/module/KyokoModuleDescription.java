package moe.kyokobot.bot.module;

import com.google.gson.annotations.SerializedName;

public class KyokoModuleDescription {
    @SerializedName("main")
    public String mainClass;

    @SerializedName("name")
    public String moduleName;
}
