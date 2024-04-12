package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.exception.AuthException;
import freshtrash.freshtrashbackend.exception.MemberException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import freshtrash.freshtrashbackend.security.TokenProvider;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final FileService fileService;

    public Member getMemberEntityByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 회원 가입
     */
    public void registerMember(Member member) {
        if (checkEmailDuplication(member.getEmail())) {
            throw new MemberException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
        if (checkNicknameDuplication(member.getNickname())) {
            throw new MemberException(ErrorCode.ALREADY_EXISTS_NICKNAME);
        }
        member.setPassword(encoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    /**
     * 로그인
     */
    public LoginResponse signIn(String email, String password) {
        Member member = getMemberEntityByEmail(email);
        checkPassword(password, member.getPassword());
        // 토큰 발급
        String accessToken = generateAccessToken(email, member);
        return LoginResponse.of(accessToken);
    }

    /**
     * 이메일 중복 체크
     */
    public boolean checkEmailDuplication(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * 닉네임 중복확인
     */
    public boolean checkNicknameDuplication(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    /**
     * 비밀번호 일치 확인
     * @param inputPassword 입력한 비밀번호
     * @param existPassword 기존 비밀번호
     */
    private void checkPassword(String inputPassword, String existPassword) {
        if (!encoder.matches(inputPassword, existPassword)) {
            throw new AuthException("not matched password");
        }
    }

    /**
     * AccessToken 발급
     */
    private String generateAccessToken(String email, Member member) {
        return tokenProvider.generateAccessToken(
                email,
                member.getNickname(),
                String.format("%s:%s", member.getId(), member.getUserRole().getName()),
                member.getRating(),
                member.getFileName(),
                member.getAddress());
    }

    /**
     * member 정보 조회
     */
    public Member getMemberEntity(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public Member updateMember(Long memberId, MemberRequest memberRequest, MultipartFile imgFile) {
        String updatedFileName = FileUtils.generateUniqueFileName(imgFile);
        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));

        if (checkNicknameDuplication(memberRequest.nickname())) {
            throw new MemberException(ErrorCode.ALREADY_EXISTS_NICKNAME);
        }

        // 파일은 유효할 경우에만 수정
        if (FileUtils.isValid(imgFile)) {
            String savedFileName = member.getFileName();
            member.setFileName(updatedFileName);
            // 수정된 파일 저장
            fileService.uploadFile(imgFile, updatedFileName);

            if (!Objects.isNull(savedFileName) && !savedFileName.isBlank()) {
                // 이전 파일 삭제
                fileService.deleteFileIfExists(savedFileName);
            }
        }

        member.setNickname(memberRequest.nickname());
        member.setAddress(memberRequest.address());
        return memberRepository.save(member);
    }
}
