package org.speedtracer.collect;

import org.speedtracer.trace.TraceHeader;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Trace collector and repository using basic ThreadLocal storage.
 * 
 * @author Dustin
 * 
 */
@Singleton
public class ServerTraceRepository implements TraceRepository, TraceCollector,
		TraceManager {

	protected int maxTraces = 100;
	protected ThreadLocal<ServerTrace> activeTraces = new ThreadLocal<ServerTrace>();
	protected Map<String, ServerTrace> allTraces = Collections
			.synchronizedMap(new LinkedHashMap<String, ServerTrace>());

	@Override
	public void setMaxTraces(int maxTraces) {
		this.maxTraces = maxTraces;
	}

	@Override
	public TraceHeader beginTrace(String name, String formattedName,
			String urlPrefix) {
		ServerTrace trace = new ServerTrace(name, formattedName, urlPrefix);
		activeTraces.set(trace);
		return trace.getTrace();
	}

	@Override
	public void completeTrace() {
		ServerTrace trace = activeTraces.get();
		activeTraces.remove();
		allTraces.put(trace.getId(), trace);
		synchronized (allTraces) {
			while (!allTraces.isEmpty() && allTraces.size() > maxTraces) {
				// Remove the first element
				allTraces.remove(allTraces.keySet().iterator().next());
			}
		}
	}

	@Override
	public ServerTrace getTrace(String id) {
		return allTraces.get(id);
	}

	@Override
	public Map<String, ServerTrace> getTraces() {
		return Collections.unmodifiableMap(allTraces);
	}

	@Override
	public void beginStep(String label, StepType type) {
		beginStep(label, type, null, null, 0);
	}

    @Override
    public void beginStep(Step step) {
        beginStep(step.label,step.type,step.className, step.methodName, 0);
    }

    @Override
	public synchronized void beginStep(String label, StepType type,
			String className, String methodName, long lineNumber) {
		ServerTrace trace = activeTraces.get();
		if (trace != null) {
			trace.push(label, type, className, methodName, lineNumber);
		}
	}

	@Override
	public synchronized void completeStep() {
		ServerTrace trace = activeTraces.get();
		if (trace != null) {
			trace.pop();
		}
	}

	@Override
	public ServerTrace getCurrentTrace() {
		return activeTraces.get();
	}

	@Override
	public void setCurrentTrace(ServerTrace trace) {
		activeTraces.set(trace);
	}

	@Override
	public void clearCurrentTrace() {
		activeTraces.remove();
	}
}
