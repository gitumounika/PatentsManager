package com.patent.patentsmanager.services;

import com.patent.patentsmanager.model.Patent;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PatentService {

    List<Patent> loadPatentPublications(Map<String, String> params) throws IOException,Exception;
    @Async
    void download(List<Patent> patents);
    long countByProcessedStatus(String status) throws Exception;

    void savePatents(List<Patent> patents) throws Exception;
    List<Patent> findByDownloadedStatus(String status) throws Exception ;
    List<Patent> findByProcessedStatus(String status) throws Exception ;
    List<Patent>findByProcessedStatusAndDownloadedStatus(String pStatus,String dStatus) throws Exception;
    List<Patent> getPatentsByKeyword(String keyword) throws Exception;

}
