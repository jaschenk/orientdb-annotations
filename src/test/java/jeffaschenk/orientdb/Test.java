package jeffaschenk.orientdb;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: jaschenk
 * Date: 11/5/16
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestApplication.class},
        initializers = ConfigFileApplicationContextInitializer.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test {

    @Autowired
    private DataAccessFactory dataAccessFactory;

    @org.junit.Test
    public void test01() {
          assertNotNull(dataAccessFactory);
    }

}
