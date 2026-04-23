package io.yerektus.qadam.coreapi.common.rag;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Service
public class TextExtractorService {

    /**
     * Extracts text from a file based on its extension (.pdf, .docx, .txt).
     */
    public Mono<String> extractText(String filePath) {
        return Mono.fromCallable(() -> {
            String lower = filePath.toLowerCase();

            if (lower.endsWith(".pdf")) {
                return extractPdf(filePath);
            } else if (lower.endsWith(".docx")) {
                return extractDocx(filePath);
            } else if (lower.endsWith(".txt")) {
                return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
            } else {
                throw new IllegalArgumentException("Unsupported file type: " + filePath);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private String extractPdf(String filePath) throws Exception {
        try (PDDocument doc = Loader.loadPDF(Path.of(filePath).toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }

    private String extractDocx(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument doc = new XWPFDocument(fis)) {
            return doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .collect(Collectors.joining("\n"));
        }
    }
}
