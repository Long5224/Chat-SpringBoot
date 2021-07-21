package com.chat.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chat.models.ChatMessage;
import com.chat.models.MessageStatus;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

	long countBySenderIdAndRecipientIdAndStatus(
			String senderId, String recipientId, MessageStatus status);
	
	List<ChatMessage> findByChatId(String chatId);
}
