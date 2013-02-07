package org.speedtracer.trace;

import java.io.Serializable;

/**
 * Speed Tracer range concept (JSON-ready).
 * 
 * @author Dustin
 * 
 */
@SuppressWarnings("serial")
public class Range implements Serializable {

	private long start;
	private long startNanos;
	private long end;
	private long endNanos;
	private long duration;
	private long durationNanos;

	public Range() {
		this.start = System.currentTimeMillis();
		this.startNanos = System.nanoTime();
	}

	public void finish() {
		end = System.currentTimeMillis();
		endNanos = System.nanoTime();
		duration = end - start;
		durationNanos = endNanos - startNanos;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getStartNanos() {
		return startNanos;
	}

	public void setStartNanos(long startNanos) {
		this.startNanos = startNanos;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getEndNanos() {
		return endNanos;
	}

	public void setEndNanos(long endNanos) {
		this.endNanos = endNanos;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public long getDurationNanos() {
		return durationNanos;
	}

	public void setDurationNanos(long durationNanos) {
		this.durationNanos = durationNanos;
	}
}
