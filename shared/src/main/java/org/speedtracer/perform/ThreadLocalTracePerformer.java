package org.speedtracer.perform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speedtracer.collect.Step;
import org.speedtracer.collect.TraceCollector;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TODO introduce state pattern on enable
 * Date: 1/29/13
 * Time: 5:39 AM
 *
 * @author Romain Gilles
 */
@Singleton
public class ThreadLocalTracePerformer implements TracePerformer {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadLocalTracePerformer.class);
    protected final TraceCollector traceCollector;
    protected final ThreadLocal<Boolean> enabled = new ThreadLocal<>();

    @Inject
    public ThreadLocalTracePerformer(TraceCollector traceCollector) {
        this.traceCollector = traceCollector;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    @Override
    public Object trace(Interceptor interceptor) throws Throwable {
        if (Boolean.TRUE.equals(enabled.get())) {
            begin(interceptor.step());
            try {
                return interceptor.proceed();
            } finally {
                complete();
            }
        } else {
            return interceptor.proceed();
        }
    }

    protected void complete() {
        try {
            traceCollector.completeStep();
        } catch (Throwable t) {
            LOG.error("Complete step failed", t);
        }
    }

    protected void begin(Step step) {
        try {
            traceCollector.beginStep(step);
        } catch (Throwable t) {
            LOG.error("Begin step failed", t);
        }
    }
}
