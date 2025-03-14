package be.crydust.tokenreplacer;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.EventRecordingLogger;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.SubstituteLogger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public final class LoggingSpy {
    private final ByteArrayOutputStream outBaos;
    private final PrintStream out;
    private final ByteArrayOutputStream errBaos;
    private final PrintStream err;
    private final ArrayDeque<SubstituteLoggingEvent> eventQueue;
    private final SubstituteLogger logger;

    public LoggingSpy() {
        outBaos = new ByteArrayOutputStream();
        out = new PrintStream(outBaos);
        errBaos = new ByteArrayOutputStream();
        err = new PrintStream(errBaos);
        eventQueue = new ArrayDeque<>();
        logger = new SubstituteLogger("be.crydust.tokenreplacer.LoggingSpy", eventQueue, false);
    }

    public ActionBuilder apply(ActionBuilder actionBuilder) {
        return actionBuilder
                .withOut(out)
                .withErr(err)
                .withLog(logger);
    }

    public String out() {
        return outBaos.toString();
    }

    public String err() {
        return errBaos.toString();
    }

    public String log() {
        return eventQueue.stream()
                .map(loggingEvent -> MessageFormatter.arrayFormat(
                        loggingEvent.getMessage(),
                        loggingEvent.getArgumentArray(),
                        loggingEvent.getThrowable()))
                .map(tuple -> {
                    if (tuple.getThrowable() == null) {
                        return tuple.getMessage();
                    }
                    StringWriter sw = new StringWriter();
                    tuple.getThrowable().printStackTrace(new PrintWriter(sw));
                    return tuple.getMessage() + "\n" + sw;
                })
                .collect(joining("\n"));
    }
}
