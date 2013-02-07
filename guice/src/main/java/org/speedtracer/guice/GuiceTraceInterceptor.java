package org.speedtracer.guice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.speedtracer.collect.Step;
import org.speedtracer.perform.Interceptor;
import org.speedtracer.perform.TracePerformer;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * TODO comment
 * Date: 1/29/13
 * Time: 5:49 AM
 *
 * @author Romain Gilles
 */
public class GuiceTraceInterceptor implements MethodInterceptor {
    private final Provider<TracePerformer> tracePerformer;

    @Inject
    public GuiceTraceInterceptor(Provider<TracePerformer> tracePerformer) {
        this.tracePerformer = tracePerformer;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {

        return tracePerformer.get().trace(new Interceptor() {
            @Override
            public Step step() {
                return Step.newStepOn(invocation.getMethod());
            }

            @Override
            public Object proceed() throws Throwable {
                return invocation.proceed();
            }
        });
    }
}
