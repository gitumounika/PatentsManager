package com.patent.patentsmanager.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class PatentOcrThreadExecutor  {

    private static final Logger log = LoggerFactory.getLogger(PatentOcrThreadExecutor.class);

    int cores = Runtime.getRuntime().availableProcessors();
    int corePoolSize = 2;
    int maxPoolSize = 2;


    public PatentOcrThreadExecutor() {

    }

    /**
     * Compute core pool size and max pool size for ocr processing as OCR is IO operation
     * As part of OCR processing we are performing 2 Multi threading processes for each file
     * one: initiate ocr processing in multi thread
     * two: convert to images in multi threading
     * Available cores needs to be equally distributed to both processes to avoid thread rejection or GC
     * It is good to buffer one core for processing other processes other than the threads
     */
    public ExecutorService taskExecutor(String name) {
        if(cores > 4){
            corePoolSize = (cores/2) - 1;
            maxPoolSize = corePoolSize + 2;
        }
        ExecutorService executor = Executors.newFixedThreadPool(corePoolSize,new CustomizableThreadFactory(name));
        return executor;
    }
}
