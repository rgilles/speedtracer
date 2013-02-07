package org.spring4speedtracer.perform;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.speedtracer.perform.TracePerformer;

/**
 * Trace aspect implementation that provides pointcuts for many typical Spring
 * components.
 * 
 * @author Dustin
 * 
 */
@Aspect
public class DefaultAspectInterceptor extends AbstractAspectInterceptor {

    protected DefaultAspectInterceptor(TracePerformer tracePerformer) {
        super(tracePerformer);
    }

    @Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void serviceAnnotatedClass() {
	}

	@Pointcut("within(@org.springframework.stereotype.Repository *)")
	public void repositoryAnnotatedClass() {
	}

	@Override
	@Pointcut("publicMethod() && (serviceAnnotatedClass() || repositoryAnnotatedClass())")
	public void traceable() {
	}
}
