package org.spring4speedtracer.test;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.spring4speedtracer.example.service.MainService;
import org.spring4speedtracer.example.servlet.MainServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Basic validation test for the example servlet and services.
 * 
 * @author Dustin
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testContext.xml" })
public class ExampleTest {

	@Autowired
	private MainService mainService;

	@SuppressWarnings("serial")
	@Test
	public void testServlet() {

		// Replace servlet's service with local version
		MainServlet mainServlet = new MainServlet(mainService) {
			@Override
			protected MainService getMainService() {
				return mainService;
			}
		};

		// Test main method
		Assert.assertEquals("test", mainServlet.getResponse("test"));
	}
}
