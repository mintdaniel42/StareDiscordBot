package org.mintdaniel42.starediscordbot.buttons;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.mintdaniel42.starediscordbot.data.DatabaseAdapter;
import org.mintdaniel42.starediscordbot.data.GroupModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

@RequiredArgsConstructor
public final class ListButtons extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Contract(pure = true, value = "_, _, _ -> new")
    public static @NonNull ActionRow create(@NonNull final GroupModel groupModel, final int page, final long maxPages) {
        return ActionRow.of(
                Button.primary(
                        "previous:group:%s:%s".formatted(groupModel.getTag(), page),
                        R.Strings.ui("previous_page")
                ).withDisabled(page <= 0),
                Button.primary(
                        "next:group:%s:%s".formatted(groupModel.getTag(), page),
                        R.Strings.ui("next_page")
                ).withDisabled(page >= maxPages - 1)
        );
    }

    @Contract(pure = true, value = "_, _, _ -> new")
    public static @NonNull ActionRow create(@NonNull final Type type, final int page, final long maxPages) {
        return ActionRow.of(
                Button.primary(
                        "previous:%s:%s".formatted(type.name(), page),
						R.Strings.ui("previous_page")
                ).withDisabled(page <= 0),
                Button.primary(
                        "next:%s:%s".formatted(type.name(), page),
						R.Strings.ui("next_page")
                ).withDisabled(page >= maxPages - 1)
        );
    }

    @Override
    public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
        String[] buttonParts = event.getComponentId().split(":");
        if (buttonParts.length == 3 || (buttonParts.length == 4 && buttonParts[1].equals("group"))) {
            final int page = Integer.parseInt(buttonParts[buttonParts.length - 1]) + (buttonParts[0].equals("previous") ? -1 : 1);
            final var embedType = Type.valueOf(buttonParts[1]);
            GroupModel groupModel = null;
            final var messageEmbed = switch (embedType) {
                case pg -> ListEmbed.createPgList(databaseAdapter, page);
                case hns -> ListEmbed.createHnsList(databaseAdapter, page);
                case group -> {
                    if ((groupModel = databaseAdapter.getGroup(buttonParts[2])) != null) {
                        yield GroupEmbed.of(databaseAdapter, groupModel, page);
                    }
                    yield null;
                }
                default -> null;
            };
            final var actionRow = switch (embedType) {
                case pg -> create(embedType, page, databaseAdapter.getPgPages());
                case hns -> create(embedType, page, databaseAdapter.getHnsPages());
                case group -> {
                    if (groupModel != null) {
                        yield create(groupModel, page, databaseAdapter.getGroupMemberPages(groupModel.getTag()));
                    }
                    yield null;
                }
                default -> null;
            };

            if (messageEmbed != null) {
                event.deferEdit().queue(interactionHook -> {
                    if (buttonParts[0].equals("info") && page == 0) interactionHook.sendMessageEmbeds(messageEmbed)
                            .setComponents(actionRow)
                            .queue();
                    else interactionHook.editOriginalEmbeds(messageEmbed)
                            .setComponents(actionRow)
                            .queue();
                });
            } else event.deferEdit().queue();
        }
    }

    public enum Type {
        hns,
        pg,
        group,
        help
    }
}
