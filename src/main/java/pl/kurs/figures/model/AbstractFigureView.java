package pl.kurs.figures.model;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "view_abstract_figure")
@Audited
public class AbstractFigureView implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Long modelId;

    private BigDecimal area;

    private BigDecimal perimeter;

    public AbstractFigureView() {
    }

    public AbstractFigureView(BigDecimal area, BigDecimal perimeter) {
        this.area = area;
        this.perimeter = perimeter;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long figureId) {
        this.modelId = figureId;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(BigDecimal perimeter) {
        this.perimeter = perimeter;
    }

}
