package pl.kurs.figures.specs;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import pl.kurs.figures.model.AbstractFigure;
import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AbstractFigureWithCreatedAtTo implements Specification<AbstractFigure> {

    @Nullable
    private String createdAtTo;


    public AbstractFigureWithCreatedAtTo(@Nullable String createdAtTo) {
        this.createdAtTo = createdAtTo;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (createdAtTo != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date dateTo = null;
            try {
                dateTo = formatter.parse(createdAtTo);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Path<Date> datePath = root.get("createdAt");
            Predicate date = criteriaBuilder.lessThanOrEqualTo(datePath, dateTo);
            predicates.add(date);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public String getCreatedAtTo() {
        return createdAtTo;
    }

    public void setCreatedAtTo(@Nullable String createdAtTo) {
        this.createdAtTo = createdAtTo;
    }
}
