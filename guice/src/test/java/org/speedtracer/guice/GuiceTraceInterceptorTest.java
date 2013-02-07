package org.speedtracer.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.speedtracer.perform.Interceptor;
import org.speedtracer.perform.TracePerformer;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.speedtracer.guice.Interceptors.bindInterceptorOnTraceablePublicMethods;

/**
 * TODO comment
 * Date: 1/29/13
 * Time: 9:08 AM
 *
 * @author Romain Gilles
 */

public class GuiceTraceInterceptorTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testInvokeTraceAble() throws Exception {
        TraceableService traceableService = Guice.createInjector(new TraceableAnnotationModule()).getInstance(TraceableService.class);
        traceableService.traceableMethod();
        assertThat(MyTracePerformer.traced, is(true));
    }

    @Test
    public void testInvokeNotTraceableDefault() throws Exception {
        TraceableService traceableService = Guice.createInjector(new TraceableAnnotationModule()).getInstance(TraceableService.class);
        traceableService.notTraceableDefaultMethod();
        assertThat(MyTracePerformer.traced, is(false));
    }

    @Test
    public void testInvokeNotTraceableProtected() throws Exception {
        TraceableService traceableService = Guice.createInjector(new TraceableAnnotationModule()).getInstance(TraceableService.class);
        traceableService.notTraceableProtectedMethod();
        assertThat(MyTracePerformer.traced, is(false));
    }

    private class TraceableAnnotationModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(TracePerformer.class).toInstance(new MyTracePerformer());
            bindInterceptorOnTraceablePublicMethods(binder());
        }

    }
    private static class MyTracePerformer implements TracePerformer {
        public static boolean traced = false;
        @Override
        public void setEnabled(boolean enabled) {

        }

        @Override
        public Object trace(Interceptor interceptor) throws Throwable {
            traced = true;
            return interceptor.proceed();
        }
    }
}
