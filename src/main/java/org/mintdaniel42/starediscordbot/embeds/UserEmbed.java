package org.mintdaniel42.starediscordbot.embeds;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.db.UserModel;
import org.mintdaniel42.starediscordbot.utils.MCHelper;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

import java.text.DecimalFormat;

@UtilityClass
public class UserEmbed {
    @Contract(pure = true, value = "_, _ -> new")
    public @NonNull MessageEmbed of(@NonNull final UserModel userModel, @NonNull final Type type) {
        return of(userModel, type, false);
    }

    @Contract(pure = true, value = "_, _, _ -> new")
    public @NonNull MessageEmbed of(@NonNull final UserModel userModel, @NonNull final Type type, final boolean isRequest) {
        final var builder = new EmbedBuilder()
                .setDescription(userModel.getUsername())
                .setColor(isRequest ? Options.getColorRequest() : Options.getColorNormal())
                .setThumbnail(MCHelper.getThumbnail(userModel.getUuid()));

        return switch (type) {
            case BASE -> buildBaseEmbed(userModel, builder);
	        case HNS -> buildHnsEmbed(userModel, builder);
            case HNS_MORE -> buildHnsMoreEmbed(userModel, builder);
            case HNS_ALL -> buildHnsAllEmbed(userModel, builder);
            case PG -> buildPgEmbed(userModel, builder);
        };
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildBaseEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
		return builder.setTitle(R.Strings.ui("user_base_info"))
				.addField(R.Strings.ui("group_name"), userModel.getGroup() != null ? userModel.getGroup().getName() : "❌", true)
				.addField(R.Strings.ui("discord_tag"), userModel.getDiscord() == 0 ? "❌" : "<@%s>".formatted(userModel.getDiscord()), true)
				.addField(R.Strings.ui("note"), userModel.getNote(), false)
                .build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildHnsEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
		return builder.setTitle(R.Strings.ui("hide_n_seek_player_database"))
				.addField(R.Strings.ui("rating"), userModel.getHnsUser().getRating(), true)
				.addField(R.Strings.ui("points"), formatNumber(userModel.getHnsUser().getPoints()), true)
				.addField(R.Strings.ui("joined"), userModel.getHnsUser().getJoined(), true)
				.addField(R.Strings.ui("note"), userModel.getNote(), userModel.getNote().length() <= 10)
				.addField(R.Strings.ui("banned"), userModel.getHnsUser().isBanned() ? "✅" : "❌", true)
				.addField(R.Strings.ui("cheating"), userModel.getHnsUser().isCheating() ? "✅" : "❌", true)
                .build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildHnsMoreEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
		return builder.setTitle(R.Strings.ui("hide_n_seek_player_database_more_information"))
				.addField(R.Strings.ui("top10"), userModel.getHnsUser().getTop10(), true)
				.addField(R.Strings.ui("streak"), String.valueOf(userModel.getHnsUser().getStreak()), true)
				.addField(R.Strings.ui("highest_rank"), userModel.getHnsUser().getHighestRank(), true)
				.addField(R.Strings.ui("secondary"), userModel.getHnsUser().isSecondary() ? "✅" : "❌", true)
				.addField(R.Strings.ui("discord_tag"), userModel.getDiscord() == 0 ? "❌" : "<@%s>".formatted(userModel.getDiscord()), true)
				.addField(R.Strings.ui("group_name"), userModel.getGroup() != null ? userModel.getGroup().getName() : "❌", true)
                .build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildHnsAllEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
		return builder.setTitle(R.Strings.ui("hide_n_seek_player_database"))
				.addField(R.Strings.ui("rating"), userModel.getHnsUser().getRating(), true)
				.addField(R.Strings.ui("points"), formatNumber(userModel.getHnsUser().getPoints()), true)
				.addField(R.Strings.ui("joined"), userModel.getHnsUser().getJoined(), true)
				.addField(R.Strings.ui("banned"), userModel.getHnsUser().isBanned() ? "✅" : "❌", true)
				.addField(R.Strings.ui("cheating"), userModel.getHnsUser().isCheating() ? "✅" : "❌", true)
				.addField(R.Strings.ui("top10"), userModel.getHnsUser().getTop10(), true)
				.addField(R.Strings.ui("streak"), String.valueOf(userModel.getHnsUser().getStreak()), true)
				.addField(R.Strings.ui("highest_rank"), userModel.getHnsUser().getHighestRank(), true)
				.addField(R.Strings.ui("secondary"), userModel.getHnsUser().isSecondary() ? "✅" : "❌", true)
                .build();
    }

    @Contract(pure = true, value = "_, _ -> new")
    private @NonNull MessageEmbed buildPgEmbed(@NonNull final UserModel userModel, @NonNull final EmbedBuilder builder) {
		return builder.setTitle(R.Strings.ui("partygames_player_database"))
                .setThumbnail(MCHelper.getThumbnail(userModel.getPgUser().getUuid()))
				.addField(R.Strings.ui("rating"), userModel.getPgUser().getRating(), true)
				.addField(R.Strings.ui("points"), formatNumber(userModel.getPgUser().getPoints()), true)
				.addField(R.Strings.ui("joined"), userModel.getPgUser().getJoined(), true)
				.addField(R.Strings.ui("luck"), String.valueOf(userModel.getPgUser().getLuck()), true)
				.addField(R.Strings.ui("quota"), String.format("%s%%", userModel.getPgUser().getQuota()), true)
				.addField(R.Strings.ui("winrate"), String.format("%s%%", userModel.getPgUser().getWinrate()), true)
                .build();
    }

    @Contract(pure = true, value = "_ -> new")
	private @NonNull String formatNumber(double value) {
		final var stringBuilder = new StringBuilder("##.0");

		if (value >= 10_000_000_000L) {
			value /= 1_000_000_000L;
			stringBuilder.append('B');
		} else if (value >= 10_000_000L) {
			value /= 1_000_000;
			stringBuilder.append('M');
		} else if (value >= 10_000) {
			value /= 1_000;
			stringBuilder.append('K');
		}

		return new DecimalFormat(stringBuilder.toString()).format(value);
    }

    public enum Type {
        BASE,
        HNS,
        HNS_MORE,
        HNS_ALL,
        PG
    }
}
