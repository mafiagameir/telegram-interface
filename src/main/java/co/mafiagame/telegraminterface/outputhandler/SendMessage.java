package co.mafiagame.telegraminterface.outputhandler;

import co.mafiagame.telegram.api.domain.TReplyKeyboardMarkup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

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

    @JsonIgnore
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("chat_id", chatId);
        result.put("text", text);
        result.put("reply_markup", replyMarkup);
        return result;
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
