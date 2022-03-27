package com.patent.patentsmanager.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patent.patentsmanager.config.PatentConfiguration;
import com.patent.patentsmanager.constants.PatentConstants;
import com.patent.patentsmanager.enums.Status;
import com.patent.patentsmanager.model.Patent;
import com.patent.patentsmanager.repository.PatentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.PersistenceException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatentLoadService implements PatentService{

    private static final Logger log = LoggerFactory.getLogger(PatentLoadService.class);

    private final PatentConfiguration patentConfiguration;
    private final PatentRepository patentRepository;
    private final RestTemplate restTemplate;

    private static final String PATENT_DEFAULT_FILTER_SEARCH_TEXT_USPTO = "searchText";
    private static final String PATENT_DEFAULT_FILTER = "largeTextSearchFlag";
    private static final String PATENT_DEFAULT_FILTER_OFFSET = "rows";

    private static final String PATENT_DEFAULT_FILTER_SEARCH_TEXT_USPTO_VALUE = "Qualcomm";
    private static final String PATENT_DEFAULT_FILTER_VALUE = "N";
    private static final String PATENT_DEFAULT_FILTER_OFFSET_VALUE = "30";

    @Autowired
    public PatentLoadService(PatentConfiguration patentConfiguration, PatentRepository patentRepository){
        this.patentConfiguration = patentConfiguration;
        this.patentRepository = patentRepository;
        this.restTemplate = new RestTemplateBuilder().build();
    }

    @CircuitBreaker(name = "endPointUsptoCircuitBreaker",fallbackMethod = "fallBackUsptoEndpoint")
    public List<Patent> loadPatentPublications(Map<String, String> params) throws IOException,Exception {
        log.trace("Starting load Patents from USPTO API Endpoint : loadPatentPublications " );
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString());
        final ObjectMapper objectMapper = new ObjectMapper();
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(patentConfiguration.usptoRootApiUri).queryParams(constructQueryParams(params)).encode().toUriString();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(urlTemplate, JsonNode.class);
        Patent[] patents = objectMapper.readValue(response.getBody().get("results").toString(), Patent[].class);
        log.trace("Ending load Patents from USPTO API Endpoint : loadPatentPublications " );
        return Arrays.asList(patents);
    }


    private MultiValueMap constructQueryParams(Map<String,String> params) throws Exception {
        log.trace("Starting construct QueryParams for USPTO API Endpoint : constructQueryParams " );
        MultiValueMap<String, String> queryParams;
        if(null == params) {
            queryParams = new LinkedMultiValueMap<String,String>();
            queryParams.put(PATENT_DEFAULT_FILTER_SEARCH_TEXT_USPTO, List.of(PATENT_DEFAULT_FILTER_SEARCH_TEXT_USPTO_VALUE));
        } else {
            queryParams = new LinkedMultiValueMap<String,String> (params.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue()))));
        }
        queryParams.putIfAbsent(PATENT_DEFAULT_FILTER_OFFSET, List.of(PATENT_DEFAULT_FILTER_OFFSET_VALUE));
        queryParams.putIfAbsent(PATENT_DEFAULT_FILTER, List.of(PATENT_DEFAULT_FILTER_VALUE));
        log.trace("Ending construct QueryParams for USPTO API Endpoint : constructQueryParams " );
        return queryParams;
    }

    @Override
    @Async
    public void download(List<Patent> patents)  {
        log.trace("Starting Async download patents to from location URL for thread : download " );
        if(null != patents && !patents.isEmpty()){
            patents.forEach(patent -> {
                try {
                    FileUtils.copyURLToFile(new URL(patent.getFilelocationURI()),
                            new File(patentConfiguration.fileStoreLocation+patent.getPatentApplicationNumber()+
                                    PatentConstants.FILE_PATH_SEPERATOR + patent.getPublicationDocumentIdentifier()+ PatentConstants.FILE_PDF_SUFFIX),
                            1000,
                            1000);
                    patent.setDownloadedStatus(Status.PROCESSED.getStatus());
                } catch (IOException e) {
                    log.error("Error in Async download patents to from location URL for thread: download" + patent.getPatentApplicationNumber());
                }
            });
            patentRepository.saveAll(patents);
        }
        log.trace("Ending Async download patents to from location URL for thread: download " );
    }

    /**
     * Fall back method for Circuit breaker on End point Failure
     * @param params
     * @param throwable
     * @return
     */
    public List<Patent> fallBackUsptoEndpoint(Map<String,String> params,Throwable throwable){
        log.error(" Starting Circuit breaker Call back" );
        List<Patent> patents = (List<Patent>) patentRepository.findAllById(Arrays.asList("USAAAATEST"));
        log.error("Ending circuit breaker call back" , throwable.getMessage());
        return patents;
    }

    @Override
    public void savePatents(List<Patent> patents) throws Exception {
        log.trace("Starting save patents to DB : savePatents " );
        patentRepository.saveAll(patents);
        log.trace("Ending save patents to DB : savePatents " );
    }

    @Override
    public List<Patent> findByDownloadedStatus(String status) throws  Exception  {
        log.trace("Starting findByDownloadedStatus : findByDownloadedStatus" );
        List<Patent> patents = (List<Patent>) patentRepository.findByDownloadedStatus(status);
        log.trace("Ending findByProcessedStatus : findByDownloadedStatus" );
        return patents;
    }

    @Override
    public List<Patent> findByProcessedStatus(String status) throws Exception {
        log.trace("Starting findByProcessedStatus : findByProcessedStatus" );
        List<Patent> patents = (List<Patent>) patentRepository.findByProcessedStatus(status);
        log.trace("Ending findByProcessedStatus : findByProcessedStatus" );
        return patents;
    }

    @Override
    public List<Patent> getPatentsByKeyword(String keyword) throws  Exception {
        log.trace("Starting Querying Downloaded Patents : findByDownloadedStatus" );
        List<Patent> patents = patentRepository.findByKeyword(keyword);
        log.trace("Ending Querying Downloaded patents : findByDownloadedStatus" );
        return patents;
    }

    @Override
    public long countByProcessedStatus(String status) throws Exception {
        return patentRepository.countByProcessedStatus(status);
    }

    @Override
    public List<Patent> findByProcessedStatusAndDownloadedStatus(String pStatus, String dStatus) throws Exception {
        return patentRepository.findByProcessedStatusAndDownloadedStatus(pStatus,dStatus);
    }
}
