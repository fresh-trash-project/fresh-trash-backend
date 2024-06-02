package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.entity.constants.UserRole;
import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import freshtrash.freshtrashbackend.repository.projections.FlagCountSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long memberId);

    Optional<Member> findByEmail(String email);

    Optional<FlagCountSummary> findFlagCountById(Long memberId);

    Optional<FileNameSummary> findFileNameById(Long memberId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Query(nativeQuery = true, value = "update members m set m.flag_count = m.flag_count + 1 where m.id = ?1")
    void updateFlagCount(Long memberId);

    @Query(nativeQuery = true, value = "update members m set m.user_role = ?2 where m.id = ?1")
    void updateUserRoleById(Long targetMemberId, UserRole userRole);
}
