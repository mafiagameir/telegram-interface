package co.mafiagame.telegraminterface.outputhandler;

import co.mafiagame.telegram.api.domain.TReplyKeyboardMarkup;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Esa Hekmatizadeh
 */
public class SendMessage {
    @JsonProperty("chat_id")
    private Long chatId;
    private String text;
    @JsonProperty("reply_markup")
    private TReplyKeyboardMarkup replyMarkup;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TReplyKeyboardMarkup getReplyMarkup() {
        return replyMarkup;
    }

    public void setReplyMarkup(TReplyKeyboardMarkup replyMarkup) {
        this.replyMarkup = replyMarkup;
    }

    @Override
    public String toString() {
        return "SendMessage{" +
                "chatId=" + chatId +
                ", text='" + text + '\'' +
                ", replyMarkup=" + replyMarkup +
                '}';
    }
}
