package help.bac.avis.controleur;

import help.bac.avis.service.PdfService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
public class PdfControleur {
    private PdfService pdfService;
    private String url;

    public PdfControleur(PdfService pdfService) {
        this.pdfService = pdfService;
        this.url = "";
    }

    @GetMapping(path = "/pdf")
    public ResponseEntity<byte[]> generatePdf() throws MessagingException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=avis.pdf");
        Map data = this.pdfService.generatePdf("mytemplate");
        url = data.get("url").toString();
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body((byte[]) data.get("pdf"));
    }

    @GetMapping(path = "/telecharger")
    public ResponseEntity<InputStreamResource> telechargerPdf() throws IOException {
        InputStreamResource resource = this.pdfService.telechargerPdf(url);

        if (resource != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=avis.pdf");
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
