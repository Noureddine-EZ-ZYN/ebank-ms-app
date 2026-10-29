
import jakarta.annotation.PostConstruct;

import net.noureddine.ebankbot.agents.EbankAIAgent;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.objects.ChatAction;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import org.springframework.ai.chat.prompt.Prompt;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.token}")
    private String telegramBotToken;

    private final EbankAIAgent aiAgent;

    public TelegramBot(EbankAIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    // Enregistrer le bot Telegram au démarrage
    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api =
                    new TelegramBotsApi(DefaultBotSession.class);

            api.registerBot(this);

        } catch (TelegramApiException e) {
            throw new RuntimeException(
                    "Erreur lors de l'enregistrement du bot Telegram", e
            );
        }
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    // Recevoir les messages Telegram
    @Override
    public void onUpdateReceived(Update telegramRequest) {
        try {
            // Vérifier que la mise à jour contient un message
            if (!telegramRequest.hasMessage()) {
                return;
            }

            // Accepter uniquement les messages texte
            if (!telegramRequest.getMessage().hasText()) {
                return;
            }

            String messageText =
                    telegramRequest.getMessage().getText();

            Long chatId =
                    telegramRequest.getMessage().getChatId();

            // Construire le message utilisateur
            UserMessage userMessage = UserMessage.builder()
                    .text(messageText)
                    .build();

            // Afficher l'indicateur de saisie
            sendTypingQuestion(chatId);

            // Obtenir la réponse de l'agent IA
            String answer = aiAgent.chat(
                    new Prompt(userMessage)
            );

            // Envoyer la réponse à l'utilisateur
            sendTextMessage(chatId, answer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Afficher "en train d'écrire..."
    private void sendTypingQuestion(Long chatId) {
        SendChatAction action = new SendChatAction();
        action.setChatId(chatId.toString());
        action.setAction(ChatAction.TYPING);

        try {
            execute(action);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Envoyer un message texte
    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
