package org.speedtracer.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import org.speedtracer.annotation.Traceable;
import org.speedtracer.perform.TracePerformer;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static com.google.inject.matcher.Matchers.annotatedWith;

/**
 * TODO comment
 * Date: 2/1/13
 * Time: 9:28 PM
 *
 * @author Romain Gilles
 */
public final class Interceptors {

    public static void bindInterceptorOnTraceablePublicMethods(Binder binder) {
        binder.bindInterceptor(getTraceableClassMatcher(), getPublicMethod(), new GuiceTraceInterceptor(binder.getProvider(TracePerformer.class)));
    }

    private static AbstractMatcher<Method> getPublicMethod() {
        return new PublicMethodMatcher();
    }

    public static Matcher<AnnotatedElement> getTraceableClassMatcher() {
        return annotatedWith(Traceable.class);
    }

    public static class PublicMethodMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method method) {
            return Modifier.isPublic(method.getModifiers());
        }
    }
}
