package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.mintdaniel42.starediscordbot.db.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class ListButtons extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Override
    public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
        String[] buttonParts = event.getComponentId().split(":");
        if (buttonParts.length != 3) return;

        MessageEmbed messageEmbed;
        final int page = Integer.parseInt(buttonParts[2]) + (buttonParts[0].equals("previous") ? -1 : 1);

        if (buttonParts[1].equals("pg")) {
            messageEmbed = ListEmbed.createPgList(databaseAdapter, page);
            event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(messageEmbed).setComponents(
                    ActionRow.of(
                            Button.primary(String.format("previous:pg:%s", page), R.string("previous_page")).withDisabled(page < 1),
                            Button.primary(String.format("next:pg:%s", page), R.string("next_page")).withDisabled(page + 1 >= databaseAdapter.getPgPages())
                    )
            ).queue());
        }
        else if (buttonParts[1].equals("hns")) {
            messageEmbed = ListEmbed.createHnsList(databaseAdapter, page);
            event.deferReply().queue(interactionHook -> interactionHook.editOriginalEmbeds(messageEmbed).setComponents(
                    ActionRow.of(
                            Button.primary(String.format("previous:hns:%s", page), R.string("previous_page")).withDisabled(page < 1),
                            Button.primary(String.format("next:hns:%s", page), R.string("next_page")).withDisabled(page + 1 >= databaseAdapter.getHnsPages())
                    )
            ).queue());
        }
    }
}
