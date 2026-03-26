import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.LocalDate;

public class JsonStorage {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save(PageContent pageContent) {
        String fileName = LocalDate.now() + ".json";
        Path directory = Path.of("output", pageContent.domain());
        Path filePath = directory.resolve(fileName);

        try {
            Files.createDirectories(directory);
            Files.writeString(filePath, gson.toJson(pageContent));
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + filePath);
            e.printStackTrace();
        }
    }
}