package be.crydust.tokenreplacer;

import static java.util.Objects.requireNonNullElse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

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

    static final Options OPTIONS = new Options()
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

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private App() {
    }

    /**
     * Turns the arguments from the command line into a Config.
     *
     * @param args the command line arguments to parse
     * @return a valid configuration
     * @throws ReadConfigFailed when configuration is not valid or the excludes file could not be read.
     */
    @Nonnull
    static Config readConfig(@Nonnull String[] args) throws ReadConfigFailed {
        Objects.requireNonNull(args);
        try {
            CommandLine commandLine = new DefaultParser().parse(App.OPTIONS, args);
            if (commandLine.hasOption("help")
                    || !(commandLine.hasOption("D") || commandLine.hasOption("replacetokens"))) {
                throw new ReadConfigFailed("Provide at least one -D or -r argument.");
            }
            return new Config(
                    commandLine.getOptionValue("begintoken", "@"),
                    commandLine.getOptionValue("endtoken", "@"),
                    readReplacetokens(commandLine),
                    Paths.get(commandLine.getOptionValue("folder", System.getProperty("user.dir"))),
                    commandLine.hasOption("quiet"),
                    requireNonNullElse(commandLine.getOptionValues("exclude"), new String[0])
            );
        } catch (ParseException | IOException ex) {
            throw new ReadConfigFailed("Configuration not valid.", ex);
        }
    }

    @Nonnull
    private static Map<String, String> readReplacetokens(CommandLine commandLine) throws IOException {
        Map<String, String> replacetokens = new HashMap<>();
        if (commandLine.hasOption("replacetokens")) {
            Path replacetokensPath = Paths.get(commandLine.getOptionValue("replacetokens", System.getProperty("user.dir")));
            Properties properties = readProperties(replacetokensPath);
            for (String key : properties.stringPropertyNames()) {
                replacetokens.put(key, properties.getProperty(key));
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
        return replacetokens;
    }

    @Nonnull
    private static Properties readProperties(Path filePath) throws IOException {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(filePath))) {
            Properties properties = new Properties();
            properties.load(in);
            return properties;
        } catch (IOException ex) {
            throw new IOException(String.format("File %s could not be read.", filePath), ex);
        }
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar tokenreplacer.jar", App.OPTIONS);
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
        try {
            Config config = readConfig(args);
            System.out.println(config);
            if (config.isQuiet() || readContinue()) {
                new Action(config).run();
                System.out.println("Done.");
            } else {
                System.out.println("Canceled.");
            }
        } catch (ReadConfigFailed ex) {
            LOGGER.trace("Failed to read config", ex);
            System.err.println(ex.getMessage());
            if (ex.getCause() != null) {
                System.err.println(ex.getCause().getMessage());
            }
            printHelp();
        }
    }

}
