package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.repository.MemberCacheRepository;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileService fileService;

    @Mock
    private MemberCacheRepository memberCacheRepository;

    @Test
    @DisplayName("member 정보 조회")
    void given_memberId_when_getMember_then_memberIsNotNull() {
        // given
        Long memberId = 123L;
        given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(Fixture.createLoginMember()));
        // when
        Member member = memberService.getMember(memberId);
        // then
        assertThat(member).isNotNull();
    }

    @Test
    @DisplayName("member 정보 수정")
    void given_memberRequestAndImgFile_when_updateMember_then_memberRequestEqualsToUpdateMember() {
        // given
        Long memberId = 1L;
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image");
        MemberRequest memberRequest = FixtureDto.createMemberRequest();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(Fixture.createMember()));
        given(memberCacheRepository.save(any(MemberPrincipal.class))).willReturn(memberPrincipal);
        // when
        Member member = memberService.updateMember(memberPrincipal, memberRequest, imgFile);
        // then
        assertThat(member.getNickname()).isEqualTo(memberRequest.nickname());
        assertThat(member.getAddress()).isEqualTo(memberRequest.address());
    }
}