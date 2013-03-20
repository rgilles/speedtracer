speedtracer
===========

Server side implementation that provide trace for speed tracer.

This project is a fork of http://code.google.com/p/spring4speedtracer/

google chrome plugin project: http://code.google.com/p/speedtracer/



 * https://github.com/square/Dagger
 * http://eclipse.org/equinox/weaving/
 * http://julien.ponge.info/notes/revisiting-guice-and-aop-with-aspectj/
 * http://julien.ponge.info/notes/guice-or-aop-can-be-made-simple-sometimes/
 * https://github.com/jponge/guice-aspectj-sample

TODO
----

 * extract springsource specific dependencies in a dedicated project
 * make it work with spring again
 * make it work with guice
 * make it work with dagger
 * make it independent from http stake (idp)
 * make it independent from json serialization stake/technology
 * make it work with aop alliance
 * make it configurable: size, pointcut, ...
 * provide default annotations as pointcuts
 * maybe make it independent from logging technology by using the java standard one
 * have a look to byteman


    public class RequestDetails extends LazilyCreateableElement {

      ...

    private void maybeShowServerEvents(final Element parent,
          final Element insertAfter, final Css css, final Document document) {
        if (!info.hasServerTraceUrl()) {
            return;
        }

        final String traceUrl = info.getServerTraceUrl();

        // TODO(knorton): When playing back from a dump, we do not want to try to
        // fetch the server-side trace.
        serverEventController.requestTraceFor(info,
