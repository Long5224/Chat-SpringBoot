package com.chat.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.chat.models.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
