package freshtrash.freshtrashbackend.repository.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.entity.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Product> findAll(String district, Predicate predicate, Pageable pageable) {
        // district가 입력되었을 경우 predicate
        if (StringUtils.hasText(district)) {
            predicate = Expressions.booleanTemplate(
                            "JSON_CONTAINS({0}, {1}, {2})",
                            QProduct.product.address, Expressions.stringTemplate("JSON_QUOTE({0})", district), "$.district")
                    .isTrue()
                    .or(predicate);
        }
        QProduct product = QProduct.product;
        Long totalOfElements = jpaQueryFactory.select(product.count()).from(product).where(predicate).fetchFirst();
        List<Product> products = jpaQueryFactory
                .selectFrom(product)
                .where(predicate)
                .leftJoin(product.member)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return new PageImpl<>(products, pageable, totalOfElements);
    }

    private OrderSpecifier[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder<Product> entityPath = new PathBuilder<>(Product.class, "product");
            orders.add(new OrderSpecifier(direction, entityPath.get(order.getProperty())));
        });
        return orders.toArray(OrderSpecifier[]::new);
    }
}
