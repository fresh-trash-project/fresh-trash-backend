package freshtrash.freshtrashbackend.domain.product.repository.custom;

import com.querydsl.core.types.Predicate;
import freshtrash.freshtrashbackend.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProductRepository {
    Page<Product> findAll(String district, Predicate predicate, Pageable pageable);
}
