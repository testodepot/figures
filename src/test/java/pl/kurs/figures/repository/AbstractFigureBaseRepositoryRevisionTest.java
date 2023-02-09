package pl.kurs.figures.repository;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Circle;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.User;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AbstractFigureBaseRepositoryRevisionTest {


    @Autowired
    private AbstractFigureBaseRepository abstractFigureBaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private Authentication auth;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        Mockito.when(auth.getName()).thenReturn("adam123");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    public void initialRevision() {
        entityManager = entityManager.getEntityManagerFactory().createEntityManager();
        Session session = (Session) entityManager.unwrap(Session.class);

        User newUser = new User("adam", "kocik", "adam123", "123");
        Role creatorRole = new Role("CREATOR");
        creatorRole.setId(1);
        newUser.addRole(creatorRole);
        long millis = System.currentTimeMillis();
        Circle circle = new Circle(BigDecimal.valueOf(50));
        circle.setType("Circle");
        circle.setCreatedAt(new Date(millis));
        circle.setLastModifiedAt(new Date(millis));
        circle.setCreatedBy(newUser);
        circle.setLastModifiedBy("adam123");

        entityManager.getTransaction().begin();
        entityManager.persist(circle);
        circle.setRadius(BigDecimal.valueOf(80));
        entityManager.merge(circle);
        entityManager.getTransaction().commit();
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List revisions = auditReader.createQuery()
                .forRevisionsOfEntity(AbstractFigure.class, true)
                .add(AuditEntity.id().eq(circle.getId()))
                .getResultList();

        assertEquals(1, revisions.size());
        abstractFigureBaseRepository.deleteById(circle.getId());
    }
}
