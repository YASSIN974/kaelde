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

public class ImageRequester {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String apiUrl;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS).build();
    private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
    private DatabaseManager databaseManager;

    public ImageRequester(Settings settings, DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        apiUrl = settings.apiUrls.getOrDefault("imgen", "http://127.0.0.1:8000");
    }

    public byte[] getProfile(User user) throws Exception {
        UserConfig uc = databaseManager.getUser(user);

        ProfileRequest preq = new ProfileRequest();
        preq.avatarUrl = user.getEffectiveAvatarUrl();
        preq.name = user.getName();
        preq.discrim = user.getDiscriminator();
        preq.image = uc.image == null ? "default" : uc.image;
        preq.type = 0;
        preq.money = Integer.toString(uc.money);
        preq.reputation = uc.reputation > 0 ? "+" + uc.reputation : Integer.toString(uc.reputation);
        preq.level = 1;
        preq.exp = uc.xp;
        preq.maxExp = 1000;

        String json = gson.toJson(preq);
        Request request = new Request.Builder()
                .url(apiUrl + "/profile")
                .post(RequestBody.create(JSON, json))
                .build();
        Response response = client.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        if (bytes[0] != 'R' && bytes[1] != 'I') throw new IllegalStateException(response.body().string());

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
        public int level;
        public int exp;
        @SerializedName("max_exp")
        public int maxExp;
        public int type;
    }
}
