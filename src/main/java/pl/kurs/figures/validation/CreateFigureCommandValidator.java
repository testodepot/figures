package pl.kurs.figures.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

public class CreateFigureCommandValidator implements ConstraintValidator<CommandCheck, CreateFigureCommand> {

    @Value("${figures}")
    private List<String> figures;

    public CreateFigureCommandValidator(List<String> figures) {
        this.figures = figures;
    }

    @Override
    public boolean isValid(CreateFigureCommand createFigureCommand, ConstraintValidatorContext constraintValidatorContext) {

        boolean contains = figures.contains(StringUtils.capitalize(createFigureCommand.getType()));

        if (!contains) {
            String error = "not a accepted type of figure! Accepted types: " + figures;
            HibernateConstraintValidatorContext hibernateContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter( "errors", error )
                    .buildConstraintViolationWithTemplate("{checkiftypeoffigureisaccepted.message}")
                    .addConstraintViolation();
            return false;
        }

        int counter = countParamsForSpecificClass(createFigureCommand);

        if (createFigureCommand.getParameters().size() != counter) {
            String error = "wrong number of params for this figure type: " + createFigureCommand.getType();
            HibernateConstraintValidatorContext hibernateContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter( "errors", error )
                    .buildConstraintViolationWithTemplate("{parametersforspecificfigurecheck.message}")
                    .addConstraintViolation();

            return false;
        }
        return true;
    }

    private int countParamsForSpecificClass(CreateFigureCommand createFigureCommand) {
        Class<?> figureClass = null;
        try {
            figureClass = Class.forName("pl.kurs.figures.model." + StringUtils.capitalize(createFigureCommand.getType()));
        } catch (ClassNotFoundException e) {
            throw new BadEntityException("not a accepted type of figure! Accepted types: " + figures);
        }
        Field[] fields = figureClass.getDeclaredFields();
        int counter = 0;
        for (Field f : fields) {
            if (f.getType() == BigDecimal.class) {
                counter = counter + 1;
            }
        }
        return counter;
    }


}
