package pl.kurs.figures.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.exception.BadEntityException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.util.List;

@Component
public class ObjectMaker {

    @Value("${figures}")
    private List<String> figures;

    public ObjectMaker(List<String> figures) {
        this.figures = figures;
    }

    public Object makeObject(CreateFigureCommand createFigureCommand) {
        boolean isCorrect = checkIfFigureIsCorrect(StringUtils.capitalize(createFigureCommand.getType()));
        try  {
            if (isCorrect) {

                if (createFigureCommand.getParameters().size() == 1) {
                    Class clazz = Class.forName("pl.kurs.figures.model." + StringUtils.capitalize(createFigureCommand.getType()));
                    Class[] parameters = new Class[]{BigDecimal.class};
                    Constructor constructor = clazz.getConstructor(parameters);
                    Object o = constructor.newInstance(createFigureCommand.getParameters().get(0));
                    return o;
                }

                if (createFigureCommand.getParameters().size() == 2) {
                    Class clazz = Class.forName("pl.kurs.figures.model." + StringUtils.capitalize(createFigureCommand.getType()));
                    Class[] parameters = new Class[]{BigDecimal.class, BigDecimal.class};
                    Constructor constructor = clazz.getConstructor(parameters);
                    Object o = constructor.newInstance(createFigureCommand.getParameters().get(0), createFigureCommand.getParameters().get(1));
                    return o;
                }

                if (createFigureCommand.getParameters().size() == 3) {
                    Class clazz = Class.forName("pl.kurs.figures.model." + StringUtils.capitalize(createFigureCommand.getType()));
                    Class[] parameters = new Class[]{BigDecimal.class, BigDecimal.class, BigDecimal.class};
                    Constructor constructor = clazz.getConstructor(parameters);
                    Object o = constructor.newInstance(createFigureCommand.getParameters().get(0), createFigureCommand.getParameters().get(1), createFigureCommand.getParameters().get(2));
                    return o;
                }

            }
        } catch (Exception e) {
            throw new BadEntityException("No figure model of this type! Available types: " + figures);
        }
        throw new BadEntityException("No figure model of this type! Available types: " + figures);
    }

    public boolean checkIfFigureIsCorrect(String figureType) {
        return figures.contains(StringUtils.capitalize(figureType));
    }

    public List<String> getFigures() {
        return figures;
    }

    public void setFigures(List<String> figures) {
        this.figures = figures;
    }
}
