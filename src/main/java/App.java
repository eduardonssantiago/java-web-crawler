import java.util.ArrayList;
import java.util.Scanner;

public class App {

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
        CrawlPipeline crawlPipeline = new CrawlPipeline();
        ArrayList<String > websites = new ArrayList<>();

        for(int i = 0; i < 3; i++) {
            System.out.println("Website: ");
            String website = scanner.nextLine();
            websites.add(website);
        }

        for(int i = 0; i < 3; i++) {
            crawlPipeline.execute(websites.get(i));
        }
    }
}
