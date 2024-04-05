package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Waste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WasteRepository extends JpaRepository<Waste, Long> {
    @Query("select w.fileName from Waste w where w.id = ?1")
    Optional<String> findFileNameById(Long wasteId);

    boolean existsByIdAndMember_Id(Long wasteId, Long memberId);
}
