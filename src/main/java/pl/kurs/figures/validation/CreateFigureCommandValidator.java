package pl.kurs.figures.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import pl.kurs.figures.strategy.CreatingStrategy;
import pl.kurs.figures.strategy.CreatingStrategyFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CreateFigureCommandValidator implements ConstraintValidator<CreateCommandCheck, CreateFigureCommand> {

    private CreatingStrategyFactory creatingStrategyFactory;

    @Value("${figures}")
    private List<String> figures;

    public CreateFigureCommandValidator(CreatingStrategyFactory creatingStrategyFactory, List<String> figures) {
        this.creatingStrategyFactory = creatingStrategyFactory;
        this.figures = figures;
    }

    @Override
    public boolean isValid(CreateFigureCommand obj, ConstraintValidatorContext constraintValidatorContext) {

        Map<String, CreatingStrategy> strategies = creatingStrategyFactory.getStrategies();

        CreatingStrategy strategy = creatingStrategyFactory.findStrategy(StringUtils.capitalize(obj.getType()));

        boolean isFigureTypeAvailable = strategies.containsValue(strategy);

        if (!isFigureTypeAvailable) {
            String error = "not a accepted type of figure! Accepted types: " + figures;
            HibernateConstraintValidatorContext hibernateContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter( "errors", error )
                    .buildConstraintViolationWithTemplate("{checkiftypeoffigureisaccepted.message}")
                    .addConstraintViolation();
            return false;
        }

        int counter = countParamsForSpecificClass(obj, strategies);

        if (obj.getParameters().size() != counter) {
            String error = "wrong number of params for this figure type: " + obj.getType();
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

    private int countParamsForSpecificClass(CreateFigureCommand createFigureCommand, Map<String, CreatingStrategy> strategies) {
        Class<?> figureClass = null;
        try {
            figureClass = Class.forName("pl.kurs.figures.model." + StringUtils.capitalize(createFigureCommand.getType()));
        } catch (ClassNotFoundException e) {
            throw new BadEntityException("not a accepted type of figure! Accepted types: " + strategies.values().toString());
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
