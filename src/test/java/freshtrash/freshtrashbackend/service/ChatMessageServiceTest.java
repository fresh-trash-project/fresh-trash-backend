package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.entity.ChatMessage;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.repository.ChatMessageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {
    @InjectMocks
    private ChatMessageService chatMessageService;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private MemberService memberService;

    @Test
    @DisplayName("chatRoomId, memberId, message를 입력받아 ChatMessage를 생성하여 저장하고 반환한다.")
    void given_chatRoomIdAndMemberIdAndMessage_when_constructChatMessage_then_saveChatMessageAndReturn() {
        // given
        Long chatRoomId = 2L, memberId = 1L;
        String message = "message";
        Member member = Fixture.createMember();
        ChatMessage chatMessage = ChatMessage.of(chatRoomId, member, message);
        given(memberService.getMemberById(memberId)).willReturn(member);
        given(chatMessageRepository.save(chatMessage)).willReturn(chatMessage);
        // when
        ChatMessage savedChatMessage = chatMessageService.saveChatMessage(chatRoomId, memberId, message);
        // then
        assertThat(savedChatMessage).isNotNull();
    }
}