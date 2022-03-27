package com.patent.patentsmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PatentConfiguration {

    @Value("${patent.file.text.directory}")
    public String fileStoreDestinationLocation;

    @Value("${patent.file.source.directory}")
    public String fileStoreLocation;

    @Value("${patent.file.ocr.engine}")
    public String ocrEngineDataPath;

    @Value("${patent.file.store.backup}")
    public String fileStoreBackup;

    @Value("${patent.uspto.developer.base.uri}")
    public String usptoRootApiUri;
}
