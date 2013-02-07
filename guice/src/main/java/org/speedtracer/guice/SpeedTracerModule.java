package org.speedtracer.guice;

import com.google.inject.AbstractModule;
import org.speedtracer.collect.ServerTraceRepository;
import org.speedtracer.collect.TraceCollector;
import org.speedtracer.collect.TraceRepository;
import org.speedtracer.perform.ThreadLocalTracePerformer;
import org.speedtracer.perform.TracePerformer;
import org.speedtracer.write.JsonTraceWriter;
import org.speedtracer.write.TraceWriter;

/**
* TODO comment
* Date: 2/5/13
* Time: 11:12 PM
*
* @author Romain Gilles
*/
public class SpeedTracerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TraceWriter.class).to(JsonTraceWriter.class);
        bind(TraceRepository.class).to(ServerTraceRepository.class);
        bind(TraceCollector.class).to(ServerTraceRepository.class);
        bind(TracePerformer.class).to(ThreadLocalTracePerformer.class);
        Interceptors.bindInterceptorOnTraceablePublicMethods(binder());
    }
}
