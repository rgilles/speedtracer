package org.speedtracer.perform;

import org.speedtracer.collect.Step;

/**
 * Date: 1/28/13
 * Time: 9:52 AM
 * @author Romain Gilles
 */
public interface Interceptor {
    Step step();
    Object proceed() throws Throwable;
}
