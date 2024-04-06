package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.exception.MemberException;
import freshtrash.freshtrashbackend.exception.constants.ErrorCode;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getMemberEntityByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    /**
     * 회원 가입
     */
    public void registerMember(Member entity) {
        if (checkEmailDuplication(entity.getEmail())) {
            throw new MemberException(ErrorCode.ALREADY_EXISTS_EMAIL);
        }
        if (checkNicknameDuplication(entity.getNickname())) {
            throw new MemberException(ErrorCode.ALREADY_EXISTS_NICKNAME);
        }
        memberRepository.save(entity);
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
}
