package org.mintdaniel42.starediscordbot.buttons;

import fr.leonarddoo.dba.annotation.Button;
import fr.leonarddoo.dba.element.DBAButton;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

@Getter
public final class ListButtons {
    @Button(id = "CANCEL")
    public static final class CancelButton implements DBAButton {
        @Override
        public void execute(ButtonInteractionEvent event) {
        }
    }

    @Button(id = "PREVIOUS")
    public static final class PreviousPageButton implements DBAButton {
        @Override
        public void execute(ButtonInteractionEvent event) {
        }
    }

    @Button(id = "NEXT")
    public static final class NextPageButton implements DBAButton {
        @Override
        public void execute(ButtonInteractionEvent event) {
        }
    }
}
