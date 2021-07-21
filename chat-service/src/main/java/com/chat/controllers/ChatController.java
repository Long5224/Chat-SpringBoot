package com.chat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chat.models.ChatMessage;
import com.chat.models.ChatNotification;
import com.chat.services.ChatMessageService;
import com.chat.services.ChatRoomService;

@Controller
public class ChatController {
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private ChatMessageService chatMessageService;
	@Autowired
	private ChatRoomService chatRoomService;

	@MessageMapping("/chat")
	public void processMessage(@Payload ChatMessage chatMessage) {
		var chatId = chatRoomService.getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
		chatMessage.setChatId(chatId.get());

		ChatMessage saved = chatMessageService.save(chatMessage);
		messagingTemplate.convertAndSendToUser(chatMessage.getRecipientId(), "/queue/messages",
				new ChatNotification(saved.getId(), saved.getSenderId(), saved.getSenderName()));
	}

	@GetMapping("/messages/{senderId}/{recipientId}/count")
	public ResponseEntity<Long> countNewMessages(@PathVariable String senderId, @PathVariable String recipientId) {
		Long count = chatMessageService.countNewMessage(senderId, recipientId);
		return ResponseEntity.ok(count);
	}

	@GetMapping("/messages/{senderId}/{recipientId}")
	public ResponseEntity<?> findChatMessages(@PathVariable String senderId, @PathVariable String recipientId) {
		return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
	}

	@GetMapping("/messages/{id}")
	public ResponseEntity<?> findMessage(@PathVariable String id) {
		return ResponseEntity.ok(chatMessageService.findById(id));
	}
}
