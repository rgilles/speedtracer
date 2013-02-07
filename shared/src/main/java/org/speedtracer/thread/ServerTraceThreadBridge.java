package org.speedtracer.thread;

import org.speedtracer.collect.ServerTrace;
import org.speedtracer.collect.TraceCollector.StepType;
import org.speedtracer.collect.TraceManager;
import org.speedtracer.perform.TracePerformer;

public class ServerTraceThreadBridge implements TraceThreadBridge {

    private final TraceManager traceManager;

    private final TracePerformer tracePerformer;

    private final ServerTrace master;

    public ServerTraceThreadBridge(TraceManager traceManager,
                                   TracePerformer tracePerformer) {
        this.traceManager = traceManager;
        this.tracePerformer = tracePerformer;
        master = traceManager.getCurrentTrace();
        if (master != null) {
            master.anchor();
        }
    }

    @Override
    public void addThread(String name) {
        if (master != null) {
            master.setThreadName(name);
            traceManager.setCurrentTrace(master);
            tracePerformer.setEnabled(true);
            traceManager.beginStep("Thread-" + name, StepType.THREAD);
        }
    }

    @Override
    public void removeThread() {
        if (master != null) {
            traceManager.completeStep();
            traceManager.clearCurrentTrace();
            tracePerformer.setEnabled(false);
        }
    }
}
