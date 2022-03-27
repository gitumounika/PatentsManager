package com.patent.patentsmanager.controller;

import com.patent.patentsmanager.config.PatentConfiguration;
import com.patent.patentsmanager.enums.Status;
import com.patent.patentsmanager.model.Patent;
import com.patent.patentsmanager.process.PatentProcessor;
import com.patent.patentsmanager.repository.PatentRepository;
import com.patent.patentsmanager.services.PatentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.PersistenceException;
import java.util.List;

@Controller
public class PatentViewController extends PatentBaseController {

    private static Logger log = LoggerFactory.getLogger(PatentViewController.class);

    public PatentViewController(PatentProcessor patentProcessor, PatentConfiguration patentConfiguration, PatentService patentService, PatentRepository patentRepository) {
        super(patentProcessor, patentConfiguration,patentService, patentRepository);
    }

    @RequestMapping("/patents")
    public String view(Model model){
        log.trace("Display patents : patents " );
        try {
            List<Patent> patents = patentService.findByDownloadedStatus(Status.PROCESSED.getStatus());
            model.addAttribute("patents",patents);
        } catch (PersistenceException e) {
            log.error("Error in Display patents : patents " );
            e.printStackTrace();
        } catch (DataAccessException da){
            log.error("Error in Display patents : patents" );
        } catch (Exception e){
            log.error("Error in Display patents : patents" );
        }
        return "patents/view";
    }

    @RequestMapping(path = {"/","/search"})
    public String search(Model model, String keyword)  {
        try {
            List<Patent> patents;
            if (keyword != null) {
                patents = patentService.getPatentsByKeyword(keyword);
            } else {
                patents = patentService.findByDownloadedStatus(Status.PROCESSED.getStatus());
            }
            model.addAttribute("patents", patents);
        }catch (PersistenceException e) {
            log.error("Error in Display patents : patents " );
            e.printStackTrace();
        } catch (DataAccessException da){
            log.error("Error in Display patents : patents" );
        } catch (Exception e){
            log.error("Error in Display patents : patents" );
        }
        return "patents/view";
    }
}
