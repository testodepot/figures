package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithWidthFrom implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal widthFrom;

    public AbstractFigureWithWidthFrom(@Nullable BigDecimal widthFrom) {
        this.widthFrom = widthFrom;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Rectangle> rectangleRoot = criteriaBuilder.treat(root, Rectangle.class);

        if (widthFrom != null) {
            Predicate area = criteriaBuilder.greaterThanOrEqualTo(rectangleRoot.get("width"), widthFrom);
            predicates.add(area);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getWidthFrom() {
        return widthFrom;
    }

    public void setWidthFrom(@Nullable BigDecimal widthFrom) {
        this.widthFrom = widthFrom;
    }
}
