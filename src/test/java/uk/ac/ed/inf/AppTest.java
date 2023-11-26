package uk.ac.ed.inf;

import junit.framework.TestCase;

import java.time.LocalDate;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
        public void testAppWorks()
        {
            String url = "https://ilp-rest.azurewebsites.net/";
            try {
                App.main(new String[]{url, LocalDate.now().toString()});
            } catch (Exception e) {
                fail("App failed to run");
            }
        }

//        public void testAppFailsWithWrongUrl()
//        {
//            String url = "https//url.com";
//            try {
//                App.main(new String[]{url, LocalDate.now().toString()});
//            } catch (Exception e) {
//                fail("App created an Exception");
//            }
//        }
//
//        public void testAppFailsWithWrongDate()
//        {
//            String url = "https//url.com";
//            try {
//                App.main(new String[]{url, "A"});
//            } catch (Exception e) {
//                fail("App created an Exception");
//            }
//        }

//        public void testAppFailsWithWrongNumberOfArguments()
//        {
//            String url = "https//url.com";
//            try {
//                App.main(new String[]{url});
//            } catch (Exception e) {
//                fail("App created an Exception");
//            }
//        }
}
