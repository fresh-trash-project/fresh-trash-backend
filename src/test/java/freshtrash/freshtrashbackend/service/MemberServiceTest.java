package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.request.ChangePasswordRequest;
import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.exception.MemberException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.MemberCacheRepository;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

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

    @Mock
    private PasswordEncoder encoder;

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

    @Test
    @DisplayName("비밀번호 변경")
    void given_changePasswordRequestAndPrincipal_when_matchedOldPassword_then_changePassword() {
        // given
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        ChangePasswordRequest changePasswordRequest =
                FixtureDto.createChangePasswordRequest("qwer1234!!", "asdf1234!!");
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedNewPassword = bCryptPasswordEncoder.encode(changePasswordRequest.newPassword());
        given(encoder.matches(changePasswordRequest.oldPassword(), memberPrincipal.password()))
                .willReturn(true);
        given(encoder.encode(changePasswordRequest.newPassword())).willReturn(encodedNewPassword);
        willDoNothing().given(memberRepository).updatePasswordByEmail(memberPrincipal.email(), encodedNewPassword);
        // when
        assertThatCode(() -> memberService.changePassword(changePasswordRequest, memberPrincipal))
                .doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("비밀번호 변경 - 비밀번호 불일치")
    void given_changePasswordRequestAndPrincipal_when_unmatchedOldPassword_then_throwException() {
        // given
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        ChangePasswordRequest changePasswordRequest = FixtureDto.createChangePasswordRequest("qwer1234!", "asdf1234!!");
        given(encoder.matches(changePasswordRequest.oldPassword(), memberPrincipal.password()))
                .willReturn(false);
        // when
        assertThatThrownBy(() -> memberService.changePassword(changePasswordRequest, memberPrincipal))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNMATCHED_PASSWORD);
        // then
    }
}