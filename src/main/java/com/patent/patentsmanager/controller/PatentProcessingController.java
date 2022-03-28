package com.patent.patentsmanager.controller;

import com.patent.patentsmanager.config.PatentConfiguration;
import com.patent.patentsmanager.enums.Status;
import com.patent.patentsmanager.model.Patent;
import com.patent.patentsmanager.process.PatentProcessor;
import com.patent.patentsmanager.repository.PatentRepository;
import com.patent.patentsmanager.services.PatentService;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

@Component
@RestController
@RequestMapping("/process")
public class PatentProcessingController extends PatentBaseController {

    private static final Logger log = LoggerFactory.getLogger(PatentProcessingController.class);

    public PatentProcessingController(PatentProcessor patentProcessor, PatentConfiguration patentConfiguration, PatentService patentService, PatentRepository patentRepository) {
        super(patentProcessor, patentConfiguration, patentService, patentRepository);
    }

    @GetMapping("/v2/patent/ocr/pdftotext")
    public @ResponseBody ResponseEntity<String> ocrPdfTextExtract() {
        log.trace("Starting Async Pdf OCR Text extraxt : ocrPdfTextExtract");
        try {
            long ocrQueuesize = patentService.countByProcessedStatus(Status.LOCKED.getStatus());
            if(ocrQueuesize < 10) {
                List<Patent> patents = patentService.findByProcessedStatusAndDownloadedStatus(Status.NEW.getStatus(), Status.PROCESSED.getStatus());
                if (null != patents && !patents.isEmpty()) {
                    patentProcessor.ocrProcessInit(patents);
                }
            } else {
                return new ResponseEntity<>("More than 10 files are being processsed currently, Please try after sometime", HttpStatus.OK);
            }
            log.trace("Ending Pdf OCR Text extraxt : ocrPdfTextExtract");
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch (Exception io) {
            log.error("Error in Pdf OCR Text extraxt : ocrPdfTextExtract");
            io.printStackTrace();
            return new ResponseEntity<>(io.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/v1/patent/ocr/pdftotext")
    public @ResponseBody ResponseEntity<String> ocrPdfTextExtract(@RequestParam("file") File file) {
        log.trace("Starting sync Pdf OCR Text extraxt : ocrPdfTextExtract");
        try {
            log.trace("Starting Sync Pdf OCR Text extraxt : ocrPdfTextExtract");
            if (null != file && file.isFile()) {
                PDDocument document = PDDocument.load(file);
                String name = FilenameUtils.getBaseName(file.getName());
                String convertedText = patentProcessor.pdfToTextWithOCR(document, name, name);
                if (null != convertedText) {
                    patentProcessor.writeToTextFile(convertedText, patentConfiguration.fileStoreDestinationLocation,
                            FilenameUtils.getBaseName(file.getName()));
                }
                log.trace("Ending sync Pdf OCR Text extraxt : ocrPdfTextExtract");
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                log.warn(" Invalid Input file in sync Pdf OCR Text extraxt : ocrPdfTextExtract");
                return new ResponseEntity<>("Invalid input file ", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error in sync Pdf OCR Text extraxt : ocrPdfTextExtract , May be an Invalid File" + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/v1/patent/pdftotext")
    public @ResponseBody
    ResponseEntity<String> pdfToText(@RequestParam("file") MultipartFile file) {
        log.trace("Starting Pdf to Text extraxt : pdfToText ");
        try {
            if (null != file && file.getSize() != 0) {
                PDDocument document = PDDocument.load(file.getBytes());
                String name = FilenameUtils.getBaseName(file.getOriginalFilename());
                String convertedText = patentProcessor.pdfToText(document, name);
                if (null != convertedText) {
                    patentProcessor.writeToTextFile(convertedText, patentConfiguration.fileStoreDestinationLocation, FilenameUtils.getBaseName(file.getOriginalFilename()));
                }
                log.trace("Ending Pdf to Text extraxt : pdfToText ");
                return new ResponseEntity<>("Success", HttpStatus.OK);
            } else {
                log.warn("Invalid input file in Pdf to Text extraxt : pdfToText ");
                return new ResponseEntity<>("Invalid input file ", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Error in Pdf to Text extraxt : pdfToText ");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
