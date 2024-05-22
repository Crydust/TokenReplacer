package be.crydust.tokenreplacer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides an entrypoint to our application. Turns the arguments from the
 * command line into a Config and delegates further work to the Action class.
 */
public final class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private App() {
    }

    private static Options getOptions() {
        return new Options()
                .addOption(Option.builder("h")
                        .longOpt("help")
                        .hasArg(false)
                        .desc("print this message")
                        .build())
                .addOption(Option.builder("b")
                        .longOpt("begintoken")
                        .hasArg(true)
                        .desc("begintoken (default @)")
                        .argName("token")
                        .build())
                .addOption(Option.builder("e")
                        .longOpt("endtoken")
                        .hasArg(true)
                        .desc("endtoken (default @)")
                        .argName("token")
                        .build())
                .addOption(Option.builder("f")
                        .longOpt("folder")
                        .hasArg(true)
                        .desc("folder (default current directory)")
                        .argName("folder")
                        .build())
                .addOption(Option.builder("q")
                        .longOpt("quiet")
                        .hasArg(false)
                        .desc("quiet mode, do not ask if ok to replace")
                        .build())
                .addOption(Option.builder("r")
                        .longOpt("replacetokens")
                        .hasArg(true)
                        .desc("property file containing key value pairs (use -D to override)")
                        .argName("file")
                        .build())
                .addOption(Option.builder("x")
                        .longOpt("exclude")
                        .hasArg(true)
                        .desc("glob pattern to exclude").argName("glob")
                        .build())
                .addOption(Option.builder("D")
                        .argName("key=value")
                        .numberOfArgs(2)
                        .valueSeparator('=')
                        .desc("key value pairs to replace (required unless replacetokens file is defined)")
                        .build());
    }

    /**
     * Turns the arguments from the command line into a Config.
     *
     * @param args the command line arguments to parse
     * @return a valid configuration or null
     */
    @CheckForNull
    public static Config readConfig(@Nonnull String[] args) {
        Objects.requireNonNull(args);
        Config config = null;
        Options options = getOptions();
        try {
            CommandLine commandLine = new DefaultParser().parse(options, args);
            if (commandLine.hasOption("help")
                    || !(commandLine.hasOption("D") || commandLine.hasOption("replacetokens"))) {
                System.err.println("Provide at least one -D or -r argument.");
                printHelp(options);
            } else {
                Map<String, String> replacetokens = new HashMap<>();
                if (commandLine.hasOption("replacetokens")) {
                    Path replacetokensPath = Paths.get(commandLine.getOptionValue("replacetokens", System.getProperty("user.dir")));
                    Properties properties = new Properties();
                    try (InputStream in = new FileInputStream(replacetokensPath.toFile())) {
                        properties.load(in);
                        for (String key : properties.stringPropertyNames()) {
                            replacetokens.put(key, properties.getProperty(key));
                        }
                    } catch (IOException ex) {
                        System.err.printf("File %s could not be read.%n%s%n", replacetokensPath, ex.getMessage());
                        printHelp(options);
                    }
                }
                if (commandLine.hasOption("D")) {
                    Properties properties = commandLine.getOptionProperties("D");
                    for (String key : properties.stringPropertyNames()) {
                        String value = properties.getProperty(key);
                        if (replacetokens.containsKey(key)) {
                            System.out.printf("Overriding %s with value %s.%n", key, value);
                        }
                        replacetokens.put(key, value);
                    }
                }
                String[] excludes = new String[0];
                if (commandLine.hasOption("exclude")) {
                    excludes = commandLine.getOptionValues("exclude");
                }
                config = new Config(
                        commandLine.getOptionValue("begintoken", "@"),
                        commandLine.getOptionValue("endtoken", "@"),
                        replacetokens,
                        Paths.get(commandLine.getOptionValue("folder", System.getProperty("user.dir"))),
                        commandLine.hasOption("quiet"),
                        excludes
                );
            }
        } catch (ParseException ex) {
            LOGGER.error("Parsing failed.", ex);
            System.err.println(ex.getMessage());
            printHelp(options);
        }
        return config;
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar tokenreplacer.jar", options);
    }

    private static boolean readContinue() {
        System.out.println("Continue [y/N]?");
        try (Scanner scanner = new Scanner(
                System.in,
                StandardCharsets.US_ASCII)) {
            return scanner.next().equalsIgnoreCase("y");
        }
    }

    public static void main(String[] args) {
        Config config = readConfig(args);
        if (config != null) {
            System.out.println(config);
            if (config.isQuiet() || readContinue()) {
                new Action(config).run();
                System.out.println("Done.");
            } else {
                System.out.println("Canceled.");
            }
        }
    }

}
