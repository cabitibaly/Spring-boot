package help.bac.avis.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class PdfService {
    private final SpringTemplateEngine springTemplateEngine;
    private final NotificationService notificationService;
    private final String url = "http://localhost:8080/";

    public PdfService(SpringTemplateEngine springTemplateEngine, NotificationService notificationService) {
        this.springTemplateEngine = springTemplateEngine;
        this.notificationService = notificationService;
    }

    public Map generatePdf(String template) throws MessagingException, IOException {
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

//        notificationService.envoyerPdf(pdf);
        String public_id = UUID.randomUUID().toString();
        Map result = cloudinary.uploader()
                .upload(pdf, ObjectUtils.asMap("public_id", public_id));

        Map data = new HashMap<>();
        data.put("pdf", pdf);
        data.put("url", result.get("url").toString());

        return data;
    }

    public InputStreamResource telechargerPdf(String url) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream inputStream = entity.getContent();

            return new InputStreamResource(inputStream);
        }

        return null;
    }
}
