package com.deswaef.bekaert.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {

    private static final String INPUT_FILE_DIRECTORY = "input";
    private static final String INPUT_FILE = "input.txt";

    private Log LOG = LogFactory.getLog(FileService.class);


    public List<File> getFiles() {
        LOG.info("going to look for files in input directory");
        List<File> exceptionNotFounds = new ArrayList<>();

        File file = new File(INPUT_FILE);
        if (file.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                return br.lines()
                        .map(line -> new File(INPUT_FILE_DIRECTORY + File.separator + line))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            } finally {
                try {
                    if (br != null) br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            LOG.error("output file does not exist");
            return new ArrayList<>();
        }
    }

    private void writeExceptionNotFounds(List<File> exceptionNotFounds) {

    }

}
