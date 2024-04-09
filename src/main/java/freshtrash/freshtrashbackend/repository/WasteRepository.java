package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Waste;

import freshtrash.freshtrashbackend.repository.projections.FileNameSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WasteRepository extends JpaRepository<Waste, Long> {

    @EntityGraph(attributePaths = "member")
    Optional<Waste> findById(Long wasteId);

    @EntityGraph(attributePaths = "member")
    Page<Waste> findAll(Pageable pageable);

    Optional<FileNameSummary> findFileNameById(Long wasteId);

    boolean existsByIdAndMember_Id(Long wasteId, Long memberId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Waste w set w.likeCount = w.likeCount + ?2 where w.id = ?1")
    int updateLikeCount(Long wasteId, int updateCount);
}
