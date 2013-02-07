package org.speedtracer.filter;

import org.slf4j.Logger;
import org.speedtracer.collect.ServerTrace;
import org.speedtracer.collect.TraceCollector;
import org.speedtracer.collect.TraceRepository;
import org.speedtracer.log.TraceLogger;
import org.speedtracer.log.voter.TraceLogVoter;
import org.speedtracer.perform.TracePerformer;
import org.speedtracer.trace.TraceHeader;
import org.speedtracer.write.TraceWriter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Romain Gilles
 * Date: 1/23/13
 * Time: 9:32 AM
 */
public abstract class AbstractSpeedTracerFilter implements Filter {
    public static final String REQUIRE_PROPERTY = "requireProperty";
    public static final String REQUIRE_HEADER = "requireHeader";
    public static final String MAX_TRACES = "maxTraces";
    public static final String LOG_TRACES = "logTraces";
    public static final String LOG_VOTING = "logVoting";
    public static final String CONFIG_DIR_PROPERTY = "configDirProperty";
    public static final String CONFIG_FILE = "configFile";
    public static final String TRACE_ENABLED_PROPERTY = "traceEnabled";
    public static final String TRACE_ENABLED_HEADER = "X-TraceEnabled";
//    public static final String TRACE_ID_HEADER = "X-TraceId";
    public static final String TRACE_URL_HEADER = "X-TraceUrl";
    public static final String TRACE_URL_PREFIX = "/appstats/";
//    public static final String TRACE_URL_PREFIX = "/speedtracer/";
    public static final String TRACE_URL_SUFFIX = "?type=json";
    protected static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
    protected static final String HEAD = "HEAD";
    private static final Logger LOG = getLogger(AbstractSpeedTracerFilter.class);
    protected ServletContext servletContext;
    protected TraceRepository traceRepository;
    protected TraceWriter traceWriter;
    protected TracePerformer tracePerformer;
    protected Iterable<TraceLogger> traceLoggers;
    protected Iterable<TraceLogVoter> traceLogVoters;
    protected boolean requireProperty;
    protected boolean requireHeader;
    protected boolean logTraces;
    protected boolean logVoting;
    protected String configDirProperty;
    protected String configFile;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        servletContext = filterConfig.getServletContext();

        tracePerformer = getTracePerformer();
        traceRepository = getTraceRepository();
        traceWriter = getTraceWriter();
        traceLoggers = getTraceLoggers();
        traceLogVoters = getTraceLogVoters();

        this.requireProperty = Boolean.valueOf(filterConfig
                .getInitParameter(REQUIRE_PROPERTY));

        LOG.info("Configured " + REQUIRE_PROPERTY + ": " + requireProperty);

        this.requireHeader = Boolean.valueOf(filterConfig
                .getInitParameter(REQUIRE_HEADER));

        LOG.info("Configured " + REQUIRE_HEADER + ": " + requireHeader);

        String maxTracesVal = filterConfig.getInitParameter(MAX_TRACES);
        if (maxTracesVal != null) {
            try {

                int maxTraces = Integer.valueOf(maxTracesVal);
                traceRepository.setMaxTraces(maxTraces);

                LOG.info("Configured " + MAX_TRACES + ": " + maxTraces);

            } catch (Exception e) {
                LOG.warn("Invalid " + MAX_TRACES + ": " + maxTracesVal
                        + ", ignoring");
            }
        }

        this.logTraces = Boolean.valueOf(filterConfig
                .getInitParameter(LOG_TRACES));

        LOG.info("Configured " + LOG_TRACES + ": " + logTraces);

        this.logVoting = Boolean.valueOf(filterConfig
                .getInitParameter(LOG_VOTING));

        LOG.info("Configured " + LOG_VOTING + ": " + logVoting);

        this.configDirProperty = filterConfig
                .getInitParameter(CONFIG_DIR_PROPERTY);

        LOG.info("Configured " + CONFIG_DIR_PROPERTY + ": " + configDirProperty);

        this.configFile = filterConfig.getInitParameter(CONFIG_FILE);

        LOG.info("Configured " + CONFIG_FILE + ": " + configFile);

