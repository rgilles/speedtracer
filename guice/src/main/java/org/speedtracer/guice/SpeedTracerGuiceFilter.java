package org.speedtracer.guice;


import org.speedtracer.collect.TraceRepository;
import org.speedtracer.filter.AbstractSpeedTracerFilter;
import org.speedtracer.log.TraceLogger;
import org.speedtracer.log.voter.TraceLogVoter;
import org.speedtracer.perform.TracePerformer;
import org.speedtracer.write.TraceWriter;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import static java.util.Collections.emptyList;

/**
 * TODO comment
 * Date: 2/1/13
 * Time: 11:02 PM
 *
 * @author Romain Gilles
 */
@Singleton
public class SpeedTracerGuiceFilter extends AbstractSpeedTracerFilter {

    private final TracePerformer tracePerformer;
    private final TraceRepository traceRepository;
    private final TraceWriter traceWriter;
    private final Iterable<TraceLogVoter> traceLogVoters;
    private final Iterable<TraceLogger> traceLoggers;

    @Inject
    public SpeedTracerGuiceFilter(TracePerformer tracePerformer
            , TraceRepository traceRepository
            , TraceWriter traceWriter
            , @Nullable Iterable<TraceLogVoter> traceLogVoters
            , @Nullable Iterable<TraceLogger> traceLoggers) {
        this.tracePerformer = tracePerformer;
        this.traceRepository = traceRepository;
        this.traceWriter = traceWriter;
//        this.traceLogVoters = fixNullable(null);
//        this.traceLoggers = fixNullable(null);
        this.traceLogVoters = fixNullable(traceLogVoters);
        this.traceLoggers = fixNullable(traceLoggers);
    }

    private <T> Iterable<T> fixNullable(Iterable<T> values) {
        if (values != null) {
            return values;
        } else {
            return emptyList();
        }
    }

    @Override
    protected Iterable<TraceLogVoter> getTraceLogVoters() {
        return traceLogVoters;
    }

    @Override
    protected Iterable<TraceLogger> getTraceLoggers() {
        return traceLoggers;
    }

    @Override
    protected TracePerformer getTracePerformer() {
        return tracePerformer;
    }

    @Override
    protected TraceRepository getTraceRepository() {
        return traceRepository;
    }

    @Override
    protected TraceWriter getTraceWriter() {
        return traceWriter;
    }
}
