package com.deswaef.bekaert;

import com.deswaef.bekaert.service.FileService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class BekaertApplication implements CommandLineRunner {

    private static final String OUTPUT_FILE_DIRECTORY = "output";
    public static final String DOUBLES_TEXT_FILE = "doubles.txt";
    public static final String MISSING_TEXT_FILE = "missing.txt";
    @Autowired
    private FileService fileService;

    private Log LOG = LogFactory.getLog(BekaertApplication.class);

    @Override
    public void run(String... args) throws Exception {
        List<File> files = fileService.getFiles();

        preconditions();

        List<File> exceptionDoubles = new ArrayList<>();
        List<File> exceptionNotFounds = new ArrayList<>();
        files
                .forEach(x -> {
                    if (!x.exists()) {
                        LOG.info(x + " does not exist in the input directory");
                        exceptionNotFounds.add(x);
                    } else {
                        File file = new File(OUTPUT_FILE_DIRECTORY + File.separator + x.getName());
                        try {
                            Files.copy(x.toPath(), file.toPath());
                        } catch (IOException e) {
                            exceptionDoubles.add(x);
                            LOG.info("error trying to copy " + x.getName() + " > Probably already exists in the output directory");
                        }
                    }
                });
        writeAsListOfFiles(exceptionDoubles, DOUBLES_TEXT_FILE);
        writeAsListOfFiles(exceptionNotFounds, MISSING_TEXT_FILE);
        LOG.info("Done");
    }

    private void preconditions() {
        outputDirectoryPrecondition();
        exceptionFilePrecondition();
    }

    private void exceptionFilePrecondition() {
        File doubles = new File(DOUBLES_TEXT_FILE);
        if (doubles.exists()) {
            doubles.delete();
        }
        File missing = new File(MISSING_TEXT_FILE);
        if (missing.exists()) {
            missing.delete();
        }
    }

    private void outputDirectoryPrecondition() {
        File f = new File("output");
        if (!f.exists()) {
            LOG.info("creating the output directory");
            f.mkdir();
        }
    }

    private void writeAsListOfFiles(List<File> exceptions, String textFile) {
        try {
            Files.write(new File(textFile).toPath(),
                    exceptions
                            .stream()
                            .map(File::getName)
                            .collect(Collectors.toList()),
                    StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            LOG.error("unable to write to file for exceptions");
        }
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(BekaertApplication.class, args);
    }
}
