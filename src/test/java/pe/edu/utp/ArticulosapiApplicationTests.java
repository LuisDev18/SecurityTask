package pe.edu.utp;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArticulosapiApplicationTests {

	@Value("${spring.datasource.url}")
	private String dataSourceUrl;
	@Test
	void contextLoads() {
		System.out.println("DataSource URL: " + dataSourceUrl);
	}

}
