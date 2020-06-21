package springbootstarter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Import(ConfigProperties.class)
public class SpringbootstarterApplication extends SpringBootServletInitializer {

	
	public static void main(String[] args) {
		SpringApplicationBuilder builder = configuration(new SpringApplicationBuilder());
		SpringApplication app = builder.build();
		Environment env = app.run(args).getEnvironment();
		System.out.println("appversion=" + env.getProperty("appversion"));
		System.out.println("port=" + env.getProperty("server.port"));
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configuration(builder);
    }
	
	private static SpringApplicationBuilder configuration(SpringApplicationBuilder builder) {
        return builder.sources(SpringbootstarterApplication.class);
    }
}
