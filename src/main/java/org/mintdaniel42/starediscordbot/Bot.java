package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.*;
import org.mintdaniel42.starediscordbot.commands.hns.AddHNSUserCommand;
import org.mintdaniel42.starediscordbot.commands.hns.EditHNSUserCommand;
import org.mintdaniel42.starediscordbot.commands.hns.ListHNSUsersCommand;
import org.mintdaniel42.starediscordbot.commands.hns.ShowHNSUserCommand;
import org.mintdaniel42.starediscordbot.commands.pg.AddPGUserCommand;
import org.mintdaniel42.starediscordbot.commands.pg.EditPGUserCommand;
import org.mintdaniel42.starediscordbot.commands.pg.ListPGUsersCommand;
import org.mintdaniel42.starediscordbot.commands.pg.ShowPGUserCommand;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.Options;
import org.mintdaniel42.starediscordbot.utils.R;

public final class Bot extends ListenerAdapter {
    @NotNull private final DatabaseAdapter databaseAdapter;

	public Bot(@NonNull final DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;

        JDABuilder.createLight(Options.getToken())
                .addEventListeners(
                        new ListButtons(databaseAdapter),
                        new ApproveChangeButton(databaseAdapter),

                        new MaintenanceCommand(),
                        new AddHNSUserCommand(databaseAdapter),
                        new AddPGUserCommand(databaseAdapter),
                        new EditHNSUserCommand(databaseAdapter),
                        new EditPGUserCommand(databaseAdapter),
                        new ShowHNSUserCommand(databaseAdapter),
                        new ShowPGUserCommand(databaseAdapter),
                        new ListHNSUsersCommand(databaseAdapter),
                        new ListPGUsersCommand(databaseAdapter),
                        new ApproveChangeCommand(databaseAdapter),

                        this
                )
                .build();
    }

    @Override
    public void onShutdown(@NonNull final ShutdownEvent event) {
        try {
            databaseAdapter.close();
        } catch (Exception ignored) {}
    }

    @Override
    public void onGuildReady(@NonNull final GuildReadyEvent event) {
        // check if correct guild
        if (event.getGuild().getIdLong() != Options.getGuildId()) return;

        // setup commands
        event.getGuild().updateCommands().addCommands(
                Commands.slash("maintenance", R.string("control_maintenance"))
                        .addOption(OptionType.BOOLEAN, "active", R.string("if_maintenance_should_be_enabled"), true),

                Commands.slash("hns", "Hide 'n' Seek")
                        .addSubcommands(
                                new SubcommandData("show", R.string("show_hide_n_seek_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true),
                                new SubcommandData("edit", R.string("edit_a_hide_n_seek_entry"))
                                        .addOption(OptionType.STRING, "username", R.string("minecraft_username"), true, true)
                                        .addOption(OptionType.NUMBER, "points", R.string("points"), false, true)
                                        .addOption(OptionType.STRING, "rating", R.string("rating"))
                                        .addOption(OptionType.STRING, "joined", R.string("joined"))
                                        .addOption(OptionType.BOOLEAN, "secondary", R.string("secondary"))
                                        .addOption(OptionType.BOOLEAN, "banned", R.string("banned"))
                                        .addOption(OptionType.BOOLEAN, "cheating", R.string("cheating")),
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

                Commands.slash("pg", "Partygames")
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
                        .addOption(OptionType.INTEGER, "id", R.string("change_id"), false, true)
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        if (!event.isAcknowledged()) event.reply(R.string("it_looks_like_you_found_an_unfinished_command")).queue();
    }

    public static void main(final String[] args) throws Exception {
        new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
    }
}
