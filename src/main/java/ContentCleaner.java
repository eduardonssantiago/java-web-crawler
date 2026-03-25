import org.jsoup.nodes.Document;

public class ContentCleaner {

    public Document clean(Document document){
        document.select(RemovableTags.SELECTORS).remove();
        document.select("[style]").removeAttr("style");

        return document;
    }
}
