package com.patent.patentsmanager.controller;

import com.patent.patentsmanager.config.PatentConfiguration;
import com.patent.patentsmanager.enums.Status;
import com.patent.patentsmanager.model.Patent;
import com.patent.patentsmanager.process.PatentProcessor;
import com.patent.patentsmanager.repository.PatentRepository;
import com.patent.patentsmanager.services.PatentService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/load")
public class PatentLoadController extends PatentBaseController{

    private final static Logger log = LoggerFactory.getLogger(PatentLoadController.class);

    public PatentLoadController(PatentProcessor patentProcessor, PatentConfiguration patentConfiguration, PatentService patentService, PatentRepository patentRepository) {
        super(patentProcessor, patentConfiguration,patentService, patentRepository);
    }

    @GetMapping("/v1/patent/load")
    public @ResponseBody ResponseEntity<String> loadPatents(@RequestParam(required = false) Map<String, String> params){
        try {
            log.trace("Starting load Patents from USPTO API Endpoint : loadPatents "  );
            List<Patent> patents = patentService.loadPatentPublications(params);
            patents.forEach(patent -> {
                patent.setDownloadedStatus(Status.NEW.getStatus());
                patent.setProcessedStatus(Status.NEW.getStatus());
            });
            patentService.savePatents(patents);
            log.trace("Ending load Patents from USPTO API Endpoint : loadPatents " );
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (PersistenceException e) {
            log.error("Error in load Patents from USPTO API Endpoint : loadPatents " );
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DataAccessException | CallNotPermittedException da){
            log.error("Error load Patents from USPTO API Endpoint : loadPatents "  );
            return new ResponseEntity<>(da.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            log.error("Error load Patents from USPTO API Endpoint : loadPatents "  );
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/v1/patent/download")
    public @ResponseBody ResponseEntity<String> downloadPatents(){
        log.trace("Starting Download Patents file location URI : downloadPatents " );
        try {
            List<Patent> patents = patentService.findByDownloadedStatus(Status.NEW.getStatus());
            patentService.download(patents);
            log.trace("Ending Download Patents file location URI : downloadPatents " );
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (PersistenceException | DataAccessException e) {
            log.error("Error in Download Patents file location URI : downloadPatents " );
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            log.error("Error in Download Patents file location URI : downloadPatents " );
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
