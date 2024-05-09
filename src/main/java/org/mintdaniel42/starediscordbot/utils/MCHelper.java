package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.UsernameModel;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@UtilityClass
public class MCHelper {
    private static final int timeout = 172800;

    public @Nullable UUID getUuid(@NonNull DatabaseAdapter databaseAdapter, @NonNull String username) {
        UsernameModel usernameModel;
        if ((usernameModel = databaseAdapter.getUsernameModel(username)) != null && usernameModel.getLastUpdated() >= Instant.now().toEpochMilli() - timeout) return usernameModel.getUuid();
        else {
            final OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder().url("https://playerdb.co/api/player/minecraft/" + username).build();
            try(Response response = okHttpClient.newCall(request).execute()) {
                JSONObject jsonObject = new JSONObject(response.body().string()).getJSONObject("data").getJSONObject("player");
                UUID uuid = UUID.fromString(jsonObject.getString("id"));
                username = jsonObject.getString("username");
                databaseAdapter.putUsername(new UsernameModel(uuid, username, Instant.now().toEpochMilli()));
                return uuid;
            } catch (IOException e) {
                return null;
            }
        }
    }

    public @Nullable String getUsername(@NonNull DatabaseAdapter databaseAdapter, @NonNull UUID uuid) {
        UsernameModel usernameModel;
        if ((usernameModel = databaseAdapter.getUsernameModel(uuid)) != null && usernameModel.getLastUpdated() >= Instant.now().toEpochMilli() - timeout) return usernameModel.getUsername();
        else {
            final OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder().url("https://playerdb.co/api/player/minecraft/" + uuid).build();
            try (Response response = okHttpClient.newCall(request).execute()) {
                String username = new JSONObject(response.body().string()).getJSONObject("data").getJSONObject("player").getString("username");
                databaseAdapter.putUsername(new UsernameModel(uuid, username, Instant.now().toEpochMilli()));
                return username;
            } catch (IOException e) {
                return null;
            }
        }
    }

    public @NonNull String getThumbnail(@NonNull UUID uuid) {
        return "https://minotar.net/armor/bust/" + uuid;
    }
}
