package org.speedtracer.guice;

import org.slf4j.Logger;
import org.speedtracer.annotation.Traceable;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * TODO comment
 * Date: 1/29/13
 * Time: 9:26 AM
 *
 * @author Romain Gilles
 */
@Traceable
public class TraceableService {
    private static final Logger LOG = getLogger(TraceableService.class);

    public void traceableMethod() {
        LOG.info("traceable method");
    }

    protected void notTraceableProtectedMethod() {
        LOG.info("not traceable protected method");
    }

    void notTraceableDefaultMethod() {
        LOG.info("not traceable default method");
    }

    private void notTraceablePrivateMethod() {
        throw new UnsupportedOperationException("Private method must not be call");
    }
}
