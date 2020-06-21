package springbootstarter;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigProperties {
	private String appversion;

	public String getAppversion() {
		return appversion;
	}

	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}
}
