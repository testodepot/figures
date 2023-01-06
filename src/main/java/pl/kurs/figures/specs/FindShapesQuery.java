package pl.kurs.figures.specs;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

public class FindShapesQuery {

    @Nullable
    private String type;

    @Nullable
    private BigDecimal areaFrom;

    @Nullable
    private BigDecimal areaTo;

    @Nullable
    private BigDecimal perimeterFrom;

    @Nullable
    private BigDecimal perimeterTo;

    @Nullable
    private BigDecimal radiusFrom;

    @Nullable
    private BigDecimal radiusTo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Nullable
    private String createdAtFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Nullable
    private String createdAtTo;

    @Nullable
    private BigDecimal lengthFrom;

    @Nullable
    private BigDecimal lengthTo;

    @Nullable
    private BigDecimal widthFrom;

    @Nullable
    private BigDecimal widthTo;

    @Nullable
    private BigDecimal sideFrom;

    @Nullable
    private BigDecimal sideTo;

    @Nullable
    public String getType() {
        return type;
    }

    @Nullable
    public BigDecimal getAreaFrom() {
        return areaFrom;
    }

    @Nullable
    public BigDecimal getAreaTo() {
        return areaTo;
    }

    @Nullable
    public BigDecimal getPerimeterFrom() {
        return perimeterFrom;
    }

    @Nullable
    public BigDecimal getPerimeterTo() {
        return perimeterTo;
    }

    @Nullable
    public BigDecimal getRadiusFrom() {
        return radiusFrom;
    }

    @Nullable
    public BigDecimal getRadiusTo() {
        return radiusTo;
    }

    @Nullable
    public String getCreatedAtFrom() {
        return createdAtFrom;
    }

    @Nullable
    public String getCreatedAtTo() {
        return createdAtTo;
    }

    @Nullable
    public BigDecimal getLengthFrom() {
        return lengthFrom;
    }

    @Nullable
    public BigDecimal getLengthTo() {
        return lengthTo;
    }

    @Nullable
    public BigDecimal getWidthFrom() {
        return widthFrom;
    }

    @Nullable
    public BigDecimal getWidthTo() {
        return widthTo;
    }

    @Nullable
    public BigDecimal getSideFrom() {
        return sideFrom;
    }

    @Nullable
    public BigDecimal getSideTo() {
        return sideTo;
    }

    public void setType(@Nullable String type) {
        this.type = type;
    }

    public void setAreaFrom(@Nullable BigDecimal areaFrom) {
        this.areaFrom = areaFrom;
    }

    public void setAreaTo(@Nullable BigDecimal areaTo) {
        this.areaTo = areaTo;
    }

    public void setPerimeterFrom(@Nullable BigDecimal perimeterFrom) {
        this.perimeterFrom = perimeterFrom;
    }

    public void setPerimeterTo(@Nullable BigDecimal perimeterTo) {
        this.perimeterTo = perimeterTo;
    }

    public void setRadiusFrom(@Nullable BigDecimal radiusFrom) {
        this.radiusFrom = radiusFrom;
    }

    public void setRadiusTo(@Nullable BigDecimal radiusTo) {
        this.radiusTo = radiusTo;
    }

    public void setCreatedAtFrom(@Nullable String createdAtFrom) {
        this.createdAtFrom = createdAtFrom;
    }

    public void setCreatedAtTo(@Nullable String createdAtTo) {
        this.createdAtTo = createdAtTo;
    }

    public void setLengthFrom(@Nullable BigDecimal lengthFrom) {
        this.lengthFrom = lengthFrom;
    }

    public void setLengthTo(@Nullable BigDecimal lengthTo) {
        this.lengthTo = lengthTo;
    }

    public void setWidthFrom(@Nullable BigDecimal widthFrom) {
        this.widthFrom = widthFrom;
    }

    public void setWidthTo(@Nullable BigDecimal widthTo) {
        this.widthTo = widthTo;
    }

    public void setSideFrom(@Nullable BigDecimal sideFrom) {
        this.sideFrom = sideFrom;
    }

    public void setSideTo(@Nullable BigDecimal sideTo) {
        this.sideTo = sideTo;
    }
}
