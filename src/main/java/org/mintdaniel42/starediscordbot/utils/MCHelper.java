package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.UsernameModel;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@UtilityClass
@Slf4j
public class MCHelper {
    public @Nullable UUID getUuid(@NonNull final DatabaseAdapter databaseAdapter, @NonNull final String username) {
        if (databaseAdapter.getUsernameModel(username) instanceof UsernameModel usernameModel) return usernameModel.getUuid();
        else {
            final var okHttpClient = new OkHttpClient();
            final var request = new Request.Builder().url("https://playerdb.co/api/player/minecraft/" + username).build();
            try(final var response = okHttpClient.newCall(request).execute()) {
                if (response.body() instanceof ResponseBody responseBody) {
                    final var jsonObject = new JSONObject(responseBody.string()).getJSONObject("data").getJSONObject("player");
                    final var uuid = UUID.fromString(jsonObject.getString("id"));
                    databaseAdapter.putUsername(new UsernameModel(uuid, jsonObject.getString("username"), Instant.now().toEpochMilli()));
                    return uuid;
                } else return null;
            } catch (IOException | JSONException e) {
                log.error(R.logging("could_not_fetch_uuid"), e);
                return null;
            }
        }
    }

    public @Nullable String getUsername(@NonNull DatabaseAdapter databaseAdapter, @NonNull UUID uuid) {
        if (databaseAdapter.getUsernameModel(uuid) instanceof UsernameModel usernameModel) return usernameModel.getUsername();
        else {
            final var okHttpClient = new OkHttpClient();
            final var request = new Request.Builder().url("https://playerdb.co/api/player/minecraft/" + uuid).build();
            try (final var response = okHttpClient.newCall(request).execute()) {
                if (response.body() instanceof ResponseBody responseBody) {
                    final var username = new JSONObject(responseBody.string()).getJSONObject("data").getJSONObject("player").getString("username");
                    databaseAdapter.putUsername(new UsernameModel(uuid, username, Instant.now().toEpochMilli()));
                    return username;
                } else return null;
            } catch (IOException e) {
                log.error(R.logging("could_not_fetch_username"), e);
                return null;
            }
        }
    }

    public @NonNull String getThumbnail(@NonNull final UUID uuid) {
        return "https://minotar.net/armor/bust/" + uuid;
    }
}
