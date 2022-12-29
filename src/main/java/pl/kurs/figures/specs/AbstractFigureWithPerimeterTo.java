package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithPerimeterTo implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal perimeterTo;

    public AbstractFigureWithPerimeterTo(@Nullable BigDecimal perimeterTo) {
        this.perimeterTo = perimeterTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (perimeterTo != null) {
            Predicate area = criteriaBuilder.lessThanOrEqualTo(root.get("perimeter"), perimeterTo);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
