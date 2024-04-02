package freshtrash.freshtrashbackend.repository;

import freshtrash.freshtrashbackend.entity.Waste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteRepository extends JpaRepository<Waste, Long> {}
