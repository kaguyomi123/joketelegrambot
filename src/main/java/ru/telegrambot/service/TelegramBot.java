package ru.telegrambot.service;


import com.simtechdata.jokes.Jokes;
import com.simtechdata.jokes.enums.Category;
import ru.telegrambot.components.BotCommands;
import ru.telegrambot.config.BotConfig;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;

        try {
            this.execute(new SetMyCommands(BotCommands.LIST_OF_COMMANDS, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() { return config.getBotName(); }

    @Override
    public String getBotToken() { return config.getBotToken(); }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        long chatId;
        String userName;
        String receivedMessage;

        if(update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            userName = update.getMessage().getFrom().getFirstName();

            if (update.getMessage().hasText()) {
                receivedMessage = update.getMessage().getText();
                botAnswerUtils(receivedMessage, chatId, userName);
            }
        }
    }

    private void botAnswerUtils(String receivedMessage, long chatId, String userName) {
        switch (receivedMessage){
            case "/start":
                startBot(chatId, userName);
                break;
            case "/help":
                sendHelpText(chatId);
                break;
            case "/joke":
                sendJoke(chatId);
                break;
            default:
                sendMessage(chatId, "use /help command for help");

        }
    }

    private void startBot(long chatId, String userName) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Hello, " + userName + "! I'm a telegram bot.");

        try {
            execute(message);
            log.info("Start reply sent to: {}", userName);
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void sendMessage(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("error: {}", e.getMessage());
        }
    }

    private void sendHelpText(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(BotCommands.HELP_TEXT);

        try {
            execute(message);
            log.info("Help reply sent to: {}", chatId);
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

    private void sendJoke(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        Jokes joke = new Jokes.Builder().addCategory(Category.ANY_CATEGORY).build();

        message.setText(joke.getAny());

        try {
            execute(message);
            log.info("Joke sent to: {}", chatId);
        } catch (TelegramApiException e){
            log.error(e.getMessage());
        }
    }

}
