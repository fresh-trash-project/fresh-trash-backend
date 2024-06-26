package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.dto.response.ChatRoomResponse;
import freshtrash.freshtrashbackend.entity.ChatRoom;
import freshtrash.freshtrashbackend.entity.constants.SellStatus;
import freshtrash.freshtrashbackend.repository.ChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {
    @InjectMocks
    private ChatRoomService chatRoomService;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("Product id에 해당하는 채팅방 중 Close 되지 않은 채팅방 조회")
    void given_productIdAndSellStatus_when_then_getChatRoomList() {
        // given
        Long productId = 1L;
        SellStatus sellStatus = SellStatus.CLOSE;
        int expectedSize = 1;
        given(chatRoomRepository.findByProduct_IdAndSellStatusNot(eq(productId), eq(sellStatus)))
                .willReturn(List.of(Fixture.createChatRoom()));
        // when
        List<ChatRoom> chatRooms = chatRoomService.getNotClosedChatRoomsByProductId(productId);
        // then
        Assertions.assertThat(chatRooms.size()).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("채팅방 목록 조회")
    void given_memberIdAndPageable_when_getChatRooms_then_returnPagingChatRoomsResponse() {
        // given
        Long memberId = 1L;
        int expectedSize = 1;
        Pageable pageable = PageRequest.of(0, 10);
        given(chatRoomRepository.findAllBySeller_IdOrBuyer_Id(eq(memberId), eq(pageable)))
                .willReturn(new PageImpl<>(List.of(Fixture.createChatRoom())));
        // when
        Page<ChatRoomResponse> chatRooms = chatRoomService.getChatRoomsWithMemberId(memberId, pageable);
        // then
        assertThat(chatRooms.getSize()).isEqualTo(expectedSize);
    }

    @Test
    @DisplayName("채팅방 단일 조회")
    void given_chatRoomId_when_getChatRoom_then_returnChatRoom() {
        // given
        Long chatRoomId = 1L, memberId = 123L;
        given(chatRoomRepository.existsByIdAndMemberId(eq(chatRoomId), eq(memberId)))
                .willReturn(true);
        given(chatRoomRepository.findById(eq(chatRoomId))).willReturn(Optional.of(Fixture.createChatRoom()));
        // when
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId, memberId);
        // then
        assertThat(chatRoom).isNotNull();
    }

    @Test
    @DisplayName("존재하는 채팅방이 없다면 새로 생성한다.")
    void given_sellerIdAndBuyerIdAndProductId_when_notExistsChatRoom_then_createChatRoom() {
        // given
        Long sellerId = 1L, buyerId = 2L, productId = 3L;
        ChatRoom chatRoom = Fixture.createChatRoom(productId, sellerId, buyerId, true, SellStatus.ONGOING);
        given(chatRoomRepository.findBySellerIdAndBuyerIdAndProductId(sellerId, buyerId, productId))
                .willReturn(Optional.empty());
        given(chatRoomRepository.save(chatRoom)).willReturn(chatRoom);
        // when
        ChatRoom savedChatRoom = chatRoomService.getOrCreateChatRoom(sellerId, buyerId, productId);
        // then
        assertThat(savedChatRoom).isNotNull();
    }

    @Test
    @DisplayName("존재하는 채팅방이 이미 있다면 바로 조회해서 반환한다.")
    void given_sellerIdAndBuyerIdAndProductId_when_existsChatRoom_then_returnChatRoom() {
        // given
        Long sellerId = 1L, buyerId = 2L, productId = 3L;
        ChatRoom chatRoom = Fixture.createChatRoom(productId, sellerId, buyerId, true, SellStatus.ONGOING);
        given(chatRoomRepository.findBySellerIdAndBuyerIdAndProductId(sellerId, buyerId, productId))
                .willReturn(Optional.of(chatRoom));
        // when
        ChatRoom savedChatRoom = chatRoomService.getOrCreateChatRoom(sellerId, buyerId, productId);
        // then
        assertThat(savedChatRoom).isNotNull();
    }

    @Test
    @DisplayName("chatRoomId를 받아 채팅방의 OpenOrClose 값을 false로 업데이트한다.")
    void given_chatRoomId_when_then_closeChatRoom() {
        // given
        Long chatRoomId = 1L;
        willDoNothing().given(chatRoomRepository).updateOpenOrClose(chatRoomId);
        // when
        assertThatCode(() -> chatRoomService.closeChatRoom(chatRoomId)).doesNotThrowAnyException();
        // then
    }
}
