package de.teamholy.core.bungee.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;



/* copyright by Yassino */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatAction {

    TextComponent component;

    public ChatAction() {
        this.component = new TextComponent();
    }

    public ChatAction text(String text) {
        this.component.setText(text);
        return this;
    }

    public ChatAction hover(String hover) {
        this.component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        return this;
    }

    public ChatAction execute(String command) {
        this.component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        return this;
    }

    public ChatAction suggest(String suggest) {
        this.component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggest));
        return this;
    }

    public TextComponent component() {
        return this.component;
    }

}
