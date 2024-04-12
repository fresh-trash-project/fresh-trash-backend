package freshtrash.freshtrashbackend.repository.custom;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.entity.Waste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomWasteRepository {
    Page<Waste> findAll(String district, Predicate predicate, Pageable pageable);
}
