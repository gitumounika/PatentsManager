package com.patent.patentsmanager.controller;


import com.patent.patentsmanager.config.PatentConfiguration;
import com.patent.patentsmanager.constants.PatentConstants;
import com.patent.patentsmanager.process.PatentProcessor;
import com.patent.patentsmanager.repository.PatentRepository;
import com.patent.patentsmanager.services.PatentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FilenameFilter;

@Controller
public class PatentBaseController {

    public final PatentProcessor patentProcessor;
    public final PatentConfiguration patentConfiguration;
    public final PatentService patentService;
    public final PatentRepository patentRepository;
    public FilenameFilter filter;

    @Autowired
    public PatentBaseController(PatentProcessor patentProcessor, PatentConfiguration patentConfiguration, PatentService patentService, PatentRepository patentRepository) {
        this.patentProcessor = patentProcessor;
        this.patentConfiguration = patentConfiguration;
        this.patentService = patentService;
        this.patentRepository = patentRepository;
        this.filter = filter();
    }

    private FilenameFilter filter() {
        this.filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(PatentConstants.FILE_PDF_SUFFIX);
            }
        };
        return filter;
    }
}
