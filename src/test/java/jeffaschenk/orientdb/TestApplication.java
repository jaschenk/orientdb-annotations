package jeffaschenk.orientdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * User: jaschenk
 * Date: 11/5/16
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"jeffaschenk.orientdb", "jeffaschenk.examples"} )
public class TestApplication {

    /**
     * Spring Boot Application
     * @param args Incoming Runtime Arguments, passed along to Spring Application Context.
     */
    public static void main(String[] args) {
        /**
         * Start the Application
         */
        SpringApplication.run(TestApplication.class, args);
    }



}
