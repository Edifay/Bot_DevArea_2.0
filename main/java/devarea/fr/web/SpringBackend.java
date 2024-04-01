package devarea.fr.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import static devarea.fr.discord.Core.initStatus;

@SpringBootApplication
public class SpringBackend extends SpringBootServletInitializer {

    public static void checkStatus() {
        if (!initStatus)
            throw new IllegalStateException("Server is starting try again later !");
    }

}
