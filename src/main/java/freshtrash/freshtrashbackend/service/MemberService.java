package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.dto.request.MemberRequest;
import freshtrash.freshtrashbackend.dto.response.LoginResponse;
import freshtrash.freshtrashbackend.dto.security.MemberPrincipal;
import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.exception.AuthException;
import freshtrash.freshtrashbackend.exception.MemberException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.MemberCacheRepository;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import freshtrash.freshtrashbackend.security.TokenProvider;
import freshtrash.freshtrashbackend.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberCacheRepository memberCacheRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final FileService fileService;

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public MemberPrincipal getMemberCache(Long memberId) {
        return memberCacheRepository
                .findById(memberId)
                .orElseGet(() -> MemberPrincipal.fromEntity(getMemberById(memberId)));
    }

    /**
     * 회원 가입
     */
    public void registerMember(Member member) {
        checkEmailDuplication(member.getEmail());
        checkNicknameDuplication(member.getNickname());
        member.setPassword(encoder.encode(member.getPassword()));
        memberRepository.save(member);
    }

    /**
     * 로그인
     */
    public LoginResponse signIn(String email, String password) {
        Member member = getMemberByEmail(email);
        checkPassword(password, member.getPassword());
        // 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(member.getId());
        memberCacheRepository.save(MemberPrincipal.fromEntity(member));
        return LoginResponse.of(accessToken);
    }

    /**
     * 이메일 중복 체크
     */
    public void checkEmailDuplication(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
    }

    /**
     * 닉네임 중복 체크
     */
    public void checkNicknameDuplication(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new MemberException(ErrorCode.ALREADY_EXISTS_NICKNAME);
        }
    }

    /**
     * member 정보 조회
     */
    public Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * member 정보 수정
     */
    @Transactional
    public Member updateMember(Long memberId, MemberRequest memberRequest, MultipartFile imgFile) {
        checkNicknameDuplication(memberRequest.nickname());

        Member member =
                memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
        member.setNickname(memberRequest.nickname());
        member.setAddress(memberRequest.address());

        // 파일은 유효할 경우에만 수정
        if (FileUtils.isValid(imgFile)) {
            String updatedFileName = FileUtils.generateUniqueFileName(imgFile);
            member.setFileName(updatedFileName);
            // 수정된 파일 저장
            fileService.uploadFile(imgFile, updatedFileName);
        }
        memberCacheRepository.save(MemberPrincipal.fromEntity(member));

        return member;
    }

    /**
     * 이전 파일 삭제
     */
    public void deleteOldFile(String fileName) {
        if (StringUtils.hasText(fileName)) {
            fileService.deleteFileIfExists(fileName);
        }
    }

    public FileNameSummary findFileNameOfMember(Long memberId) {
        return memberRepository
                .findFileNameById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
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
}
