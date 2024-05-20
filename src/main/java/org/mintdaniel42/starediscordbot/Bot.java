package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.mintdaniel42.starediscordbot.build.Features;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.*;
import org.mintdaniel42.starediscordbot.commands.group.CreateGroupCommand;
import org.mintdaniel42.starediscordbot.commands.group.ShowGroupCommand;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.db.GroupModel;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

@Slf4j
public final class Bot extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

	public Bot(@NonNull final DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;

        JDABuilder.createLight(Options.getToken())
                .addEventListeners(
                        new AutoCompletionHandler(databaseAdapter),

                        new ListButtons(databaseAdapter),

                        new MaintenanceCommand(),

                        new HNSCommand(databaseAdapter),
                        new PGCommand(databaseAdapter),

                        new CreateGroupCommand(databaseAdapter),
                        new ShowGroupCommand(databaseAdapter),

                        new ApproveChangeCommand(databaseAdapter),

                        this
                )
                .build();
    }

    @Override
    public void onShutdown(@NonNull final ShutdownEvent event) {
        try {
            databaseAdapter.close();
        } catch (Exception e) {
            log.error(R.logging("could_not_close_database"), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onGuildReady(@NonNull final GuildReadyEvent event) {
        // check if correct guild
        if (event.getGuild().getIdLong() != Options.getGuildId()) return;

        // setup commands
        CommandListUpdateAction commandListUpdateAction = event.getGuild().updateCommands().addCommands(
                Commands.slash("maintenance", R.string("control_maintenance"))
                        .addOption(OptionType.BOOLEAN, "active", R.string("if_maintenance_should_be_enabled"), true),

                Commands.slash("hns", R.string("hide_n_seek_related_commands"))
                        .addSubcommands(
                                new SubcommandData("show", R.string("show_hide_n_seek_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true)
                                        .addOption(OptionType.BOOLEAN, "more", R.string("more_info")),
                                new SubcommandData("edit", R.string("edit_a_hide_n_seek_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true)
                                        .addOption(OptionType.NUMBER, "points", R.string("points"), false, true)
                                        .addOption(OptionType.STRING, "rating", R.string("rating"))
                                        .addOption(OptionType.STRING, "joined", R.string("joined"))
                                        .addOption(OptionType.BOOLEAN, "secondary", R.string("secondary"))
                                        .addOption(OptionType.BOOLEAN, "banned", R.string("banned"))
                                        .addOption(OptionType.BOOLEAN, "cheating", R.string("cheating"))
                                        .addOption(OptionType.STRING, "top10", R.string("top10"))
                                        .addOption(OptionType.INTEGER, "streak", R.string("streak"))
                                        .addOption(OptionType.STRING, "highest_rank", R.string("highest_rank"))
                                        .addOption(OptionType.STRING, "note", R.string("note"), false, true)
                                        .addOption(OptionType.USER, "discord", R.string("discord_tag"))
                                        .addOption(OptionType.STRING, "tag", R.string("group_tag")),
                                new SubcommandData("add", R.string("add_a_new_hide_n_seek_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true)
                                        .addOption(OptionType.NUMBER, "points", R.string("points"), true, true)
                                        .addOption(OptionType.STRING, "rating", R.string("rating"))
                                        .addOption(OptionType.STRING, "joined", R.string("joined"))
                                        .addOption(OptionType.BOOLEAN, "secondary", R.string("secondary"))
                                        .addOption(OptionType.BOOLEAN, "banned", R.string("banned"))
                                        .addOption(OptionType.BOOLEAN, "cheating", R.string("cheating")),
                                new SubcommandData("list", R.string("list_hide_n_seek_entries"))
                                        .addOption(OptionType.INTEGER, "page", R.string("page"), false, true)
                        ),

                Commands.slash("pg", R.string("partygames_related_commands"))
                        .addSubcommands(
                                new SubcommandData("show", R.string("show_partygames_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true),
                                new SubcommandData("edit", R.string("edit_a_partygames_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true)
                                        .addOption(OptionType.NUMBER, "points", R.string("points"), false, true)
                                        .addOption(OptionType.STRING, "rating", R.string("rating"))
                                        .addOption(OptionType.STRING, "joined", R.string("joined"))
                                        .addOption(OptionType.NUMBER, "luck", R.string("luck"))
                                        .addOption(OptionType.NUMBER, "quota", R.string("quota"))
                                        .addOption(OptionType.NUMBER, "winrate", R.string("winrate")),
                                new SubcommandData("add", R.string("add_a_new_partygames_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true)
                                        .addOption(OptionType.NUMBER, "points", R.string("points"), true, true)
                                        .addOption(OptionType.STRING, "rating", R.string("rating"))
                                        .addOption(OptionType.STRING, "joined", R.string("joined"))
                                        .addOption(OptionType.NUMBER, "luck", R.string("luck"))
                                        .addOption(OptionType.NUMBER, "quota", R.string("quota"))
                                        .addOption(OptionType.NUMBER, "winrate", R.string("winrate")),
                                new SubcommandData("list", R.string("list_partygames_entries"))
                                        .addOption(OptionType.INTEGER, "page", R.string("page"), false, true)
                        ),

                Commands.slash("approve", R.string("approve_a_change"))
                        .addOption(OptionType.INTEGER, "id", R.string("change_id"), true, true)
        );

        if (Features.dev) {
            commandListUpdateAction.addCommands(
    Commands.slash("group", R.string("group_related_commands"))
                    .addSubcommandGroups(new SubcommandGroupData("user", R.string("user_related_group_commands"))
                            .addSubcommands(
                                    new SubcommandData("add", R.string("add_user_to_group"))
                                            .addOption(OptionType.STRING, "tag", R.string("group_tag"), true, true)
                                            .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true),
                                    new SubcommandData("remove", R.string("remove_user_from_group"))
                                            .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true),
                                    new SubcommandData("show", R.string("show_group_of_user"))
                                            .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true)
                            )
                    )
                    .addSubcommands(
                            new SubcommandData("show", R.string("show_group"))
                                    .addOption(OptionType.STRING, "tag", R.string("group_tag"), true, true),
                            new SubcommandData("create", R.string("create_group"))
                                    .addOption(OptionType.STRING, "tag", R.string("group_tag"), true)
                                    .addOption(OptionType.STRING, "name", R.string("group_name"), true)
                                    .addOption(OptionType.STRING, "leader", R.string("group_leader"), true, true)
                                    .addOptions(new OptionData(OptionType.STRING, "relation", R.string("group_relation"), true)
                                            .addChoice(R.string("enemy"), GroupModel.Relation.enemy.name())
                                            .addChoice(R.string("neutral"), GroupModel.Relation.neutral.name())
                                            .addChoice(R.string("ally"), GroupModel.Relation.ally.name())),
                            new SubcommandData("edit", R.string("edit_group"))
                                    .addOption(OptionType.STRING, "tag", R.string("group_tag"), true, true)
                                    .addOption(OptionType.STRING, "name", R.string("group_name"))
                                    .addOption(OptionType.STRING, "leader", R.string("group_leader"), false, true)
                                    .addOptions(new OptionData(OptionType.STRING, "relation", R.string("group_relation"))
                                            .addChoice(R.string("enemy"), GroupModel.Relation.enemy.name())
                                            .addChoice(R.string("neutral"), GroupModel.Relation.neutral.name())
                                            .addChoice(R.string("ally"), GroupModel.Relation.ally.name())),
                            new SubcommandData("delete", R.string("delete_group"))
                                    .addOption(OptionType.STRING, "tag", R.string("group_tag"), true, true)
                                    .addOption(OptionType.BOOLEAN, "confirm", R.string("confirm_deletion"), true)
                    )).queue();
        }

        commandListUpdateAction.queue();
    }

    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (!event.isAcknowledged()) event.reply(R.string("it_looks_like_you_found_an_unfinished_command")).queue();
    }

    @Override
    public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
        if (!event.isAcknowledged()) event.reply(R.string("this_button_is_not_yet_ready_to_be_pressed")).queue();
    }

    public static void main(@NonNull final String[] args) throws Exception {
        new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
    }
}
