package moe.kyokobot.social.requester;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import moe.kyokobot.bot.Settings;
import moe.kyokobot.bot.entity.UserConfig;
import moe.kyokobot.bot.manager.DatabaseManager;
import net.dv8tion.jda.core.entities.User;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

import static moe.kyokobot.social.LevelHandler.LEVEL_THRESHOLD;

public class ImageRequester {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String apiUrl;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).build();
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private DatabaseManager databaseManager;

    public ImageRequester(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        apiUrl = Settings.instance.apiUrls.getOrDefault("imgen", "http://localhost:3000/api/imgen");
    }

    public byte[] getProfile(User user) throws Exception {
        UserConfig uc = databaseManager.getUser(user);

        ProfileRequest preq = new ProfileRequest();
        preq.avatarUrl = user.getEffectiveAvatarUrl();
        preq.name = user.getName();
        preq.discrim = user.getDiscriminator();
        preq.image = uc.getImage() == null ? "default" : uc.getImage();
        preq.type = 0;
        preq.money = Long.toString(uc.getMoney());
        preq.reputation = uc.getReputation() > 0 ? "+" + uc.getReputation() : Long.toString(uc.getReputation());
        preq.level = uc.getLevel();
        preq.exp = uc.getXp();
        preq.maxExp = LEVEL_THRESHOLD * uc.getLevel();
        if (uc.getTheme() == 0) uc.setTheme(1); // fix
        preq.theme = uc.getTheme();

        String json = gson.toJson(preq);
        Request request = new Request.Builder()
                .url(apiUrl + "/profile")
                .post(RequestBody.create(JSON, json))
                .build();
        Response response = client.newCall(request).execute();

        if (response.code() != 200) throw new IllegalStateException("Received non-successful status code, please try again later (" + response.code() + ")!");

        byte[] bytes = response.body().bytes();
        if (bytes[0] != (byte) 0x89 && bytes[1] != 0x50) throw new IllegalStateException(response.body().string());

        return bytes;
    }

    private class ProfileRequest {
        @SerializedName("avatar_url")
        public String avatarUrl;
        public String image;
        public String name;
        public String discrim;
        public String reputation;
        public String money;
        public long level;
        public long exp;
        @SerializedName("max_exp")
        public long maxExp;
        public int type;
        public int theme;
    }
}
