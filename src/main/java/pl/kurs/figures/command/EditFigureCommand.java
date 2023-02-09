package pl.kurs.figures.command;

import pl.kurs.figures.validation.BigDecimalCheck;
import pl.kurs.figures.validation.EditCommandCheck;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@EditCommandCheck
public class EditFigureCommand {

    private Long idFigure;

    @NotEmpty(message = "list of params cant be empty!")
    @BigDecimalCheck
    private List<BigDecimal> parameters;

    public EditFigureCommand(Long idFigure, @NotEmpty(message = "list of params cant be empty!") @BigDecimalCheck List<BigDecimal> parameters) {
        this.idFigure = idFigure;
        this.parameters = parameters;
    }

    public Long getIdFigure() {
        return idFigure;
    }

    public void setIdFigure(Long idFigure) {
        this.idFigure = idFigure;
    }

    public List<BigDecimal> getParameters() {
        return parameters;
    }

    public void setParameters(List<BigDecimal> parameters) {
        this.parameters = parameters;
    }
}
