package freshtrash.freshtrashbackend.service;

import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserDetailsService {

    private final MemberRepository memberRepository;

    public UserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE: " + member.getUserRole().name());

        return new User(member.getEmail(), member.getPassword(), Collections.singletonList(authority));
    }
}