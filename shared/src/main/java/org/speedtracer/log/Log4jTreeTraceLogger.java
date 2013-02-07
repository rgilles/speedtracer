package org.speedtracer.log;

import org.slf4j.Logger;
import org.speedtracer.collect.ServerTrace;
import org.speedtracer.trace.FrameStack;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Level;

import static org.slf4j.LoggerFactory.getLogger;

public class Log4jTreeTraceLogger implements TraceLogger {

    private static final Logger LOG = getLogger(Log4jTreeTraceLogger.class);

    @Override
    public void log(ServerTrace trace, HttpServletRequest request)
            throws IOException {

        // Don't build if level is not enabled
        if (!LOG.isInfoEnabled()) {
            return;
        }

        // Build info
        StringBuilder sb = new StringBuilder();

        if (request != null) {

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
        }

        // Build trace
        String info = sb.toString();
        sb = new StringBuilder();
        sb.append(trace.getTrace().getFrameStack().getOperation().getLabel()
                + " "
                + trace.getTrace().getFrameStack().getRange().getDuration()
                + "ms\n------------------------------\n");
        log(sb, 0, trace.getTrace().getFrameStack());
        sb.append("------------------------------\n" + info + "\n");

        // Log trace
        LOG.info(sb.toString());
    }

    protected void append(StringBuilder sb, String name, String value) {
        if (sb.length() > 0) {
            sb.append(",");
        }
        sb.append(name + "=\"" + value + "\"");
    }

    protected void log(StringBuilder sb, int depth, FrameStack fs) {
        for (int i = 0; i < depth; i++) {
            sb.append(" ");
        }
        sb.append(fs.getOperation().getLabel() + " "
                + fs.getRange().getDuration() + "ms (self "
                + (fs.getRange().getDuration() - fs.getChildrenDuration())
                + "ms)\n");
        for (FrameStack child : fs.getChildren()) {
            log(sb, depth + 1, child);
        }
    }
}
