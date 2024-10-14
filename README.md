import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.mockito.Mockito.*;
import org.springframework.boot.SpringApplication;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = IMDataLineageApplication.class)
@TestPropertySource("classpath:cat.properties")
class IMDataLineageApplicationTest {

    @Test
    void contextLoads() {
        // This test simply ensures the Spring Boot application context loads successfully
    }

    @Test
    void testMainMethod() {
        // Mock SpringApplication.run to ensure it's called
        try (var mockedSpringApplication = mockStatic(SpringApplication.class)) {
            String[] args = {};
            IMDataLineageApplication.main(args);
            // Verify that SpringApplication.run is called with the correct class and args
            mockedSpringApplication.verify(() -> SpringApplication.run(IMDataLineageApplication.class, args));
        }
    }
}
