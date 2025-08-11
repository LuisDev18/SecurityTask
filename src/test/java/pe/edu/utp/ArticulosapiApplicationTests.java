package pe.edu.utp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ArticulosapiApplicationTests {

  @Container
  static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
    .withDatabaseName("testdb")
    .withUsername("testuser")
    .withPassword("testpass")
    .withCommand("--log_bin_trust_function_creators=1");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", mySQLContainer::getUsername);
    registry.add("spring.datasource.password", mySQLContainer::getPassword);
  }

  @Test
  void contextLoads() {}
}
