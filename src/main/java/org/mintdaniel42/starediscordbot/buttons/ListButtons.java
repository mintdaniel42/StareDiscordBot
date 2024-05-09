package org.mintdaniel42.starediscordbot.buttons;

import fr.leonarddoo.dba.annotation.Button;
import fr.leonarddoo.dba.element.DBAButton;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Getter
public final class ListButtons {
    private final DBAButton previousPageButton, nextPageButton;

    public ListButtons() {
        previousPageButton = new PreviousPageButton();
        nextPageButton = new NextPageButton();
    }

    @Button(id = "previous_page_button")
    public final class PreviousPageButton implements DBAButton {
        @Override
        public void execute(ButtonInteractionEvent event) {
        }
    }

    @Button(id = "next_page_button")
    public final class NextPageButton implements DBAButton {
        @Override
        public void execute(ButtonInteractionEvent event) {
        }
    }
}
