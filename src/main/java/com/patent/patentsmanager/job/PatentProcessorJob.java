package com.patent.patentsmanager.job;

import com.patent.patentsmanager.config.PatentSchedulerConfig;
import com.patent.patentsmanager.constants.PatentConstants;
import com.patent.patentsmanager.enums.Status;
import com.patent.patentsmanager.model.Patent;
import com.patent.patentsmanager.process.PatentProcessor;
import com.patent.patentsmanager.services.PatentService;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.List;

@Configuration
public class PatentProcessorJob implements Job {

    private static Logger log = LoggerFactory.getLogger(PatentProcessorJob.class);

    private final static long REPEAT_INTERVAL = 1200000L;

    private final PatentProcessor patentProcessor;
    private final PatentService patentService;

    @Autowired
    public PatentProcessorJob(PatentProcessor patentProcessor, PatentService patentService) {
        this.patentProcessor = patentProcessor;
        this.patentService = patentService;
    }

    @Bean(name = "patentPdfOcrProceessJob")
    public JobDetailFactoryBean patentPdfOcrProcessJob() {
        return PatentSchedulerConfig.createJobDetail(this.getClass());
    }

    @Bean(name = "patentPdfOcrProcessTrigger")
    public SimpleTriggerFactoryBean patentPdfOcrProcessJobTrigger(@Qualifier("patentPdfOcrProceessJob") JobDetail jobDetail) {
        return PatentSchedulerConfig.createTrigger(jobDetail,REPEAT_INTERVAL);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("Running patentPdfOcrProcessJob execute | frequency ", REPEAT_INTERVAL);
        try {
            long activeOcrproess = Thread.getAllStackTraces().keySet().stream().filter(t-> (t.getName().contains(PatentConstants.OCR_THREAD_POOL_NAME)
                                                            && t.getState() == Thread.State.RUNNABLE)).count();
            log.info("Current Active Ocr processes Running : " + activeOcrproess);
            if(activeOcrproess == 0) {
                List<Patent> patents = patentService.findByProcessedStatusAndDownloadedStatus(Status.NEW.getStatus(), Status.PROCESSED.getStatus());
                if(!patents.isEmpty()) {
                    boolean enqueued = enqueue(patents);
                    if (enqueued) {
                        patentProcessor.ocrProcessInit(patents);
                        /*dequeued in each async run*/
                    }
                }
            } else {
                log.warn("Active Ocr Processes running , Did not enqueue for current patentPdfOcrProcessJob execute");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean enqueue(List<Patent> patents) throws Exception {
        patents.forEach(it -> {it.setProcessedStatus(Status.LOCKED.getStatus());});
        patentService.savePatents(patents);
        return true;
    }

}
