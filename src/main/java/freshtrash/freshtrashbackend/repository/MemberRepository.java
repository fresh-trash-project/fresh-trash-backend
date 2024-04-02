package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {}
