package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Rectangle;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AbstractFigureWithAreaRectangle implements Specification<AbstractFigure> {

    @Nullable
    private BigDecimal areaFromRectangle;

    @Nullable
    private BigDecimal areaToRectangle;


    public AbstractFigureWithAreaRectangle(@Nullable BigDecimal areaFromRectangle, @Nullable BigDecimal areaToRectangle) {
        this.areaFromRectangle = areaFromRectangle;
        this.areaToRectangle = areaToRectangle;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Root<Rectangle> rectangleRoot = criteriaBuilder.treat(root, Rectangle.class);

        Expression<BigDecimal> rectangleArea = criteriaBuilder.function("calculateAreaRectangle", BigDecimal.class, rectangleRoot.get("width"), rectangleRoot.get("length"));


        if (areaFromRectangle != null) {
            Predicate areaRectangle = criteriaBuilder.greaterThanOrEqualTo(rectangleArea, areaFromRectangle);
            predicates.add(areaRectangle);
        }

        if (areaToRectangle != null) {
            Predicate areaRectangleTo = criteriaBuilder.lessThanOrEqualTo(rectangleArea, areaToRectangle);
            predicates.add(areaRectangleTo);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public BigDecimal getAreaToRectangle() {
        return areaToRectangle;
    }

    public void setAreaToRectangle(@Nullable BigDecimal areaToRectangle) {
        this.areaToRectangle = areaToRectangle;
    }

    @Nullable
    public BigDecimal getAreaFromRectangle() {
        return areaFromRectangle;
    }

    public void setAreaFromRectangle(@Nullable BigDecimal areaFromRectangle) {
        this.areaFromRectangle = areaFromRectangle;
    }
}
