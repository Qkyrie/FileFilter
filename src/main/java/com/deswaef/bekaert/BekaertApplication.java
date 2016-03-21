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
import java.util.List;

@SpringBootApplication
public class BekaertApplication implements CommandLineRunner {

    private static final String OUTPUT_FILE_DIRECTORY = "output";
    @Autowired
    private FileService fileService;

    private Log LOG = LogFactory.getLog(BekaertApplication.class);

    @Override
    public void run(String... args) throws Exception {
        List<File> files = fileService.getFiles();

        File f = new File("output");
        if (!f.exists()) {
            LOG.info("creating the output directory");
            f.mkdir();
        }

        files
                .forEach(x -> {
                    if (!x.exists()) {
                        LOG.info(x + " does not exist in the input directory");
                    } else {
                        File file = new File(OUTPUT_FILE_DIRECTORY + File.separator + x.getName());
                        try {
                            Files.copy(x.toPath(), file.toPath());
                        } catch (IOException e) {
                            LOG.info("error trying to copy " + x.getName() + " > Probably already exists in the output directory");
                        }
                    }
                });
        LOG.info("Done");
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(BekaertApplication.class, args);
    }
}
