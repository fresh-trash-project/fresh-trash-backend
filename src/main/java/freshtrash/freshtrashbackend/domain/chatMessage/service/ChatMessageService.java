package freshtrash.freshtrashbackend.domain.chatMessage.service;

import freshtrash.freshtrashbackend.domain.chatMessage.entity.ChatMessage;
import freshtrash.freshtrashbackend.domain.chatMessage.repository.ChatMessageRepository;
import freshtrash.freshtrashbackend.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final MemberService memberService;

    public ChatMessage saveChatMessage(Long chatRoomId, Long memberId, String message) {
        log.debug("save chat message");
        return chatMessageRepository.save(ChatMessage.of(chatRoomId, memberService.getMemberById(memberId), message));
    }
}
