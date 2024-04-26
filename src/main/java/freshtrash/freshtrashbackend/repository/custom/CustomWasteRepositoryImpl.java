package freshtrash.freshtrashbackend.repository.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import freshtrash.freshtrashbackend.entity.QWaste;
import freshtrash.freshtrashbackend.entity.Waste;
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
public class CustomWasteRepositoryImpl implements CustomWasteRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Waste> findAll(String district, Predicate predicate, Pageable pageable) {
        // district가 입력되었을 경우 predicate
        if (StringUtils.hasText(district)) {
            predicate = Expressions.booleanTemplate(
                            "JSON_CONTAINS({0}, {1}, {2})",
                            QWaste.waste.address, Expressions.stringTemplate("JSON_QUOTE({0})", district), "$.district")
                    .isTrue()
                    .or(predicate);
        }
        QWaste waste = QWaste.waste;
        Long totalOfElements = jpaQueryFactory.select(waste.count()).from(waste).where(predicate).fetchFirst();
        List<Waste> wastes = jpaQueryFactory
                .selectFrom(waste)
                .where(predicate)
                .leftJoin(waste.member)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return new PageImpl<>(wastes, pageable, totalOfElements);
    }

    private OrderSpecifier[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier> orders = new ArrayList<>();
        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder<Waste> entityPath = new PathBuilder<>(Waste.class, "waste");
            orders.add(new OrderSpecifier(direction, entityPath.get(order.getProperty())));
        });
        return orders.toArray(OrderSpecifier[]::new);
    }
}
