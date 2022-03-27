package com.patent.patentsmanager.process;


import com.patent.patentsmanager.config.PatentConfiguration;
import com.patent.patentsmanager.constants.PatentConstants;
import com.patent.patentsmanager.model.Patent;
import net.sourceforge.tess4j.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * author : Mounika
 */
@Service
public class PatentProcessorImpl implements PatentProcessor<PDDocument> {

    private static final Logger log = LoggerFactory.getLogger(PatentProcessorImpl.class);

    private static final String TESSERACTS_LANGUAGE = "eng";
    private static final String EMPTY_STRING = " ";
    private final PatentConfiguration patentConfiguration;


    @Autowired
    public PatentProcessorImpl(PatentConfiguration patentConfiguration) {
        this.patentConfiguration = patentConfiguration;
    }

    /**
     * @param document
     * @return
     * @throws IOException
     * @throws TesseractException
     */
    @Override
    public String pdfToTextWithOCR(PDDocument document, String fileName, String currDirectory) throws IOException, TesseractException, InterruptedException {
        log.info("Starting pdfToTextWithOCR for patent: " + currDirectory + EMPTY_STRING + fileName + EMPTY_STRING + Thread.currentThread().getName());
        String result = null;
        try {
            Tesseract1 tesseract = new Tesseract1();
            tesseract.setDatapath(patentConfiguration.ocrEngineDataPath);
            tesseract.setLanguage(TESSERACTS_LANGUAGE);
            tesseract.setVariable("user_defined_dpi","71");
            /* PSM Auto detects vertical segmented page text more accurately than other PSM (Page Segment Mode) options.*/
            tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_AUTO);
            result = tesseract.doOCR(pdfToImage(document), new Rectangle());
            log.info("Ending pdfToTextWithOCR for patent: " + currDirectory + EMPTY_STRING + fileName + EMPTY_STRING + Thread.currentThread().getName());
        } catch (IOException io) {
            throw io;
        } catch (InterruptedException e) {
            throw e;
        } finally {
            document.close();
        }
        return result;
    }

    /**
     * @param document
     * @return
     * @throws IOException
     */
    @Override
    public String pdfToText(PDDocument document, String fileName) throws IOException {
        log.info("Starting pdfToText for : " + fileName + EMPTY_STRING + Thread.currentThread().getName());
        String strippedText = null;
        try {
            if (null != document) {
                PDFTextStripper stripper = new PDFTextStripper();
                strippedText = stripper.getText(document);
            }
        } catch (IOException io){
            throw io;
        } finally {
            document.close();
        }
        log.info("Ending pdfToText for : " + fileName + EMPTY_STRING + Thread.currentThread().getName());
        return strippedText;
    }

    /**
     * @param\text
     * @param filename
     * @throws IOException
     */
    @Override
    public void writeToTextFile(String text, String currDirectory, String filename) throws IOException {
        log.trace("Starting writeToTextFile for patent: " + currDirectory + EMPTY_STRING + filename + EMPTY_STRING + Thread.currentThread().getName());
        FileWriter textFile = null;
        try {
            if (null != text) {
                textFile = new FileWriter(patentConfiguration.fileStoreLocation+currDirectory
                        + PatentConstants.FILE_PATH_SEPERATOR + filename + PatentConstants.FILE_TEXT_SUFFIX);
                textFile.write(text);
            }
        }catch(IOException io){
            throw io;
        }finally {
            if(null != textFile) {
                textFile.close();
            }
        }
        log.trace("Ending writeToTextFile for patent: " + currDirectory + EMPTY_STRING + filename + EMPTY_STRING + Thread.currentThread().getName());
    }


    /**
     * @param document
     * @return
     * @throws IOException
     */
    public List<IIOImage> pdfToImage(PDDocument document) throws IOException, InterruptedException {
        log.trace("Starting pdfToImage for : "  + Thread.currentThread().getName());
        ExecutorService pool = Executors.newFixedThreadPool(3);
        List<IIOImage> images = new ArrayList<IIOImage>();
        Map<Integer,IIOImage> imageMap = new HashMap<Integer,IIOImage>();
        if (null != document) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            BufferedImage out[] = new BufferedImage[numberOfPages];

            /*PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                CompletableFuture.supplyAsync(() -> {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI( page, 300, ImageType.RGB);
                    IIOImage image = new IIOImage(bim, null, null);
                    images.add(image);
                    return null;
                },  pool);
            }

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                IIOImage image = new IIOImage(bim, null, null);
                images.add(image);
            }*/
            for(int page = 0; page<out.length;page++){
                int pageIndex = page;
                pool.submit(() -> {
                    BufferedImage bim = null;
                    try {
                        bim = pdfRenderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);
                        IIOImage image = new IIOImage(bim, null, null);
                        imageMap.put(pageIndex,image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            pool.shutdown();
            pool.awaitTermination(5, TimeUnit.HOURS);
            for(Integer i=0;i<numberOfPages;i++){
                images.add(imageMap.get(i));
            }
        }
        log.trace("Ending pdfToImage for : "  + Thread.currentThread().getName());
        return images;
    }
}
