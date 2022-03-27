package com.patent.patentsmanager.process;

import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface PatentProcessor<T> {
    String pdfToTextWithOCR(PDDocument document, String filename, String currDirectory) throws IOException, TesseractException, InterruptedException;
    String pdfToText(PDDocument document,String filename) throws IOException;
    void writeToTextFile(String text, String currDirectory, String filename) throws IOException,Exception;
}
