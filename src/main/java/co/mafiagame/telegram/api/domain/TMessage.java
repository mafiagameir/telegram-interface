/*
 * Copyright (C) 2015 mafiagame.ir
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package co.mafiagame.telegram.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author hekmatof
 */
public class TMessage {
    @JsonProperty("message_id")
    private Long id;
    private TUser from;
    private Integer date;
    private TChat chat;
    @JsonProperty("forward_from")
    private TUser forwardFrom;
    @JsonProperty("forward_from_chat")
    private TChat forwardFromChat;
    @JsonProperty("forward_date")
    private Integer forwardDate;
    @JsonProperty("reply_to_message")
    private TMessage replyToMessage;
    @JsonProperty("edit_date")
    private Integer editDate;
    private String caption;
    @JsonProperty("new_chat_member")
    private TUser newChatMember;
    @JsonProperty("left_chat_member")
    private TUser leftChatMember;
    @JsonProperty("new_chat_title")
    private String newChatTitle;
    @JsonProperty("delete_chat_photo")
    private Boolean deleteChatPhoto;
    @JsonProperty("group_chat_created")
    private Boolean groupChatCreated;
    @JsonProperty("supergroup_chat_created")
    private Boolean supergroupChatCreated;
    @JsonProperty("channel_chat_created")
    private Boolean channelChatCreated;
    private String text;
    private List<TMessageEntity> entities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TUser getFrom() {
        return from;
    }

    public void setFrom(TUser from) {
        this.from = from;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public TChat getChat() {
        return chat;
    }

    public void setChat(TChat chat) {
        this.chat = chat;
    }

    public TUser getForwardFrom() {
        return forwardFrom;
    }

    public void setForwardFrom(TUser forwardFrom) {
        this.forwardFrom = forwardFrom;
    }

    public TChat getForwardFromChat() {
        return forwardFromChat;
    }

    public void setForwardFromChat(TChat forwardFromChat) {
        this.forwardFromChat = forwardFromChat;
    }

    public Integer getForwardDate() {
        return forwardDate;
    }

    public void setForwardDate(Integer forwardDate) {
        this.forwardDate = forwardDate;
    }

    public TMessage getReplyToMessage() {
        return replyToMessage;
    }

    public void setReplyToMessage(TMessage replyToMessage) {
        this.replyToMessage = replyToMessage;
    }

    public Integer getEditDate() {
        return editDate;
    }

    public void setEditDate(Integer editDate) {
        this.editDate = editDate;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public TUser getNewChatMember() {
        return newChatMember;
    }

    public void setNewChatMember(TUser newChatMember) {
        this.newChatMember = newChatMember;
    }

    public TUser getLeftChatMember() {
        return leftChatMember;
    }

    public void setLeftChatMember(TUser leftChatMember) {
        this.leftChatMember = leftChatMember;
    }

    public String getNewChatTitle() {
        return newChatTitle;
    }

    public void setNewChatTitle(String newChatTitle) {
        this.newChatTitle = newChatTitle;
    }

    public Boolean getDeleteChatPhoto() {
        return deleteChatPhoto;
    }

    public void setDeleteChatPhoto(Boolean deleteChatPhoto) {
        this.deleteChatPhoto = deleteChatPhoto;
    }

    public Boolean getGroupChatCreated() {
        return groupChatCreated;
    }

    public void setGroupChatCreated(Boolean groupChatCreated) {
        this.groupChatCreated = groupChatCreated;
    }

    public Boolean getSupergroupChatCreated() {
        return supergroupChatCreated;
    }

    public void setSupergroupChatCreated(Boolean supergroupChatCreated) {
        this.supergroupChatCreated = supergroupChatCreated;
    }

    public Boolean getChannelChatCreated() {
        return channelChatCreated;
    }

    public void setChannelChatCreated(Boolean channelChatCreated) {
        this.channelChatCreated = channelChatCreated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TMessageEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<TMessageEntity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "TMessage{" +
                "id=" + id +
                ", from=" + from +
                ", date=" + date +
                ", chat=" + chat +
                ", forwardFrom=" + forwardFrom +
                ", forwardFromChat=" + forwardFromChat +
                ", forwardDate=" + forwardDate +
                ", replyToMessage=" + replyToMessage +
                ", editDate=" + editDate +
                ", caption='" + caption + '\'' +
                ", newChatMember=" + newChatMember +
                ", leftChatMember=" + leftChatMember +
                ", newChatTitle='" + newChatTitle + '\'' +
                ", deleteChatPhoto=" + deleteChatPhoto +
                ", groupChatCreated=" + groupChatCreated +
                ", supergroupChatCreated=" + supergroupChatCreated +
                ", channelChatCreated=" + channelChatCreated +
                ", text='" + text + '\'' +
                ", entities=" + entities +
                '}';
    }
}
