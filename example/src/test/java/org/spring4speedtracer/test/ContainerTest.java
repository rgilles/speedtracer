package org.spring4speedtracer.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.speedtracer.filter.AbstractSpeedTracerFilter;

/**
 * Full stack test using an example war deployed to Jetty with tracing enabled.
 * 
 * @author Dustin
 * 
 */
public class ContainerTest {

	private static int PORT = 10080;
	private static String LOCALHOST = "http://localhost:" + PORT;

	private static Server server;

	@BeforeClass
	public static void beforeClass() throws Exception {

		// Create a Jetty server
		server = new Server(PORT);

		// Deploy war
		WebAppContext context = new WebAppContext("build/dist/example.war",
				"/example");
		server.setHandler(context);

		// Start server
		server.start();
	}

	@AfterClass
	public static void afterClass() throws Exception {

		// Stop server
		if (server.isRunning()) {
			server.stop();
		}
	}

	@Test
	public void testDisabled() throws Exception {

		// Disable tracing
		System.clearProperty(AbstractSpeedTracerFilter.TRACE_ENABLED_PROPERTY);

		// Send a get request to the servlet URL
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(LOCALHOST + "/example/main?arg=val");
		int resp = httpClient.executeMethod(get);

		// Check for success and return value
		Assert.assertEquals(200, resp);
		Assert.assertEquals("val", get.getResponseBodyAsString());

		// Make sure trace was disabled
//		Assert.assertEquals(null, get
//				.getResponseHeader(AbstractSpeedTracerFilter.TRACE_ID_HEADER));
		Assert.assertEquals(null, get
				.getResponseHeader(AbstractSpeedTracerFilter.TRACE_URL_HEADER));
	}

	@Test
	public void testEnabled() throws Exception {

		// Enable tracing
		System.setProperty(AbstractSpeedTracerFilter.TRACE_ENABLED_PROPERTY,
				Boolean.TRUE.toString());

		// Send a get request to the servlet URL
		HttpClient httpClient = new HttpClient();
		GetMethod get = new GetMethod(LOCALHOST + "/example/main?arg=val");
		int resp = httpClient.executeMethod(get);

		// Check for success and return value
		Assert.assertEquals(200, resp);
		Assert.assertEquals("val", get.getResponseBodyAsString());

		// Check for trace info
//		String traceId = get.getResponseHeader(
//				AbstractSpeedTracerFilter.TRACE_ID_HEADER).getValue();
//		Assert.assertNotNull(traceId);
		String traceUrl = get.getResponseHeader(
				AbstractSpeedTracerFilter.TRACE_URL_HEADER).getValue();
		Assert.assertNotNull(traceUrl);
//		Assert.assertTrue(traceUrl.contains(traceId));

		// Request trace
		get = new GetMethod(LOCALHOST + traceUrl);
		resp = httpClient.executeMethod(get);

		// Verify trace
		Assert.assertEquals(200, resp);
		String trace = get.getResponseBodyAsString();
		Assert.assertNotNull(trace);
//		Assert.assertTrue(trace.startsWith("{\"trace\":{\"id\":\"" + traceId
//				+ "\""));
	}
}
