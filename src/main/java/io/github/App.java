package io.github;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import java.util.Collections;

/**
 * SpringBoot
 *
 * @author Joey
 * @Email 2434387555@qq.com
 */
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"io.github"})
public class App extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.setBannerMode(Mode.CONSOLE);
        app.run(args);
    }

    /**
     * 部署Tomcat---mvn clean package(如果使用Tomcat将packaging改为war)
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(App.class);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        // This will set to use COOKIE only
        servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
        // This will prevent any JS on the page from accessing the
        // cookie - it will only be used/accessed by the HTTP transport
        // mechanism in use
        SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
        logger.debug(String.format("sessionCookieConfig.isHttpOnly()===>%s", sessionCookieConfig.isHttpOnly()));
        sessionCookieConfig.setHttpOnly(true);
    }
}
