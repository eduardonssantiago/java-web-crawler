import org.jsoup.nodes.Document;

public class CrawlPipeline {

    Fetcher fetcher = new Fetcher();
    ContentCleaner clean = new ContentCleaner();
    Normalizer normalizer = new Normalizer();
    JsonStorage jsonStorage = new JsonStorage();

    public void execute(String url){
        Document document = fetcher.fetch(url);
        if(document == null){
            return;
        }
        clean.clean(document);
        PageContent pageContent = normalizer.transformInPage(document);
        jsonStorage.save(pageContent);
    }
}
