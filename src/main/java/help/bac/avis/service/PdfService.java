package help.bac.avis.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PdfService {
    private final SpringTemplateEngine springTemplateEngine;
    private NotificationService notificationService;
    private final String url = "http://localhost:8080/";

    public PdfService(SpringTemplateEngine springTemplateEngine, NotificationService notificationService) {
        this.springTemplateEngine = springTemplateEngine;
        this.notificationService = notificationService;
    }

    public byte[] generatePdf(String template) throws MessagingException, IOException {
        // Configuration cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", System.getenv("cloud_name"));
        config.put("api_key", System.getenv("api_key"));
        config.put("api_secret", System.getenv("api_secret"));
        Cloudinary cloudinary = new Cloudinary(config);

        // Definition du contexte
        Context context = new Context();
        context.setVariable("name", "John Doe");
        String html = springTemplateEngine.process(template, context);

        // Conversion du HTML en XHTML
        Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String xhtml = document.html();

        // Generation du PDF
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(xhtml, url);
        renderer.layout();
        renderer.createPDF(stream);
        byte[] pdf = stream.toByteArray();

        notificationService.envoyerPdf(pdf);
        String public_id = UUID.randomUUID().toString();
        Map result = cloudinary.uploader()
                .upload(pdf, ObjectUtils.asMap("public_id", public_id));
        log.info("result: " + result.get("url"));

        return pdf;
    }
}
