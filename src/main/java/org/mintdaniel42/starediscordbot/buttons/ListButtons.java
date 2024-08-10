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
import org.mintdaniel42.starediscordbot.data.TutorialModel;
import org.mintdaniel42.starediscordbot.embeds.GroupEmbed;
import org.mintdaniel42.starediscordbot.embeds.ListEmbed;
import org.mintdaniel42.starediscordbot.utils.R;

import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
public final class ListButtons extends ListenerAdapter {
    @NonNull private final DatabaseAdapter databaseAdapter;

    @Contract(pure = true, value = "-> new")
    public static @NonNull ActionRow createInfo() {
        return ActionRow.of(
                Button.link(
                        "https://github.com/mintdaniel42/StareDiscordBot",
                        R.Strings.ui("show_source_code")
                ),
                Button.primary(
                        "list:%s:%s".formatted("hns", 0),
                        R.Strings.ui("list_hide_n_seek_entries")
                ),
                Button.primary(
                        "list:%s:%s".formatted("pg", 0),
                        R.Strings.ui("list_partygames_entries")
                )
        );
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NonNull ActionRow createTutorial(@NonNull final TutorialModel tutorialModel) {
        final var tutorialList = Arrays.asList(R.Tutorials.list());
        final var index = tutorialList.stream()
                .sorted()
                .toList()
                .indexOf(tutorialModel);
        return ActionRow.of(
                Button.primary(
                        "%s:%s:%s".formatted("tutorial", tutorialList.get(index > 0 ? index - 1 : 0).getId(), UUID.randomUUID()),
                                R.Strings.ui("back_s", tutorialList.get(index > 0 ? index - 1 : 0).getTitle())
                        ).withEmoji(R.Emojis.arrowLeft)
                        .withDisabled(index < 1),
                Button.primary(
                                "%s:%s:%s".formatted("tutorial", tutorialList.get(index < tutorialList.size() - 1 ? index + 1 : index).getId(), UUID.randomUUID()),
                                R.Strings.ui("continue_s", tutorialList.get(index < tutorialList.size() - 1 ? index + 1 : index).getTitle())
                        ).withEmoji(R.Emojis.arrowRight)
                        .withDisabled(index >= tutorialList.size() - 1)
        );
    }

    @Contract(pure = true, value = "_, _, _ -> new")
    public static @NonNull ActionRow create(@NonNull final GroupModel groupModel, final int page, final long maxPages) {
        return ActionRow.of(
                Button.primary(
                        "group:%s:%s".formatted(groupModel.getTag(), page - 1),
                        R.Strings.ui("previous_page")
                        ).withEmoji(R.Emojis.arrowLeft)
                        .withDisabled(page <= 0),
                Button.primary(
                        "group:%s:%s".formatted(groupModel.getTag(), page + 1),
                        R.Strings.ui("next_page")
                        ).withEmoji(R.Emojis.arrowRight)
                        .withDisabled(page >= maxPages - 1)
        );
    }

    @Contract(pure = true, value = "_, _, _ -> new")
    public static @NonNull ActionRow create(@NonNull final Type type, final int page, final long maxPages) {
        return ActionRow.of(
                Button.primary(
                        "list:%s:%s".formatted(type.name(), page - 1),
						R.Strings.ui("previous_page")
                        ).withEmoji(R.Emojis.arrowLeft)
                        .withDisabled(page <= 0),
                Button.primary(
                        "list:%s:%s".formatted(type.name(), page + 1),
						R.Strings.ui("next_page")
                        ).withEmoji(R.Emojis.arrowRight)
                        .withDisabled(page >= maxPages - 1)
        );
    }

    @Override
    public void onButtonInteraction(@NonNull final ButtonInteractionEvent event) {
        String[] buttonParts = event.getComponentId().split(":");
        if (buttonParts.length == 3 && (buttonParts[0].equals("group") || buttonParts[0].equals("list"))) {
            final var page = Integer.parseInt(buttonParts[buttonParts.length - 1]);
            final var embedType = Type.valueOf(buttonParts[1]);
            GroupModel groupModel = null;
            final var messageEmbed = switch (embedType) {
                case pg -> ListEmbed.createPgList(databaseAdapter, page);
                case hns -> ListEmbed.createHnsList(databaseAdapter, page);
                case group -> {
                    if ((groupModel = databaseAdapter.getGroup(buttonParts[1])) != null) {
                        yield GroupEmbed.of(databaseAdapter, groupModel, page);
                    } else yield null;
                }
                default -> null;
            };
            final var actionRow = switch (embedType) {
                case pg -> create(embedType, page, databaseAdapter.getPgPages());
                case hns -> create(embedType, page, databaseAdapter.getHnsPages());
                case group -> {
                    if (groupModel != null) {
                        yield create(groupModel, page, databaseAdapter.getGroupMemberPages(groupModel.getTag()));
                    } else yield null;
                }
                default -> null;
            };

            if (messageEmbed != null) {
                event.deferEdit().queue(interactionHook -> interactionHook.editOriginalEmbeds(messageEmbed)
                        .setComponents(actionRow)
                        .queue());
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
