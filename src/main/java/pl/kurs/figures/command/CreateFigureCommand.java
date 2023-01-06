package pl.kurs.figures.command;

import pl.kurs.figures.validation.BigDecimalCheck;
import pl.kurs.figures.validation.CreateCommandCheck;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.List;

@CreateCommandCheck
public class CreateFigureCommand {

    @Pattern(regexp = "[A-Za-z]+", message = "type should contains only letters!")
    @NotBlank(message = "type should not be blank or null!")
    private String type;

    @NotEmpty(message = "list of parameterrs should not be empty!")
    @BigDecimalCheck
    private List<BigDecimal> parameters;

    public CreateFigureCommand(@Pattern(regexp = "[A-Za-z]+", message = "type should contains only letters!") @NotBlank(message = "type should not be blank or null!") String type, @NotEmpty(message = "list of parameterrs should not be empty!") @BigDecimalCheck List<BigDecimal> parameters) {
        this.type = type;
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public List<BigDecimal> getParameters() {
        return parameters;
    }
}
