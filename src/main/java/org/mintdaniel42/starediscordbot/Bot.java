package org.mintdaniel42.starediscordbot;

import fr.leonarddoo.dba.loader.DBALoader;
import lombok.NonNull;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
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
		ListenerAdapter listButtons = new ListButtons(databaseAdapter);

        JDABuilder.createLight(Options.getToken())
                .addEventListeners(listButtons, this)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
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

                new ListHNSUsersCommand(databaseAdapter),
                new ListPGUsersCommand(databaseAdapter),

                new ApproveChangeCommand(databaseAdapter)
        );
    }

    public static void main(String[] args) throws Exception {
        new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
    }
}
