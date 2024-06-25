package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.Fixture.Fixture;
import freshtrash.freshtrashbackend.Fixture.FixtureDto;
import freshtrash.freshtrashbackend.dto.projections.FileNameSummary;
import freshtrash.freshtrashbackend.dto.projections.FlagCountSummary;
import freshtrash.freshtrashbackend.dto.request.ChangePasswordRequest;
import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.exception.MemberException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.MemberCacheRepository;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import freshtrash.freshtrashbackend.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ActiveProfiles("test")
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

    @Mock
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("memberId로 member 정보 조회")
    void given_memberId_when_getMember_then_returnMember() {
        // given
        Long memberId = 123L;
        given(memberRepository.findById(eq(memberId))).willReturn(Optional.of(Fixture.createLoginMember()));
        // when
        Member member = memberService.getMemberById(memberId);
        // then
        assertThat(member).isNotNull();
    }

    @Test
    @DisplayName("email로 member 정보 조회")
    void given_email_when_getMember_then_returnMember() {
        // given
        String email = "testUser@gmail.com";
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(Fixture.createLoginMember()));
        // when
        Member member = memberService.getMemberByEmail(email);
        // then
        assertThat(member).isNotNull();
    }

    @Test
    @DisplayName("memberId로 member 캐시 조회")
    void given_memberId_when_getMemberCache_then_returnMemberPrincipal() {
        // given
        Long memberId = 1L;
        given(memberCacheRepository.findById(memberId)).willReturn(Optional.of(FixtureDto.createMemberPrincipal()));
        // when
        MemberPrincipal memberPrincipal = memberService.getMemberCache(memberId);
        // then
        assertThat(memberPrincipal).isNotNull();
    }

    @Test
    @DisplayName("member 객체를 받아 회원가입 수행")
    void given_member_when_checkEmailAndNickname_then_saveMemberAndReturn() {
        // given
        Member member = Fixture.createMember();
        String encodedPassword = "encodedPassword";
        given(memberRepository.existsByEmail(member.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(member.getNickname())).willReturn(false);
        given(encoder.encode(member.getPassword())).willReturn(encodedPassword);
        given(memberRepository.save(member)).willReturn(member);
        // when
        Member savedMember = memberService.registerMember(member);
        // then
        assertThat(savedMember.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("email과 password를 입력받아 토큰을 발급하고 캐싱한 후 토큰을 반환한다.")
    void given_EmailAndPassword_when_generateAccessToken_then_saveCacheAndReturnToken() {
        // given
        String email = "testUser@gmail.com", password = "pw";
        Member member = Fixture.createMember();
        String accessToken = "accessToken";
        MemberPrincipal memberPrincipal = MemberPrincipal.fromEntity(member);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(encoder.matches(password, member.getPassword())).willReturn(true);
        given(tokenProvider.generateAccessToken(member.getId())).willReturn(accessToken);
        given(memberCacheRepository.save(memberPrincipal)).willReturn(memberPrincipal);
        // when
        LoginResponse loginResponse = memberService.signIn(email, password);
        // then
        assertThat(loginResponse.accessToken()).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("email을 입력받아 이미 존재하는지 확인하고 존재하지않으면 아무것도 반환하지 않는다.")
    void given_email_when_then_notAnyReturn() {
        // given
        String email = "testUser@gmail.com";
        given(memberRepository.existsByEmail(email)).willReturn(false);
        // when
        assertThatCode(() -> memberService.checkEmailDuplication(email)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("email을 입력받아 이미 존재하는지 확인하고 존재할 경우 예외가 발생한다.")
    void given_email_when_then_throwException() {
        // given
        String email = "testUser@gmail.com";
        given(memberRepository.existsByEmail(email)).willReturn(true);
        // when
        assertThatThrownBy(() -> memberService.checkEmailDuplication(email))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_EXISTS_EMAIL);
        // then
    }

    @Test
    @DisplayName("nickname을 입력받아 이미 존재하는지 확인하고 존재하지 않을 경우 아무것도 반환하지 않는다.")
    void given_nickname_when_then_notAnyReturn() {
        // given
        String nickname = "testUser";
        given(memberRepository.existsByNickname(nickname)).willReturn(false);
        // when
        assertThatCode(() -> memberService.checkNicknameDuplication(nickname)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("nickname을 입력받아 이미 존재하는지 확인하고 존재하지 않을 경우 예외가 발생한다.")
    void given_nickname_when_then_throwException() {
        // given
        String nickname = "testUser";
        given(memberRepository.existsByNickname(nickname)).willReturn(true);
        // when
        assertThatThrownBy(() -> memberService.checkNicknameDuplication(nickname))
                .isInstanceOf(MemberException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_EXISTS_NICKNAME);
        // then
    }

    @Test
    @DisplayName("member 정보 수정")
    void given_memberRequestAndImgFile_when_updateMember_then_memberRequestEqualsToUpdateMember() {
        // given
        Long memberId = 1L;
        MemberPrincipal memberPrincipal = FixtureDto.createMemberPrincipal();
        MockMultipartFile imgFile = Fixture.createMultipartFileOfImage("test_image_content");
        MemberRequest memberRequest = FixtureDto.createMemberRequest();
        given(memberRepository.findById(memberId)).willReturn(Optional.of(Fixture.createMember()));
        given(memberCacheRepository.save(any(MemberPrincipal.class))).willReturn(memberPrincipal);
        willDoNothing().given(fileService).uploadFile(eq(imgFile), anyString());
        // when
        Member member = memberService.updateMember(memberPrincipal, memberRequest, imgFile);
        // then
        assertThat(member.getNickname()).isEqualTo(memberRequest.nickname());
        assertThat(member.getAddress()).isEqualTo(memberRequest.address());
    }

    @Test
    @DisplayName("memberId를 입력받아 fileName만 조회한다.")
    void given_memberId_when_then_returnFileName() {
        // given
        Long memberId = 1L;
        String fileName = "file";
        given(memberRepository.findFileNameById(memberId)).willReturn(Optional.of(new FileNameSummary(fileName)));
        // when
        FileNameSummary fileNameSummary = memberService.findFileNameOfMember(memberId);
        // then
        assertThat(fileNameSummary.fileName()).isEqualTo(fileName);
    }

    @Test
    @DisplayName("memberId와 flagLimit을 입력받아 Member의 flagCount + 1하고 업데이트한 값을 반환한다.")
    void given_memberIdAndFlagLimit_when_increaseFlagCount_then_returnUpdatedFlagCount() {
        // given
        Long memberId = 1L;
        int flagLimit = 10;
        int updatedFlagCount = 3;
        willDoNothing().given(memberRepository).updateFlagCount(memberId, flagLimit);
        given(memberRepository.findFlagCountById(memberId))
                .willReturn(Optional.of(new FlagCountSummary(updatedFlagCount)));
        // when
        FlagCountSummary flagCountSummary = memberService.updateFlagCount(memberId, flagLimit);
        // then
        assertThat(flagCountSummary.flagCount()).isEqualTo(updatedFlagCount);
    }

    @Test
    @DisplayName("email과 변경할 password를 입력받아 비밀번호 변경을 수행한다.")
    void given_emailAndNewPassword_when_then_updatePassword() {
        // given
        String email = "testUser@gmail.com", newPassword = "pw123";
        String encodedPassword = "encodedPassword";
        given(encoder.encode(newPassword)).willReturn(encodedPassword);
        willDoNothing().given(memberRepository).updatePasswordByEmail(email, encodedPassword);
        // when
        assertThatCode(() -> memberService.updatePassword(email, newPassword)).doesNotThrowAnyException();
        // then
    }

    @Test
    @DisplayName("changePasswordRequest를 통해 비밀번호 변경을 수행한다.")
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