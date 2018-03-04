package CreepyCrawler;

import CreepyCrawler.ui.MainView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by i on 18.02.2018.
 */
@SpringBootApplication
public class StartingPoint {
    public static void main(String[] args) {
        SpringApplication.run(StartingPoint.class, args);
    }
}

@RestController
class HomeController {

    @RequestMapping("/")
    public void test() {
        MainView mainView = new MainView();
        mainView.init(null);
    }
}