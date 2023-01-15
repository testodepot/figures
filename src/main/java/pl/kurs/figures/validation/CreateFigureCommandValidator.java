package pl.kurs.figures.validation;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.strategy.CreatingStrategy;
import pl.kurs.figures.strategy.CreatingStrategyFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.Set;

public class CreateFigureCommandValidator implements ConstraintValidator<CreateCommandCheck, CreateFigureCommand> {

    private CreatingStrategyFactory creatingStrategyFactory;

    public CreateFigureCommandValidator(CreatingStrategyFactory creatingStrategyFactory) {
        this.creatingStrategyFactory = creatingStrategyFactory;
    }

    @Override
    public boolean isValid(CreateFigureCommand obj, ConstraintValidatorContext constraintValidatorContext) {

        Map<String, CreatingStrategy> strategies = creatingStrategyFactory.getStrategies();

        CreatingStrategy strategy = creatingStrategyFactory.findStrategy(StringUtils.capitalize(obj.getType()));

        boolean isFigureTypeAvailable = strategies.containsValue(strategy);

        Set<String> strategiesSet = strategies.keySet();

        if (!isFigureTypeAvailable) {
            String error = "not a accepted type of figure! Accepted types: " + strategiesSet.toString();
            HibernateConstraintValidatorContext hibernateContext = constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext
                    .addMessageParameter( "errors", error )
                    .buildConstraintViolationWithTemplate("{checkiftypeoffigureisaccepted.message}")
                    .addConstraintViolation();
            return false;
        }

        int counter = strategy.getNumberOfParamsOfSpecificFigure();

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


}
