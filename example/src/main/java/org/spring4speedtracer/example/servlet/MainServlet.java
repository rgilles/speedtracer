package org.spring4speedtracer.example.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.spring4speedtracer.example.service.MainService;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Example servlet that calls MainService to echo the content of a request
 * parameter.
 * TODO test traceable annotation on it ;)
 * @author Dustin
 * @author Romain Gilles
 * 
 */
@SuppressWarnings("serial")
@Singleton
public class MainServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(MainServlet.class);

	private static final String MAIN_SERVICE = "mainService";
	private static final String ARG = "arg";
    private final MainService mainService;

    @Inject
    public MainServlet(MainService mainService) {
        this.mainService = mainService;
    }

    @Override
	public void init() {

		System.setProperty("traceEnabled",
				Boolean.TRUE.toString());
		System.setProperty("resources.dir", "src/example/resources");

		LOG.debug("MainServlet loaded, test by accessing a URL like http://localhost:9080/example/main?arg=test");
	}

	public String getResponse(String arg) {

		MainService mainService = getMainService();

		mainService.fastMethod();

		return mainService.slowMethod(arg);
	}

	protected MainService getMainService() {
        return mainService;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.getWriter().write(getResponse(req.getParameter(ARG)));
	}
}
