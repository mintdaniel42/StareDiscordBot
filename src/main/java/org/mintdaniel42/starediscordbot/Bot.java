package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.*;
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
                        new ApproveChangeButton(databaseAdapter),

                        new MaintenanceCommand(),
                        new HelpCommand(),

                        new UserCommand(databaseAdapter),
                        new HNSCommand(databaseAdapter),
                        new PGCommand(databaseAdapter),
                        //#if dev
                        new GroupCommand(databaseAdapter),
                        //#endif
                        new ApproveChangeCommand(databaseAdapter),

                        this
                )
                .build();
    }

    public static void main(@NonNull final String[] args) throws Exception {
        //#if dev
        log.info(R.Strings.log("running_in_dev_mode"));
        //#endif
        new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
    }

    @Override
    public void onGuildReady(@NonNull final GuildReadyEvent event) {
        // check if correct guild
        if (event.getGuild().getIdLong() != Options.getGuildId()) return;

        // setup commands
        event.getGuild().updateCommands()
                .addCommands(addBasicCommands())
                .addCommands(addHnsCommands())
                .addCommands(addPgCommands())
                .addCommands(addUserCommands())
                //#if dev
                .addCommands(addGroupCommands())
                //#endif
                .queue();
    }

    @Override
    public void onShutdown(@NonNull final ShutdownEvent event) {
        try {
            databaseAdapter.close();
        } catch (Exception e) {
            log.error(R.Strings.log("could_not_close_database"), e);
            throw new RuntimeException(e);
        }
    }

    //#if dev
    @Override
    public void onSlashCommandInteraction(@NonNull final SlashCommandInteractionEvent event) {
        if (event.getMember() instanceof Member member) {
            log.info(R.Strings.log("command_s_invoked_by_user_s",
                    event.getFullCommandName(),
                    member.getEffectiveName()));
        }
    }
    //#endif

    @Contract(" -> new")
    private @NonNull SlashCommandData[] addBasicCommands() {
        return new SlashCommandData[]{
                Commands.slash("maintenance", R.Strings.ui("control_maintenance"))
                        .addOption(OptionType.BOOLEAN, "active", R.Strings.ui("if_maintenance_should_be_enabled"), true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.slash("approve", R.Strings.ui("approve_a_change"))
                        .addOption(OptionType.INTEGER, "id", R.Strings.ui("change_id"), true, true)
                //#if dev
                , Commands.slash("help", R.Strings.ui("list_all_commands"))
                //#endif
        };
    }

    @Contract(" -> new")
    private @NonNull SlashCommandData addHnsCommands() {
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
                );
    }

    @Contract(" -> new")
    private @NonNull SlashCommandData addPgCommands() {
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

    @Contract(" -> new")
    private @NonNull SlashCommandData addUserCommands() {
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

    //#if dev
    @Contract(" -> new")
    private @NonNull SlashCommandData addGroupCommands() {
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
                                .addOption(OptionType.BOOLEAN, "confirm", R.Strings.ui("confirm_deletion"), true)
                );
    }
    //#endif

    @Override
    public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
        if (event.getMember() instanceof Member member) {
            log.info(R.Strings.log("button_s_pressed_by_user_s",
                    event.getComponentId(),
                    member.getEffectiveName()));
        }
    }
}
