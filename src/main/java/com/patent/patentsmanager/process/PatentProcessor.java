package com.patent.patentsmanager.process;

import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface PatentProcessor<Patent> {
    String pdfToTextWithOCR(PDDocument document, String filename, String currDirectory) throws IOException, TesseractException, InterruptedException;
    String pdfToText(PDDocument document,String filename) throws IOException;
    void writeToTextFile(String text, String currDirectory, String filename) throws IOException,Exception;
    boolean ocrProcessInit(List<com.patent.patentsmanager.model.Patent> patents) throws Exception ;
}
