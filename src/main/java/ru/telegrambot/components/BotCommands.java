package ru.telegrambot.components;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface BotCommands {
    List<BotCommand> LIST_OF_COMMANDS = List.of(
            new BotCommand("/start", "start bot"),
            new BotCommand("/help", "bot info"),
            new BotCommand("/joke", "jokes =)")
    );

    String HELP_TEXT = "этот пацанчик выдает анекдот, используйте /joke";
}
