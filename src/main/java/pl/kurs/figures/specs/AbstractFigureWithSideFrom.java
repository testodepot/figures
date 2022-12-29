package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Square;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithSideFrom implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal sideFrom;

    public AbstractFigureWithSideFrom(@Nullable BigDecimal sideFrom) {
        this.sideFrom = sideFrom;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Square> squareRoot = criteriaBuilder.treat(root, Square.class);

        if (sideFrom != null) {
            Predicate area = criteriaBuilder.greaterThanOrEqualTo(squareRoot.get("side"), sideFrom);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getSideFrom() {
        return sideFrom;
    }

    public void setSideFrom(@Nullable BigDecimal sideFrom) {
        this.sideFrom = sideFrom;
    }
}
