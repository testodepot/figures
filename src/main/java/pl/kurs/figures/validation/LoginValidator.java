package pl.kurs.figures.validation;

import org.springframework.stereotype.Component;
import pl.kurs.figures.service.UserService;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class LoginValidator implements ConstraintValidator<CheckIfLoginNotTaken, String> {

    private UserService userService;

    public LoginValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !userService.existsByLogin(s);
    }
}
