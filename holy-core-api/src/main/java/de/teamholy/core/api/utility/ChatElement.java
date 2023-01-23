package de.teamholy.core.api.utility;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

public class ChatElement {

    public static ChatElement empty() {
        return new ChatElement(emptyComp());
    }

    public static ChatElement of(String text) {
        return new ChatElement(compOf("§7" + text));
    }

    public static ChatElement ofError(String text) {
        return new ChatElement(compOf("§c" + text));
    }

    public static ChatElement ofSuccess(String text) {
        return new ChatElement(compOf("§a" + text));
    }

    public static ChatElement ofHover(String text, String hover) {
        return new ChatElement(createHover(text, hover));
    }

    public static ChatElement ofSuggest(String text, String suggestion) {
        return new ChatElement(createSuggest(text, suggestion));
    }

    public static ChatElement ofCommand(String text, String command) {
        return new ChatElement(createCommand(text, command));
    }

    public static ChatElement ofHoverCommand(String text, String hover, String command) {
        return new ChatElement(createHoverCommand(text, hover, command));
    }

    public static ChatElement ofHoverSuggest(String text, String hover, String suggestion) {
        return new ChatElement(createHoverSuggest(text, hover, suggestion));
    }

    public static ChatElement ofHoverUrl(String text, String hover, String url) {
        return new ChatElement(createHoverUrl(text, hover, url));
    }

    /* static methods for internal usage */

    private static TextComponent compOf(String text) {
        TextComponent component = emptyComp();
        BaseComponent[] baseComponents = TextComponent.fromLegacyText(text);
        if (baseComponents != null) {
            for (BaseComponent baseComponent : baseComponents) {
                component.addExtra(baseComponent);
            }
        }
        return component;
    }

    private static TextComponent emptyComp() {
        TextComponent component = new TextComponent("");
        component.setBold(false);
        component.setItalic(false);
        component.setObfuscated(false);
        component.setStrikethrough(false);
        component.setColor(ChatColor.GRAY);
        component.setUnderlined(false);
        return component;
    }

    private static TextComponent createHoverUrl(String text, String hover, String url) {
        return createClickHoverComponent(text, new ClickEvent(ClickEvent.Action.OPEN_URL, url),
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
    }

    private static TextComponent createHoverSuggest(String text, String hover, String suggestion) {
        return createClickHoverComponent(text, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion),
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
    }

    private static TextComponent createHoverCommand(String text, String hover, String command) {
        command = command.startsWith("/") ? command : "/" + command;
        return createClickHoverComponent(text, new ClickEvent(ClickEvent.Action.RUN_COMMAND, command),
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
    }

    private static TextComponent createCommand(String text, String command) {
        command = command.startsWith("/") ? command : "/" + command;
        return createClickComponent(text, new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    private static TextComponent createHover(String text, String hover) {
        return createHoverComponent(text, new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
    }

    private static TextComponent createSuggest(String text, String suggestion) {
        return createClickComponent(text, new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, suggestion));
    }

    private static TextComponent createHoverComponent(String text, HoverEvent event) {
        TextComponent hoverComp = ChatElement.compOf(text);
        hoverComp.setHoverEvent(event);
        return hoverComp;
    }

    private static TextComponent createClickComponent(String text, ClickEvent event) {
        TextComponent hoverComp = ChatElement.compOf(text);
        hoverComp.setClickEvent(event);
        return hoverComp;
    }

    private static TextComponent createClickHoverComponent(String text, ClickEvent clickEvent, HoverEvent hoverEvent) {
        TextComponent hoverComp = ChatElement.compOf(text);
        hoverComp.setClickEvent(clickEvent);
        hoverComp.setHoverEvent(hoverEvent);
        return hoverComp;
    }

    /* start of ChatElement object */

    private final TextComponent component;

    public ChatElement(TextComponent component) {
        this.component = component;
    }

    public TextComponent comp() {
        return component;
    }

    public ChatElement with(String text) {
        return append(ChatElement.of("§7" + text));
    }

    public ChatElement with(TextComponent textComponent) {
        component.addExtra(textComponent);
        return this;
    }

    public ChatElement with(ChatElement element) {
        component.addExtra(element.comp());
        return this;
    }

    public ChatElement error(String text) {
        return append(ChatElement.of("§c" + text));
    }

    public ChatElement success(String text) {
        return append(ChatElement.of("§a" + text));
    }

    public ChatElement hover(String text, String hover) {
        return append(createHover(text, hover));
    }

    public ChatElement suggest(String text, String suggestion) {
        return append(createSuggest(text, suggestion));
    }

    public ChatElement command(String text, String command) {
        return append(createCommand(text, command));
    }

    public ChatElement hoverCommand(String text, String hover, String command) {
        return append(createHoverCommand(text, hover, command));
    }

    public ChatElement hoverSuggest(String text, String hover, String suggestion) {
        return append(createHoverSuggest(text, hover, suggestion));
    }

    public ChatElement hoverUrl(String text, String hover, String url) {
        return append(createHoverUrl(text, hover, url));
    }

    @Deprecated
    public ChatElement append(TextComponent textComponent) {
        component.addExtra(textComponent);
        return this;
    }

    @Deprecated
    public ChatElement append(ChatElement element) {
        component.addExtra(element.comp());
        return this;
    }
}
