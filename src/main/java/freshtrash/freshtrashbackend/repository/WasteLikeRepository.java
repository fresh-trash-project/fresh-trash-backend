package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.WasteLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface WasteLikeRepository extends JpaRepository<WasteLike, Long> {
    boolean existsByMemberIdAndWasteId(Long memberId, Long wasteId);

    void deleteByMemberIdAndWasteId(Long memberId, Long wasteId);

    @EntityGraph(attributePaths = {"waste", "waste.member"})
    Page<WasteLike> findAllByMember_Id(Long memberId, Pageable pageable);
}
