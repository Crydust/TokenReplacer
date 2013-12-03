package be.crydust.tokenreplacer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    private final Config config;

    public static Options getOptions() {
        Options options = new Options();
        options.addOption("h", "help", false, "print this message");
        options.addOption("b", "begintoken", true, "begintoken (default @)");
        options.addOption("e", "endtoken", true, "endtoken (default @)");
        options.addOption("f", "folder", true, "folder (default current directory)");
        OptionBuilder.withArgName("key=value");
        OptionBuilder.hasArgs(2);
        OptionBuilder.withValueSeparator('=');
        OptionBuilder.withDescription("key value pairs to replace (required)");
        OptionBuilder.isRequired(true);
        options.addOption(OptionBuilder.create("D"));
        return options;
    }

    public static void main(String[] args) {
        try {
            Options options = getOptions();
            CommandLine commandLine = new BasicParser().parse(options, args);
            if (commandLine.hasOption('h')) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar TockenReplacer.jar", options);
            } else {
                Map<String, String> replacetokens = new HashMap<>();
                if (commandLine.hasOption("D")) {
                    Properties properties = commandLine.getOptionProperties("D");
                    for (String key : properties.stringPropertyNames()) {
                        replacetokens.put(key, properties.getProperty(key));
                    }
                }
                Config config = new Config(
                        commandLine.getOptionValue('b', "@"),
                        commandLine.getOptionValue('e', "@"),
                        replacetokens,
                        Paths.get(commandLine.getOptionValue('f', System.getProperty("user.dir")))
                );
                LOGGER.debug("{}", config);

                System.out.println("Continue [y/N]?");
                Scanner scan = new Scanner(System.in);
                String answer = scan.next();
                if (answer.trim().equalsIgnoreCase("y")) {
                    new App(config).run();
                    System.out.println("Done.");
                } else {
                    System.out.println("Canceled.");
                }
            }
        } catch (ParseException ex) {
            LOGGER.error("Parsing failed.", ex);
        }
    }

    private App(Config config) {
        this.config = config;
    }

    @Override
    public void run() {
        try {
            TokenReplacer replacer = new TokenReplacer(config.getBegintoken(), config.getEndtoken(), config.getReplacetokens());
            List<Path> templates = new FilesFinder(config.getFolder(), "*.template").call();
            for (Path template : templates) {
                Path file = FileExtensionUtil.replaceExtension(template, "");
                if (file.toFile().exists()) {
                    String fileContents = new FileReader(file).call();
                    Path backupFile = FileExtensionUtil.replaceExtension(template, ".bak");
                    new FileWriter(fileContents, backupFile).run();
                }
                String templateContents = new FileReader(template).call();
                new FileWriter(replacer.replace(templateContents), file).run();
            }
        } catch (Exception ex) {
            LOGGER.error(null, ex);
        }
    }

    private static class Config {

        private final String begintoken;
        private final String endtoken;
        private final Map<String, String> replacetokens;
        private final Path folder;

        public Config(String begintoken, String endtoken, Map<String, String> replacetokens, Path folder) {
            this.begintoken = begintoken;
            this.endtoken = endtoken;
            this.replacetokens = replacetokens;
            this.folder = folder;
        }

        public String getBegintoken() {
            return begintoken;
        }

        public String getEndtoken() {
            return endtoken;
        }

        public Map<String, String> getReplacetokens() {
            return replacetokens;
        }

        public Path getFolder() {
            return folder;
        }

        @Override
        public String toString() {
            return "Config{" + "begintoken=" + begintoken + ", endtoken=" + endtoken + ", replacetokens=" + replacetokens + ", folder=" + folder + '}';
        }

    }

}
