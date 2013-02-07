package org.spring4speedtracer.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.speedtracer.collect.ServerTrace;
import org.speedtracer.collect.TraceCollector.StepType;
import org.speedtracer.collect.TraceRepository;
import org.speedtracer.log.Log4jTreeTraceLogger;
import org.speedtracer.log.TraceLogger;
import org.speedtracer.log.voter.StepThresholdTraceLogVoter;
import org.speedtracer.log.voter.TraceLogVoter;
import org.speedtracer.perform.TracePerformer;
import org.speedtracer.thread.TraceThreadBridge;
import org.speedtracer.thread.TraceThreadBridgeProvider;
import org.speedtracer.trace.TraceHeader;
import org.speedtracer.write.JsonTraceWriter;
import org.spring4speedtracer.example.service.MainService;
import org.spring4speedtracer.filter.SpeedTracerSpringFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;

/**
 * Test for core trace functionality using AspectJ proxies.
 *
 * @author Dustin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testContext.xml")
public class TraceTest {

    private static final String CONFIG1 = "src/example/resources/config/trace.properties";
    private static final String CONFIG2 = CONFIG1 + ".2";
    private static final String CONFIG3 = CONFIG1 + ".3";
    private static final String CONFIG4 = CONFIG1 + ".4";
    private static final String CONFIG5 = CONFIG1 + ".5";
    private static final String CONFIG6 = CONFIG1 + ".6";

    @Autowired
    private MainService mainService;

    @Autowired
    private TracePerformer tracePerformer;

    @Autowired
    private TraceRepository traceRepository;

    @Autowired
    private TraceLogVoter traceLogVoter;

    @Autowired
    private TraceThreadBridgeProvider traceThreadBridgeProvider;

    @Test
    public void testDisabled() {

        // Disable aspect
        tracePerformer.setEnabled(false);

        // Begin a trace
        TraceHeader traceHeader = traceRepository.beginTrace("name",
                "formattedName", "urlPrefix");

        // Call some methods
        mainService.fastMethod();
        mainService.slowMethod("foo");

        // Complete the trace
        traceRepository.completeTrace();

        // Make sure no steps were added by the aspect
        Assert.assertNull(traceRepository.getTrace(traceHeader.getId())
                .getTrace().getFrameStack());

    }

    @Test
    public void testTrace() throws IOException {

        // Enable aspect
        tracePerformer.setEnabled(true);

        // Begin a trace
        TraceHeader traceHeader = traceRepository.beginTrace("name",
                "formattedName", "urlPrefix");

        // Call some methods
        traceRepository.beginStep("label", StepType.HTTP);
        mainService.fastMethod();
        mainService.slowMethod("foo");
        traceRepository.completeStep();

        // Complete the trace
        traceRepository.completeTrace();

        // Verify
        ServerTrace trace = traceRepository.getTrace(traceHeader.getId());
        new Log4jTreeTraceLogger().log(trace, null);
        Assert.assertNotNull(traceHeader.getId());
        Assert.assertNotNull(traceHeader.getUrl());
        Assert.assertTrue(traceHeader.getUrl().contains(traceHeader.getId()));
        Assert.assertNotNull(trace.getTrace().getFrameStack());
        Assert.assertEquals("0", trace.getTrace().getFrameStack().getId());
        Assert.assertEquals(2, trace.getTrace().getFrameStack().getChildren()
                .size());

        // Write trace to json
        StringWriter stringWriter = new StringWriter();
        new JsonTraceWriter().write(trace, stringWriter);
        String result = stringWriter.toString();

        // Verify
        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("{\"trace\":"));
    }

    @Test
    public void testMultiThreadedTrace() throws IOException,
            InterruptedException {

        // Create a thread pool
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // Enable aspect
        tracePerformer.setEnabled(true);

        // Begin a trace
        TraceHeader traceHeader = traceRepository.beginTrace("name",
                "formattedName", "urlPrefix");

        // Begin a step
        traceRepository.beginStep("outer", StepType.HTTP);

        // Call some methods
        mainService.fastMethod();
        mainService.slowMethod("foo");

        // Get a bridge
        final TraceThreadBridge traceThreadBridge = traceThreadBridgeProvider
                .getTraceThreadBridge();

        // Create tasks to perform work across different threads
        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
        tasks.add(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                // Join the trace
                traceThreadBridge.addThread("first");

                // Call some methods
                mainService.fastMethod();
                mainService.slowMethod("foo1");

                // Leave the trace
                traceThreadBridge.removeThread();

                return null;
            }
        });
        tasks.add(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                // Join the trace
                traceThreadBridge.addThread("second");

                // Call some methods
                mainService.fastMethod();
                mainService.slowMethod("foo2");

                // Leave the trace
                traceThreadBridge.removeThread();

                return null;
            }
        });
        tasks.add(new Callable<Void>() {
            @Override
            public Void call() throws Exception {

                // Join the trace
                traceThreadBridge.addThread("third");

                // Call some methods
                mainService.fastMethod();
                mainService.slowMethod("foo3");

                // Leave the trace
                traceThreadBridge.removeThread();

                return null;
            }
        });

        // Run the tasks
        pool.invokeAll(tasks);

        // Complete the step
        traceRepository.completeStep();

        // Complete the trace
        traceRepository.completeTrace();

        // Verify
        ServerTrace trace = traceRepository.getTrace(traceHeader.getId());
        new Log4jTreeTraceLogger().log(trace, null);
        Assert.assertNotNull(traceHeader.getId());
        Assert.assertNotNull(traceHeader.getUrl());
        Assert.assertTrue(traceHeader.getUrl().contains(traceHeader.getId()));
        Assert.assertNotNull(trace.getTrace().getFrameStack());
        Assert.assertEquals("0", trace.getTrace().getFrameStack().getId());
        Assert.assertEquals(5, trace.getTrace().getFrameStack().getChildren()
                .size());
        Assert.assertEquals(3, trace.getTrace().getFrameStack().getChildren()
                .get(0).getChildren().size());
        Assert.assertEquals(1, trace.getTrace().getFrameStack().getChildren()
                .get(1).getChildren().size());
        Assert.assertEquals(2, trace.getTrace().getFrameStack().getChildren()
                .get(2).getChildren().size());
        Assert.assertEquals(2, trace.getTrace().getFrameStack().getChildren()
                .get(3).getChildren().size());
        Assert.assertEquals(2, trace.getTrace().getFrameStack().getChildren()
                .get(4).getChildren().size());

        // Write trace to json
        StringWriter stringWriter = new StringWriter();
        new JsonTraceWriter().write(trace, stringWriter);
        String result = stringWriter.toString();

        // Verify
        Assert.assertNotNull(result);
        Assert.assertTrue(result.startsWith("{\"trace\":"));

        // Shut down pool
        pool.shutdown();
    }

    @Test
    public void testLogVoting() throws IOException {

        // Enable aspect
        tracePerformer.setEnabled(true);

        // Begin a trace
        TraceHeader traceHeader = traceRepository.beginTrace("name",
                "formattedName", "urlPrefix");

        // Call some methods
        traceRepository.beginStep("label", StepType.HTTP);
        mainService.fastMethod();
        mainService.slowMethod("foo");
        traceRepository.completeStep();

        // Complete the trace
        traceRepository.completeTrace();

        // Run logger scenarios
        TestTraceLogger logger = new TestTraceLogger();
        traceLogVoter.setConfigFile(CONFIG1);
        TestLogRunner runner = new TestLogRunner(traceRepository, logger,
                traceLogVoter);
        runner.logTrace(traceHeader, null);
        Assert.assertTrue(logger.logged);

        logger.logged = false;
        traceLogVoter.setConfigFile(CONFIG2);
        runner.logTrace(traceHeader, null);
        Assert.assertTrue(logger.logged);

        logger.logged = false;
        traceLogVoter.setConfigFile(CONFIG3);
        runner.logTrace(traceHeader, null);
        Assert.assertTrue(logger.logged);

        logger.logged = false;
        traceLogVoter.setConfigFile(CONFIG4);
        runner.logTrace(traceHeader, null);
        Assert.assertFalse(logger.logged);

        logger.logged = false;
        traceLogVoter.setConfigFile(CONFIG5);
        runner.logTrace(traceHeader, null);
        Assert.assertFalse(logger.logged);

        logger.logged = false;
        traceLogVoter.setConfigFile(CONFIG6);
        runner.logTrace(traceHeader, null);
        Assert.assertFalse(logger.logged);

        logger.logged = false;
        traceLogVoter.setConfigFile(CONFIG5);
        TraceLogVoter voter2 = new StepThresholdTraceLogVoter();
        voter2.setConfigFile(CONFIG6);
        runner = new TestLogRunner(traceRepository, logger, traceLogVoter,
                voter2);
        runner.logTrace(traceHeader, null);
        Assert.assertFalse(logger.logged);

        logger.logged = false;
        traceLogVoter.setConfigFile(CONFIG5);
        voter2 = new StepThresholdTraceLogVoter();
        voter2.setConfigFile(CONFIG1);
        runner = new TestLogRunner(traceRepository, logger, traceLogVoter,
                voter2);
        runner.logTrace(traceHeader, null);
        Assert.assertTrue(logger.logged);
    }

    @Test
    public void testCleanup() {

        // Enable aspect
        tracePerformer.setEnabled(true);

        for (int i = 0; i < 200; i++) {
            int lastSize = traceRepository.getTraces().size();

            // Begin a trace
            String id = traceRepository.beginTrace("name", "formattedName",
                    "urlPrefix").getId();

            // Call some methods
            traceRepository.beginStep("label", StepType.HTTP);
            traceRepository.completeStep();

            // Complete the trace
            traceRepository.completeTrace();

            // Make sure saved traces never exceeds limit
            Assert.assertEquals(lastSize >= 99 ? 100 : lastSize + 1,
                    traceRepository.getTraces().size());

            // Make sure last trace was never cleaned up
            Assert.assertNotNull(traceRepository.getTrace(id));
        }

        // Make sure trace map can't be modified externally
        try {
            traceRepository.getTraces().clear();
            Assert.fail("Trace clear succeeded");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    private class TestTraceLogger implements TraceLogger {

        private boolean logged;

        @Override
        public void log(ServerTrace trace, HttpServletRequest request)
                throws IOException {
            logged = true;
        }
    }

    private class TestLogRunner extends SpeedTracerSpringFilter {

        public TestLogRunner(TraceRepository traceRepository,
                             TraceLogger traceLogger, TraceLogVoter... traceLogVoters) {
            this.logTraces = true;
            this.logVoting = true;
            this.traceRepository = traceRepository;
            this.traceLoggers = asList(traceLogger);
            this.traceLogVoters = asList(traceLogVoters);
        }

        @Override
        public void logTrace(TraceHeader traceHeader, HttpServletRequest request)
                throws IOException {
            super.logTrace(traceHeader, request);
        }
    }

    ;
}
