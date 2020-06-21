package springbootstarter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	private final Logger logger = LoggerFactory.getLogger(HelloController.class);
	
	@Value( "${appversion}" )
	private String appversion;
	
	@RequestMapping("/hello")
	public String index() {
		logger.debug("appversion="+appversion);
		return "Greetings from Spring Boot!";
	}

}