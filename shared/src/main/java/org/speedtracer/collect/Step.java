package org.speedtracer.collect;

import java.lang.reflect.Method;

import static org.speedtracer.collect.TraceCollector.StepType;
import static org.speedtracer.collect.TraceCollector.StepType.METHOD;

/**
 * Created with IntelliJ IDEA.
 * User: rogilles
 * Date: 1/28/13
 * Time: 9:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class Step {
    public final String label;
    public final StepType type;
    public final String className;
    public final String methodName;

    Step(String label, StepType type, String className, String methodName) {
        this.label = label;
        this.type = type;
        this.className = className;
        this.methodName = methodName;
    }

    public static Step newStep(String label, StepType type, String className, String methodName) {
        return new Step(label, type, className, methodName);
    }

    public static Step newStepOn(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        return newMethodStep(declaringClass.getName(), declaringClass.getSimpleName(), method.getName());
    }

    public static Step newMethodStep(String declaringClassName, String declaringClassSimpleName, String methodName) {
        String label = declaringClassSimpleName + "." + methodName;
        return newStep(label, METHOD, declaringClassName, methodName);
    }


}
