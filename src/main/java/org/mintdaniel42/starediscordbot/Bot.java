package org.mintdaniel42.starediscordbot;

import fr.leonarddoo.dba.loader.DBALoader;
import lombok.NonNull;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.mintdaniel42.starediscordbot.buttons.ListButtons;
import org.mintdaniel42.starediscordbot.commands.*;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.utils.Options;

import java.util.ResourceBundle;

public final class Bot extends ListenerAdapter {
    public static final ResourceBundle strings = ResourceBundle.getBundle("strings", Options.getLocale());
    private final DatabaseAdapter databaseAdapter;

    public Bot(@NonNull DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        JDABuilder.createLight(Options.getToken())
                .addEventListeners(this)
                .build();
    }

    @Override
    public void onShutdown(@NonNull ShutdownEvent event) {
        try {
            databaseAdapter.close();
        } catch (Exception ignored) {}
    }

    @Override
    public void onGuildReady(@NonNull GuildReadyEvent event) {
        // check if correct guild
        if (event.getGuild().getIdLong() != Options.getGuildId()) return;

        // setup commands
        DBALoader.getInstance(event.getJDA()).addDBACommandsToGuild(event.getGuild(),
                new MaintenanceCommand(),

                new AddHNSUserCommand(databaseAdapter),
                new AddPGUserCommand(databaseAdapter),

                new EditHNSUserCommand(databaseAdapter),
                new EditPGUserCommand(databaseAdapter),

                new ShowHNSUserCommand(databaseAdapter),
                new ShowPGUserCommand(databaseAdapter),

                new ListHNSUsersCommand(databaseAdapter),   // TODO: buttons
                new ListPGUsersCommand(databaseAdapter)//,    // TODO: buttons

                //new ApproveChangeCommand(databaseAdapter)   // TODO: everything
        );

        ListButtons listButtons = new ListButtons();

        DBALoader.getInstance(event.getJDA()).initDBAEvent(
                listButtons.getPreviousPageButton(),    // TODO
                listButtons.getNextPageButton()         // TODO
        );
    }

    public static void main(String[] args) throws Exception {
        new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
    }
}
