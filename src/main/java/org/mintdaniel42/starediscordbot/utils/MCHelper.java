package org.mintdaniel42.starediscordbot.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.mintdaniel42.starediscordbot.data.entity.UsernameEntity;
import org.mintdaniel42.starediscordbot.data.repository.UsernameRepository;

import java.io.IOException;
import java.util.UUID;

@UtilityClass
@Slf4j
public class MCHelper {
    public @Nullable UUID getUuid(@NonNull final UsernameRepository usernameRepository, @NonNull final String username) {
        final var usernameOptional = usernameRepository.selectByUsername(username);
        if (usernameOptional.isPresent()) return usernameOptional.get().getUuid();
        else {
            final var okHttpClient = new OkHttpClient();
            final var request = new Request.Builder().url("https://playerdb.co/api/player/minecraft/" + username).build();
            try (final var response = okHttpClient.newCall(request).execute()) {
                if (response.body() instanceof ResponseBody responseBody) {
                    final var jsonObject = new JSONObject(responseBody.string()).getJSONObject("data").getJSONObject("player");
                    final var uuid = UUID.fromString(jsonObject.getString("id"));
                    final var caseAwareUsername = jsonObject.getString("username");
                    usernameRepository.insert(UsernameEntity.builder()
                            .uuid(uuid)
                            .username(caseAwareUsername)
                            .lastUpdated(System.currentTimeMillis())
                            .build());
                    return uuid;
                } else return null;
            } catch (IOException e) {
                log.error(R.Strings.log("could_not_fetch_username"), e);
                return null;
            }
        }
    }

    public @Nullable String getUsername(@NonNull UsernameRepository usernameRepository, @NonNull UUID uuid) {
        final var usernameOptional = usernameRepository.selectById(uuid);
        if (usernameOptional.isPresent()) return usernameOptional.get().getUsername();
        else {
            final var okHttpClient = new OkHttpClient();
            final var request = new Request.Builder().url("https://playerdb.co/api/player/minecraft/" + uuid).build();
            try (final var response = okHttpClient.newCall(request).execute()) {
                if (response.body() instanceof ResponseBody responseBody) {
                    final var username = new JSONObject(responseBody.string()).getJSONObject("data").getJSONObject("player").getString("username");
                    usernameRepository.insert(UsernameEntity.builder()
                            .uuid(uuid)
                            .username(username)
                            .lastUpdated(System.currentTimeMillis())
                            .build());
                    return username;
                } else return null;
            } catch (IOException e) {
                log.error(R.Strings.log("could_not_fetch_username"), e);
                return null;
            }
        }
    }

    public @NonNull String getThumbnail(@NonNull final UUID uuid) {
        return "https://minotar.net/armor/bust/" + uuid;
    }
}
