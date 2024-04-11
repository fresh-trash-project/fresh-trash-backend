package freshtrash.freshtrashbackend.repository.custom;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import freshtrash.freshtrashbackend.entity.QWaste;
import freshtrash.freshtrashbackend.entity.Waste;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomWasteRepositoryImpl implements CustomWasteRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Waste> findAll(String district, Predicate predicate, Pageable pageable) {
        List<Waste> wastes = jpaQueryFactory
                .selectFrom(QWaste.waste)
                .where(Expressions.booleanTemplate(
                                "JSON_CONTAINS({0}, {1}, {2})",
                                QWaste.waste.address,
                                Expressions.stringTemplate("JSON_QUOTE({0})", district),
                                "$.district")
                        .isTrue().or(predicate))
                .leftJoin(QWaste.waste.member)
                .fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(wastes, pageable, wastes.size());
    }
}
