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

public class AbstractFigureWithCreatedAtFrom implements Specification<AbstractFigure> {

    @Nullable
    private String createdAtFrom;

    public AbstractFigureWithCreatedAtFrom(String createdAtFrom) {
        this.createdAtFrom = createdAtFrom;
    }

    @Override
    public Predicate toPredicate(Root<AbstractFigure> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (createdAtFrom != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date dateFrom = null;
            try {
                dateFrom = formatter.parse(createdAtFrom);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Path<Date> datePath = root.get("createdAt");
            Predicate date = criteriaBuilder.greaterThanOrEqualTo(datePath, dateFrom);
            predicates.add(date);
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    @Nullable
    public String getCreatedAtFrom() {
        return createdAtFrom;
    }

    public void setCreatedAtFrom(@Nullable String createdAtFrom) {
        this.createdAtFrom = createdAtFrom;
    }
}
