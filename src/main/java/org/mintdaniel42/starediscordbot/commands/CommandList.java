package org.mintdaniel42.starediscordbot.commands;

import lombok.NonNull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.mintdaniel42.starediscordbot.data.AchievementModel;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.utils.R;

public enum CommandList {
	user,
	hns,
	pg,
	group,
	maintenance,
	approve,
	info
	//#if dev
	//$$ , streak;
	//#else
	;
	//#endif

	public @NonNull CommandData get() {
		return switch (this) {
			case user -> getUserCommand();
			case hns -> getHnsCommand();
			case pg -> getPgCommand();
			case group -> getGroupCommand();
			case maintenance -> getMaintenanceCommand();
			case approve -> getApproveCommand();
			case info -> Commands.slash("info", R.Strings.ui("show_bot_information"));
			//#if dev
			//$$ case streak -> getStreakCommand();
			//#endif
		};
	}

	private @NonNull CommandData getUserCommand() {
		return Commands.slash("user", R.Strings.ui("user_related_commands"))
				.addSubcommands(
						new SubcommandData("edit", R.Strings.ui("edit_a_user_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
								.addOption(OptionType.STRING, "note", R.Strings.ui("note"))
								.addOption(OptionType.USER, "discord", R.Strings.ui("discord_tag")),
						new SubcommandData("delete", R.Strings.ui("delete_a_user_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
				);
	}

	private @NonNull CommandData getHnsCommand() {
		return Commands.slash("hns", R.Strings.ui("hide_n_seek_related_commands"))
				.addSubcommands(
						new SubcommandData("show", R.Strings.ui("show_hide_n_seek_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true),
						new SubcommandData("showmore", R.Strings.ui("show_hide_n_seek_entry_more"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true),
						new SubcommandData("edit", R.Strings.ui("edit_a_hide_n_seek_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
								.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), false, true)
								.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
								.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
								.addOption(OptionType.BOOLEAN, "secondary", R.Strings.ui("secondary"))
								.addOption(OptionType.BOOLEAN, "banned", R.Strings.ui("banned"))
								.addOption(OptionType.BOOLEAN, "cheating", R.Strings.ui("cheating"))
								.addOption(OptionType.STRING, "top10", R.Strings.ui("top10"))
								.addOption(OptionType.INTEGER, "streak", R.Strings.ui("streak"))
								.addOption(OptionType.STRING, "highest_rank", R.Strings.ui("highest_rank")),
						new SubcommandData("add", R.Strings.ui("add_a_new_hide_n_seek_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
								.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), true, true)
								.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
								.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
								.addOption(OptionType.BOOLEAN, "secondary", R.Strings.ui("secondary"))
								.addOption(OptionType.BOOLEAN, "banned", R.Strings.ui("banned"))
								.addOption(OptionType.BOOLEAN, "cheating", R.Strings.ui("cheating"))
								.addOption(OptionType.STRING, "top10", R.Strings.ui("top10"))
								.addOption(OptionType.INTEGER, "streak", R.Strings.ui("streak"))
								.addOption(OptionType.STRING, "highest_rank", R.Strings.ui("highest_rank")),
						new SubcommandData("list", R.Strings.ui("list_hide_n_seek_entries"))
								.addOption(OptionType.INTEGER, "page", R.Strings.ui("page"), false, true)
						//#if dev
						, new SubcommandData("tutorial", R.Strings.ui("show_the_tutorial"))
								.addOption(OptionType.STRING, "page", R.Strings.ui("page"), false, true)
				)
				.addSubcommandGroups(
						new SubcommandGroupData("achievements", R.Strings.ui("achievements_related_commands"))
								.addSubcommands(
										new SubcommandData("add", R.Strings.ui("add_an_achievement"))
												.addOption(OptionType.STRING, "name", R.Strings.ui("achievement_name"), true)
												.addOption(OptionType.STRING, "description", R.Strings.ui("achievement_description"), true)
												.addOptions(new OptionData(OptionType.STRING, "type", R.Strings.ui("achievement_type"), true)
														.addChoice(AchievementModel.Type.riddle.name(), R.Strings.ui("riddle"))
														.addChoice(AchievementModel.Type.normal.name(), R.Strings.ui("normal"))
														.addChoice(AchievementModel.Type.longterm.name(), R.Strings.ui("longterm")))
												.addOption(OptionType.STRING, "points", R.Strings.ui("achievement_points"), true),
										new SubcommandData("list", R.Strings.ui("show_achievements"))
												.addOption(OptionType.STRING, "points", R.Strings.ui("filter_by_achievement_points"), false)
												.addOption(OptionType.INTEGER, "page", R.Strings.ui("page"), false, true)
												.addOptions(new OptionData(OptionType.STRING, "type", R.Strings.ui("filter_by_achievement_type"))
														.addChoice(AchievementModel.Type.riddle.name(), R.Strings.ui("riddle"))
														.addChoice(AchievementModel.Type.normal.name(), R.Strings.ui("normal"))
														.addChoice(AchievementModel.Type.longterm.name(), R.Strings.ui("longterm")))
								)
				)
				.addSubcommandGroups(
						new SubcommandGroupData("maps", R.Strings.ui("maps_related_command"))
								.addSubcommands(
										new SubcommandData("add", R.Strings.ui("add_a_map"))
												.addOption(OptionType.STRING, "name", R.Strings.ui("the_maps_name"), true)
												.addOption(OptionType.STRING, "builder", R.Strings.ui("the_builders_names"), true, true)
												.addOption(OptionType.STRING, "release", R.Strings.ui("the_maps_release_date"), true)
												.addOption(OptionType.ATTACHMENT, "picture", R.Strings.ui("a_picture_of_the_map"), true)
												.addOptions(new OptionData(OptionType.STRING, "difficulty", R.Strings.ui("the_maps_difficulty"), true)
														.addChoice("easy", "easy")
														.addChoice("medium", "medium")
														.addChoice("hard", "hard")
														.addChoice("ultra", "ultra")
												)
								));
		//#else
		//$$ );
		//#endif
	}

	private @NonNull CommandData getPgCommand() {
		return Commands.slash("pg", R.Strings.ui("partygames_related_commands"))
				.addSubcommands(
						new SubcommandData("show", R.Strings.ui("show_partygames_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true),
						new SubcommandData("edit", R.Strings.ui("edit_a_partygames_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
								.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), false, true)
								.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
								.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
								.addOption(OptionType.NUMBER, "luck", R.Strings.ui("luck"), false, true)
								.addOption(OptionType.NUMBER, "quota", R.Strings.ui("quota"))
								.addOption(OptionType.NUMBER, "winrate", R.Strings.ui("winrate")),
						new SubcommandData("add", R.Strings.ui("add_a_new_partygames_entry"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
								.addOption(OptionType.NUMBER, "points", R.Strings.ui("points"), true, true)
								.addOption(OptionType.STRING, "rating", R.Strings.ui("rating"))
								.addOption(OptionType.STRING, "joined", R.Strings.ui("joined"))
								.addOption(OptionType.NUMBER, "luck", R.Strings.ui("luck"), false, true)
								.addOption(OptionType.NUMBER, "quota", R.Strings.ui("quota"))
								.addOption(OptionType.NUMBER, "winrate", R.Strings.ui("winrate")),
						new SubcommandData("list", R.Strings.ui("list_partygames_entries"))
								.addOption(OptionType.INTEGER, "page", R.Strings.ui("page"), false, true));
	}

	private @NonNull CommandData getGroupCommand() {
		return Commands.slash("group", R.Strings.ui("group_related_commands"))
				.addSubcommandGroups(new SubcommandGroupData("user", R.Strings.ui("user_related_group_commands"))
						.addSubcommands(
								new SubcommandData("add", R.Strings.ui("add_user_to_group"))
										.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true, true)
										.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true),
								new SubcommandData("remove", R.Strings.ui("remove_user_from_group"))
										.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true),
								new SubcommandData("show", R.Strings.ui("show_group_of_user"))
										.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
						)
				)
				.addSubcommands(
						new SubcommandData("show", R.Strings.ui("show_group"))
								.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true, true),
						new SubcommandData("create", R.Strings.ui("create_group"))
								.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true)
								.addOption(OptionType.STRING, "name", R.Strings.ui("group_name"), true)
								.addOption(OptionType.STRING, "leader", R.Strings.ui("group_leader"), true, true)
								.addOptions(new OptionData(OptionType.STRING, "relation", R.Strings.ui("group_relation"), true)
										.addChoice(R.Strings.ui("enemy"), GroupModel.Relation.enemy.name())
										.addChoice(R.Strings.ui("neutral"), GroupModel.Relation.neutral.name())
										.addChoice(R.Strings.ui("ally"), GroupModel.Relation.ally.name())),
						new SubcommandData("edit", R.Strings.ui("edit_group"))
								.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true, true)
								.addOption(OptionType.STRING, "name", R.Strings.ui("group_name"))
								.addOption(OptionType.STRING, "leader", R.Strings.ui("group_leader"), false, true)
								.addOptions(new OptionData(OptionType.STRING, "relation", R.Strings.ui("group_relation"))
										.addChoice(R.Strings.ui("enemy"), GroupModel.Relation.enemy.name())
										.addChoice(R.Strings.ui("neutral"), GroupModel.Relation.neutral.name())
										.addChoice(R.Strings.ui("ally"), GroupModel.Relation.ally.name())),
						new SubcommandData("delete", R.Strings.ui("delete_group"))
								.addOption(OptionType.STRING, "tag", R.Strings.ui("group_tag"), true, true)
				);
	}

	private @NonNull CommandData getMaintenanceCommand() {
		return Commands.slash("maintenance", R.Strings.ui("control_maintenance"))
				.addOption(OptionType.BOOLEAN, "active", R.Strings.ui("if_maintenance_should_be_enabled"), true)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
	}

	private @NonNull CommandData getApproveCommand() {
		return Commands.slash("approve", R.Strings.ui("approve_a_change"))
				.addOption(OptionType.INTEGER, "id", R.Strings.ui("change_id"), true, true);
	}

	//#if dev
	private @NonNull CommandData getStreakCommand() {
		return Commands.slash("streak", R.Strings.ui("manage_the_streak_of_a_user"))
				.addSubcommands(
						new SubcommandData("start", R.Strings.ui("start_a_users_streak"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true),
						new SubcommandData("stop", R.Strings.ui("stop_a_users_streak"))
								.addOption(OptionType.STRING, "username", R.Strings.ui("minecraft_username"), true, true)
				);
	}
	//#endif
}
