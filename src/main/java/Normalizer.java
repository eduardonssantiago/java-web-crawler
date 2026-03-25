import org.jsoup.nodes.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.UUID;

public class Normalizer {

    public PageContent transformInPage(Document document) {
        Instant timeStamp = Instant.now();
        String domain;
        try {
            URI uri = new URI(document.location());
            domain = uri.getHost();
        } catch (URISyntaxException e) {
            domain = "unknown";
        }

        return new PageContent(UUID.randomUUID(),
                document.baseUri(),
                domain,
                document.title(),
                document.text(),
                document.select("html").attr("lang"),
                timeStamp);
    }
}
