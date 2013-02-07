package org.speedtracer.log;

import org.slf4j.Logger;
import org.speedtracer.collect.ServerTrace;
import org.speedtracer.trace.FrameStack;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;

import static org.slf4j.LoggerFactory.getLogger;

public class Log4jCsvTraceLogger implements TraceLogger {

    private static final Logger LOG = getLogger(Log4jCsvTraceLogger.class);

    @Override
    public void log(ServerTrace trace, HttpServletRequest request)
            throws IOException {

        // Don't build if level is not enabled
        if (!LOG.isInfoEnabled()) {
            return;
        }

        // Build info
        StringBuilder sb = new StringBuilder();

        // Include trace ID
        append(sb, "traceId", trace.getId());

        // Include requestURI
        append(sb, "requestURI", request.getRequestURI());

        // Include headers
        Enumeration<?> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String headerName = (String) e.nextElement();
            String headerValue = request.getHeader(headerName);
            append(sb, headerName, headerValue.replace("\"", "'"));
        }

        // Build trace
        log(sb.toString(), trace.getTrace().getFrameStack());
    }

    protected void append(StringBuilder sb, String name, String value) {
        if (sb.length() > 0) {
            sb.append(",");
        }
        sb.append(name + "=\"" + value + "\"");
    }

    protected void append(StringBuilder sb, String name, long value) {
        if (sb.length() > 0) {
            sb.append(",");
        }
        sb.append(name + "=" + value);
    }

    protected void log(String prefix, FrameStack fs) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        append(sb, "label", fs.getOperation().getLabel());
        append(sb, "type", fs.getOperation().getType());
        append(sb, "class",
                fs.getOperation().getSourceCodeLocation() == null ? "" : fs
                        .getOperation().getSourceCodeLocation().getClassName());
        append(sb, "method",
                fs.getOperation().getSourceCodeLocation() == null ? "" : fs
                        .getOperation().getSourceCodeLocation().getMethodName());
        append(sb, "duration", fs.getRange().getDuration());
        append(sb, "self",
                fs.getRange().getDuration() - fs.getChildrenDuration());
        LOG.info(sb.toString());
        for (FrameStack child : fs.getChildren()) {
            log(prefix, child);
        }
    }
}
