package org.spring4speedtracer.example.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Providers;
import org.speedtracer.guice.SpeedTracerGuiceFilter;
import org.speedtracer.guice.SpeedTracerModule;
import org.speedtracer.log.TraceLogger;
import org.speedtracer.log.voter.TraceLogVoter;
import org.spring4speedtracer.example.service.MainService;
import org.spring4speedtracer.example.service.MainServiceImpl;
import org.spring4speedtracer.example.service.child.FastService;
import org.spring4speedtracer.example.service.child.FastServiceImpl;
import org.spring4speedtracer.example.service.child.SlowService;
import org.spring4speedtracer.example.service.child.SlowServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO comment
 * Date: 2/4/13
 * Time: 11:43 AM
 *
 * @author Romain Gilles
 */
public class MainServletContextListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets() {
                install(new SpeedTracerModule());
                //create missing binding for speedtracer filter (extension point)
                bind(new TypeLiteral<Iterable<TraceLogVoter>>(){}).toProvider(Providers.<Iterable<TraceLogVoter>>of(null));
                bind(new TypeLiteral<Iterable<TraceLogger>>(){}).toProvider(Providers.<Iterable<TraceLogger>>of(null));
                //create web application specific bindings
                bind(MainService.class).to(MainServiceImpl.class);
                bind(FastService.class).to(FastServiceImpl.class);
                bind(SlowService.class).to(SlowServiceImpl.class);

                Map < String, String > params = new HashMap<>();
                params.put("requireProperty", String.valueOf(true));
                params.put("requireHeader", String.valueOf(false));
                params.put("maxTraces", String.valueOf(100));
                params.put("logTraces", String.valueOf(true));
                params.put("logVoting", String.valueOf(true));
                params.put("configDirProperty", "resources.dir");
                params.put("configFile", "/config/trace.properties");
                filter("/*").through(SpeedTracerGuiceFilter.class, params);
                serve("/main").with(MainServlet.class);
            }
        });
    }
}
