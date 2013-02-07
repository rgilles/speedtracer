package org.spring4speedtracer.filter;

import java.util.Collection;

import org.speedtracer.collect.TraceRepository;
import org.speedtracer.filter.AbstractSpeedTracerFilter;
import org.speedtracer.log.TraceLogger;
import org.speedtracer.log.voter.TraceLogVoter;
import org.speedtracer.perform.TracePerformer;
import org.speedtracer.write.TraceWriter;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Filter that enables logging Spring performance metrics and publishing them to
 * Speed Tracer.
 */
public class SpeedTracerSpringFilter extends AbstractSpeedTracerFilter {

    @Override
    protected Collection<TraceLogVoter> getTraceLogVoters() {
        return getBeans(TraceLogVoter.class);
    }

    @Override
    protected Collection<TraceLogger> getTraceLoggers() {
        return getBeans(TraceLogger.class);
    }

    @Override
    protected TracePerformer getTracePerformer() {
        return getBean("tracePerformer");
    }

    @Override
    protected TraceRepository getTraceRepository() {
        return getBean("traceRepository");
    }

    @Override
    protected TraceWriter getTraceWriter() {
        return getBean("traceWriter");
    }

    /**
     * Get a Spring bean from the current web application context.
     *
     * @param <T>  bean type
     * @param name bean name
     * @return the bean
     */
    @SuppressWarnings("unchecked")
    protected <T> T getBean(String name) {

        Object bean = WebApplicationContextUtils.getWebApplicationContext(
                servletContext).getBean(name);
        if (bean == null) {
            throw new IllegalArgumentException("Bean not found: " + name);
        }

        return (T) bean;
    }

    /**
     * Get Spring beans from the current web application context.
     *
     * @param <T>   bean type
     * @param clazz bean class
     * @return the beans
     */
    protected <T> Collection<T> getBeans(Class<T> clazz) {

        return WebApplicationContextUtils
                .getWebApplicationContext(servletContext).getBeansOfType(clazz)
                .values();
    }
}
