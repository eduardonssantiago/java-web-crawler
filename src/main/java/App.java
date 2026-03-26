public class App {

    public static void main(String[] args){
        CrawlPipeline crawlPipeline = new CrawlPipeline();
        crawlPipeline.execute("http://info.cern.ch");
    }
}
