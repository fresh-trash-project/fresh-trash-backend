package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Member;
import freshtrash.freshtrashbackend.dto.projections.FileNameSummary;
import freshtrash.freshtrashbackend.dto.projections.FlagCountSummary;
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

    @Query(
            nativeQuery = true,
            value =
                    """
            update members m
            set m.flag_count = m.flag_count + 1,
                m.user_role = case
                    when m.flag_count >= ?2 then 'BLACK_USER'
                    else m.user_role
                end
            where m.id = ?1
            """)
    void updateFlagCount(Long memberId, int flagLimit);

    @Query(nativeQuery = true, value = "update members m set m.password = ?2 where m.email = ?1")
    void updatePasswordByEmail(String email, String encodedPassword);
}
