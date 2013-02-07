package org.spring4speedtracer.perform;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.speedtracer.collect.Step;
import org.speedtracer.collect.TraceCollector;
import org.speedtracer.perform.ThreadLocalTracePerformer;
import org.speedtracer.perform.Interceptor;
import org.speedtracer.perform.TracePerformer;

/**
 * Abstract aspect for gathering trace metrics around Spring components.
 * Pointcuts to configure tracing must be provided by the extending class.
 *
 * @author Dustin
 */
public abstract class AbstractAspectInterceptor {

    private final TracePerformer tracePerformer;

    public AbstractAspectInterceptor(TracePerformer tracePerformer) {
        this.tracePerformer = tracePerformer;
    }

    /**
     * Pointcut determining what is traced, usually by combining other
     * pointcuts.
     */
    public abstract void traceable();

    /**
     * Advice that performs a trace around an invocation, by default using the
     * tracelabe() pointcut.
     *
     * @param pjp proceeding join point
     * @return the return value from the target, if any
     * @throws Throwable anything thrown by the target
     */
    @Around("traceable()")
    public Object trace(final ProceedingJoinPoint pjp) throws Throwable {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Step step() {
                return newStartStep(pjp);
            }

            @Override
            public Object proceed() throws Throwable {
                return pjp.proceed();
            }
        };
        return tracePerformer.trace(interceptor);
    }


    private Step newStartStep(ProceedingJoinPoint pjp) {
        return Step.newMethodStep(pjp.getSignature().getDeclaringTypeName()
                , pjp.getSignature().getDeclaringType().getSimpleName(), pjp.getSignature().getName());
    }
}