        if (traceLogVoters != null) {
            for (TraceLogVoter voter : traceLogVoters) {
                voter.setConfigFile((configDirProperty != null ? System
                        .getProperty(configDirProperty) : "") + configFile);
            }
        }
    }

    protected abstract Iterable<TraceLogVoter> getTraceLogVoters();

    protected abstract Iterable<TraceLogger> getTraceLoggers();

    protected abstract TracePerformer getTracePerformer();

    protected abstract TraceRepository getTraceRepository();

    protected abstract TraceWriter getTraceWriter();

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Check for a trace request
        String url = request.getRequestURI();
        if (url.contains(TRACE_URL_PREFIX)) {

            LOG.debug("Retrieving: " + url);

            retrieveTrace(request, response);

        } else {

            if (isEnabled(request.getHeader(TRACE_ENABLED_HEADER))) {

                LOG.debug("Tracing: " + url);

                // Trace the request
                performTrace(request, response, filterChain);

            } else {

                // Proceed
                filterChain.doFilter(request, response);
            }
        }
    }

    /**
     * Retrieve an existing trace.
     *
     * @param request  the request
     * @param response the response
     * @throws java.io.IOException from the trace writer
     */
    protected void retrieveTrace(HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {

        // Retrieve the trace
        String url = request.getRequestURI();
        String traceId = url.substring(url.lastIndexOf("/") + 1);
        ServerTrace trace = traceRepository.getTrace(traceId);
        if (trace == null) {
            throw new IllegalArgumentException("Trace " + traceId
                    + " not found");
        }

        // Set headers
        response.setContentType(JSON_CONTENT_TYPE);
        addNoCacheHeaders(response);

        // Build the trace if necessary
        if (trace.getOutput() == null) {
            trace.setOutput(new ByteArrayOutputStream());
            traceWriter.write(trace, new OutputStreamWriter(trace.getOutput()));
        }

        if (request.getMethod().equals(HEAD)) {

            // Return trace length
            response.setContentLength(trace.getOutput().size());
        } else {

            // Return the trace
            trace.getOutput().writeTo(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    /**
     * Perform a trace around the current request.
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     * @throws java.io.IOException            from the chain
     * @throws javax.servlet.ServletException from the chain
     */
    protected void performTrace(HttpServletRequest request,
                                HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        boolean enabled = false;
        TraceHeader traceHeader = null;
        try {

            // Start trace
            String url = request.getRequestURI();
            traceHeader = traceRepository.beginTrace(request.getContextPath()
                    .substring(1), request.getContextPath(),
                    request.getContextPath() + TRACE_URL_PREFIX);

            // Set headers to inform Speed Tracer
//            response.setHeader(TRACE_ID_HEADER, traceHeader.getId());
//            response.setHeader(TRACE_URL_HEADER, traceHeader.getUrl()
//                    + TRACE_URL_SUFFIX);
            response.addHeader(TRACE_URL_HEADER,
                    request.getContextPath() + TRACE_URL_PREFIX + "details?time=" + traceHeader.getId() + "&type=json");
            // Begin top-level step for the request
            traceRepository
                    .beginStep(
                            request.getMethod()
                                    + " "
                                    + url
                                    + (request.getQueryString() != null ? ("?" + request
                                    .getQueryString()) : ""),
                            TraceCollector.StepType.HTTP);

            // Enable aspect tracing
            tracePerformer.setEnabled(true);
            enabled = true;
        } catch (Exception e) {
            LOG.error("Error beginning trace", e);
        }

        // Proceed
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (enabled) {
                try {

                    // Disable aspect tracing
                    tracePerformer.setEnabled(false);

                    // Complete top-level step
                    traceRepository.completeStep();

                    // Complete the trace and store it for retrieval
                    traceRepository.completeTrace();

                    // Log the trace if enabled
                    logTrace(traceHeader, request);
                } catch (Exception e) {
                    LOG.error("Error completing trace", e);
                }
            }
        }
    }

    /**
     * Log the trace if loggers are enabled and a voter approves.
     *
     * @param traceHeader the trace header
     * @param request     the request
     * @throws java.io.IOException
     */
    protected void logTrace(TraceHeader traceHeader, HttpServletRequest request)
            throws IOException {

        // Check for loggers
        if (isTraceLoggingEnable()) {

            // Get the trace
            ServerTrace trace = traceRepository.getTrace(traceHeader.getId());

            // Check for voters

            if (isTraceToLog(trace)) {
                // Log to all loggers
                for (TraceLogger traceLogger : traceLoggers) {
                    long start = System.currentTimeMillis();
                    traceLogger.log(trace, request);
                    LOG.debug("Logged in "
                            + (System.currentTimeMillis() - start) + "ms");
                }
            }
        }
    }

    private boolean isTraceToLog(ServerTrace trace) {
        boolean log = false;
        if (isLogVotingEnable()) {
            // Log if any voter approves
            for (TraceLogVoter voter : traceLogVoters) {
                long start = System.currentTimeMillis();
                log = voter.shouldLog(trace);
                LOG.debug("Checked in "
                        + (System.currentTimeMillis() - start) + "ms");
                if (log) {
                    LOG.debug("Vote for logging");
                    break;
                }
            }
        } else {
            // Always log by default
            LOG.debug("Logging by default");
            log = true;
        }
        return log;
    }

    private boolean isTraceLoggingEnable() {
        return logTraces && hasTraceLoggers();
    }

    private boolean isLogVotingEnable() {
        return logVoting && hasTraceLogVoters();
    }

    private boolean hasTraceLogVoters() {
        return traceLogVoters != null && traceLogVoters.iterator().hasNext();
    }

    private boolean hasTraceLoggers() {
        return traceLoggers != null && traceLoggers.iterator().hasNext();
    }

    /**
     * Determine if trace is enabled using Java property and/or header if
     * required
     *
     * @param header the trace header
     * @return true if enabled
     */
    protected boolean isEnabled(String header) {
        //TODO review complexity
        boolean enabled = (!requireProperty || Boolean.valueOf(System
                .getProperty(TRACE_ENABLED_PROPERTY)))
                && (!requireHeader || (header!= null && header.trim().length() > 0));

        LOG.debug("Trace enable: {}", enabled);

        return enabled;
    }

    /**
     * Add no-cache headers to an HTTP response.
     *
     * @param response the response
     */
    protected void addNoCacheHeaders(HttpServletResponse response) {

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
        response.setDateHeader("Expires", 0);
        response.setDateHeader("Date", System.currentTimeMillis());
    }
}
