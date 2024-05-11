package org.mintdaniel42.starediscordbot;

import com.github.ygimenez.exception.InvalidHandlerException;
import com.github.ygimenez.method.Pages;
import com.github.ygimenez.model.PaginatorBuilder;
import fr.leonarddoo.dba.loader.DBALoader;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
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
        JDA jda = JDABuilder.createLight(Options.getToken())
                .addEventListeners(this)
                .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .build();

	    try {
		    Pages.activate(PaginatorBuilder.createPaginator()
				    .setHandler(jda)
                    .shouldEventLock(true)
		            .build());
    } catch (InvalidHandlerException e) {
		    throw new RuntimeException(e);
	    }
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
                new ListPGUsersCommand(databaseAdapter),    // TODO: buttons

                new ApproveChangeCommand(databaseAdapter)
        );

        DBALoader.getInstance(event.getJDA()).initDBAEvent(
		        new ListButtons.CancelButton(),    // TODO
                new ListButtons.PreviousPageButton(),
		        new ListButtons.NextPageButton()// TODO
        );
    }

    public static void main(String[] args) throws Exception {
        new Bot(new DatabaseAdapter(Options.getJdbcUrl()));
    }
}
