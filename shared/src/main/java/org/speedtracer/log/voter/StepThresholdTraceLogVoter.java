package org.speedtracer.log.voter;

import org.slf4j.Logger;
import org.speedtracer.collect.ServerTrace;
import org.speedtracer.trace.FrameStack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

public class StepThresholdTraceLogVoter implements TraceLogVoter {

    private static final Logger LOG = getLogger(StepThresholdTraceLogVoter.class);

    protected static final String THRESHOLD = "threshold.";
    protected static final String ALL = "*";

    protected String configFile;

    @Override
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    @Override
    public boolean shouldLog(ServerTrace trace) {

        // Load thresholds from config
        Map<String, Long> thresholds = new HashMap<>();
        getThresholds(thresholds);

        if (thresholds.isEmpty()) {
            // If no thresholds log everything
            return true;
        }

        // Parse durations from trace
        Map<String, Long> durations = new HashMap<>();
        getDurations(durations, trace.getTrace().getFrameStack());

        // Determine if any duration exceeds its threshold
        boolean found = false;
        for (Entry<String, Long> entry : durations.entrySet()) {
            if (thresholds.keySet().contains(entry.getKey())) {
                // Check specific threshold
                if (thresholds.get(entry.getKey()) < entry.getValue()) {
                    return true;
                } else {
                    found = true;
                }
            }
        }

        if (!found) {
            // Check total threshold
            if (thresholds.keySet().contains(ALL)) {
                if (thresholds.get(ALL) < trace.getTrace().getRange()
                        .getDuration()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void getDurations(Map<String, Long> durations, FrameStack frameStack) {
        Long duration = durations.get(frameStack.getOperation().getLabel());
        if (duration == null || duration < frameStack.getRange().getDuration()) {
            duration = frameStack.getRange().getDuration();
            durations.put(frameStack.getOperation().getLabel(), duration);
        }
        for (FrameStack child : frameStack.getChildren()) {
            getDurations(durations, child);
        }
    }

    protected void getThresholds(Map<String, Long> params) {
        try {
            Properties props = new Properties();
            FileInputStream in = new FileInputStream(configFile);
            props.load(in);
            in.close();
            for (Object key : props.keySet()) {
                String k = (String) key;
                if (k.startsWith(THRESHOLD)) {
                    LOG.debug("Found threshold: " + key + "=" + props.get(key));
                    params.put(k.substring(THRESHOLD.length()),
                            Long.valueOf((String) props.get(key)));
                }
            }
        } catch (FileNotFoundException fnfe) {
            // Ignore
        } catch (Exception e) {
            LOG.error("Error loading thresholds", e);
        }
    }
}
