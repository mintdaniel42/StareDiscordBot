package org.mintdaniel42.starediscordbot;

import lombok.NonNull;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.mintdaniel42.starediscordbot.buttons.ApproveChangeButton;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.*;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.util.ResourceBundle;

public final class Bot extends ListenerAdapter {
    public static final ResourceBundle strings = ResourceBundle.getBundle("strings", Options.getLocale());
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
                Commands.slash(CommandNames.maintenance.name(), strings.getString("control_maintenance"))
                        .addOption(OptionType.BOOLEAN, "active", strings.getString("if_maintenance_should_be_enabled"), true),

                Commands.slash(CommandNames.addhnsuser.name(), strings.getString("add_a_new_hide_n_seek_entry"))
                        .addOption(OptionType.STRING, "username", strings.getString("minecraft_username"), true, true)
                        .addOption(OptionType.NUMBER, "points", strings.getString("points"), true, true)
                        .addOption(OptionType.STRING, "rating", strings.getString("rating"))
                        .addOption(OptionType.STRING, "joined", strings.getString("joined"))
                        .addOption(OptionType.BOOLEAN, "secondary", strings.getString("secondary"))
                        .addOption(OptionType.BOOLEAN, "banned", strings.getString("banned"))
                        .addOption(OptionType.BOOLEAN, "cheating", strings.getString("cheating")),

                Commands.slash(CommandNames.addpguser.name(), strings.getString("add_a_new_partygames_entry"))
                        .addOption(OptionType.STRING, "username", strings.getString("minecraft_username"), true, true)
                        .addOption(OptionType.NUMBER, "points", strings.getString("points"), true, true)
                        .addOption(OptionType.STRING, "rating", strings.getString("rating"))
                        .addOption(OptionType.STRING, "joined", strings.getString("joined"))
                        .addOption(OptionType.NUMBER, "luck", strings.getString("luck"))
                        .addOption(OptionType.NUMBER, "quota", strings.getString("quota"))
                        .addOption(OptionType.NUMBER, "winrate", strings.getString("winrate")),

                Commands.slash(CommandNames.edithnsuser.name(), strings.getString("edit_a_hide_n_seek_entry"))
                        .addOption(OptionType.STRING, "username", strings.getString("minecraft_username"), true, true)
                        .addOption(OptionType.NUMBER, "points", strings.getString("points"), false,true)
                        .addOption(OptionType.STRING, "rating", strings.getString("rating"))
                        .addOption(OptionType.STRING, "joined", strings.getString("joined"))
                        .addOption(OptionType.BOOLEAN, "secondary", strings.getString("secondary"))
                        .addOption(OptionType.BOOLEAN, "banned", strings.getString("banned"))
                        .addOption(OptionType.BOOLEAN, "cheating", strings.getString("cheating")),

                Commands.slash(CommandNames.editpguser.name(), strings.getString("edit_a_partygames_entry"))
                        .addOption(OptionType.STRING, "username", strings.getString("minecraft_username"), true, true)
                        .addOption(OptionType.NUMBER, "points", strings.getString("points"), false,true)
                        .addOption(OptionType.STRING, "rating", strings.getString("rating"))
                        .addOption(OptionType.STRING, "joined", strings.getString("joined"))
                        .addOption(OptionType.NUMBER, "luck", strings.getString("luck"))
                        .addOption(OptionType.NUMBER, "quota", strings.getString("quota"))
                        .addOption(OptionType.NUMBER, "winrate", strings.getString("winrate")),

                Commands.slash(CommandNames.showhnsuser.name(), strings.getString("show_hide_n_seek_entry"))
                        .addOption(OptionType.STRING, "username", strings.getString("minecraft_username"), true, true),

                Commands.slash(CommandNames.showpguser.name(), strings.getString("show_partygames_entry"))
                        .addOption(OptionType.STRING, "username", strings.getString("minecraft_username"), true, true),

                Commands.slash(CommandNames.listhnsusers.name(), strings.getString("list_hide_n_seek_entries"))
                        .addOption(OptionType.INTEGER, "page", strings.getString("page"), false, true),

                Commands.slash(CommandNames.listpgusers.name(), strings.getString("list_partygames_entries"))
                        .addOption(OptionType.INTEGER, "page", strings.getString("page"), false, true),

                Commands.slash(CommandNames.approvechange.name(), strings.getString("approve_a_change"))
                        .addOption(OptionType.INTEGER, "id", strings.getString("change_id"), false, true)
        ).queue();
    }

    public static void main(final String[] args) throws Exception {
        new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
    }

    public enum CommandNames {
        maintenance,
        addhnsuser,
        addpguser,
        edithnsuser,
        editpguser,
        showhnsuser,
        showpguser,
        listhnsusers,
        listpgusers,
        approvechange
    }
}
