package uk.ac.ed.inf;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ed.inf.Flight.FlightDataHandlerTest;

import java.time.LocalDate;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(AppTest.class);
        suite.addTestSuite(LngLatHandlerTest.class);
        suite.addTestSuite(OrderValidatorTest.class);
        suite.addTestSuite(FlightDataHandlerTest.class);
        return suite;
    }

    /**
     * Rigourous Test :-)
     */
    public void testAppDoesNotCrash() {
        String url = "https://ilp-rest.azurewebsites.net";
        String date = LocalDate.now().toString();

        try {
            App.main(new String[]{url, date});
        } catch (Exception e) {
            fail("App crashed");
        }
    }
}
