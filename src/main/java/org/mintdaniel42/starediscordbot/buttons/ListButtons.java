package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
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

        final var page = Integer.parseInt(buttonParts[2]) + (buttonParts[0].equals("previous") ? -1 : 1);
        final var embedType = Type.valueOf(buttonParts[1]);
        final var messageEmbed = switch (embedType) {
            case pg -> ListEmbed.createPgList(databaseAdapter, page);
            case hns -> ListEmbed.createHnsList(databaseAdapter, page);
            default -> null;
        };
        final var actionRow = switch (embedType) {
            case pg -> create(embedType, page, databaseAdapter.getPgPages());
            case hns -> create(embedType, page, databaseAdapter.getHnsPages());
            default -> null;
        };

        if (messageEmbed != null) {
            event.deferEdit().queue(interactionHook -> interactionHook.editOriginalEmbeds(messageEmbed)
                    .setComponents(actionRow)
                    .queue());
        } else event.deferEdit().queue();
    }

    @Contract(pure = true, value = "_, _, _ -> new")
    public static @NonNull ActionRow create(@NonNull final Type type, final int page, final long maxPages) {
        return ActionRow.of(
                Button.primary(
                        "previous:%s:%s".formatted(type.name(), page),
                        R.string("previous_page")
                ).withDisabled(page <= 0),
                Button.primary(
                        "next:%s:%s".formatted(type.name(), page),
                        R.string("next_page")
                ).withDisabled(page >= maxPages)
        );
    }

    public enum Type {
        hns,
        pg,
        group,
        help
    }
}
