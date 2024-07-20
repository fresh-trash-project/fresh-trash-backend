package freshtrash.freshtrashbackend.repository.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import freshtrash.freshtrashbackend.entity.Alarm;
import freshtrash.freshtrashbackend.entity.Product;
import freshtrash.freshtrashbackend.entity.QAlarm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomAlarmRepositoryImpl implements CustomAlarmRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Alarm> findAllByMember_Id(Long memberId, Boolean isRead, Pageable pageable) {
        QAlarm alarm = QAlarm.alarm;
        // where
        Predicate predicate = isRead
                ? alarm.readAt.isNotNull().and(alarm.memberId.eq(memberId))
                : alarm.readAt.isNull().and(alarm.memberId.eq(memberId));
        // count
        Long NotReadAlarmCount = jpaQueryFactory
                .select(alarm.count())
                .from(alarm)
                .where(predicate)
                .fetchFirst();
        // select
        List<Alarm> alarms = jpaQueryFactory
                .selectFrom(alarm)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        return new PageImpl<>(alarms, pageable, NotReadAlarmCount);
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
