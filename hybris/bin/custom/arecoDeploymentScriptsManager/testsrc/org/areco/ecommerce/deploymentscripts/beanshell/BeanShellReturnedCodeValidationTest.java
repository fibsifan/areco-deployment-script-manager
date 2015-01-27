package org.areco.ecommerce.deploymentscripts.beanshell;

import de.hybris.bootstrap.annotations.UnitTest;
import org.areco.ecommerce.deploymentscripts.beanshell.impl.DefaultBeanShellService;
import org.junit.Assert;
import org.junit.Test;

/**
 * It checks if the beanshell service handles the returned codes from the script correctly.
 *
 * Created by arobirosa on 27.01.15.
 */
@UnitTest
public class BeanShellReturnedCodeValidationTest {

        //We don't have any mocks to inject.
        private BeanShellService defaultBeanShellService = new DefaultBeanShellService();

        @Test
        public void testCorrectReturnedValue() throws BeanShellExecutionException {
                defaultBeanShellService.executeScript("return \"OK\";");
        }

        @Test
        public void testIncorrectReturnedValue() {
                try {
                        defaultBeanShellService.executeScript("return \"Error99\";");
                } catch (BeanShellExecutionException e) {
                        Assert.assertTrue("The message of the exception must contain the returned value: "
                                + e.getMessage(), e.getMessage().contains("Error99"));
                        return;git
                }
                Assert.fail("An exception must have been thrown.");
        }
}
