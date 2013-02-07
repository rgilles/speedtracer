package org.speedtracer.thread;

import org.speedtracer.collect.TraceManager;
import org.speedtracer.perform.TracePerformer;

public class ServerTraceThreadBridgeProvider implements
        TraceThreadBridgeProvider {


    private final TraceManager traceManager;
    private final TracePerformer tracePerformer;

    public ServerTraceThreadBridgeProvider(TraceManager traceManager, TracePerformer tracePerformer) {
        this.traceManager = traceManager;
        this.tracePerformer = tracePerformer;
    }

    @Override
    public TraceThreadBridge getTraceThreadBridge() {
        return new ServerTraceThreadBridge(traceManager, tracePerformer);
    }
}
