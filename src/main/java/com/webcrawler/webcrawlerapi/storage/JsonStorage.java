package com.webcrawler.webcrawlerapi.storage;

import com.google.gson.*;
import com.webcrawler.webcrawlerapi.model.PageContent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class JsonStorage {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Instant.class,
                    (JsonSerializer<Instant>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(Instant.class,
                    (JsonDeserializer<Instant>) (json, typeOfJson, context) -> Instant.parse(json.getAsString()))
            .create();
    private final Map<UUID, Path> index = new HashMap<>();
    private static final Path OUTPUT_DIR = Path.of("output");
    private static final Path INDEX_FILE = OUTPUT_DIR.resolve("index.json");

    public JsonStorage() {
        loadIndex();
    }

    public void save(PageContent pageContent) {
        String fileName = pageContent.uuid() + ".json";
        Path directory = Path.of("output", pageContent.domain());
        Path filePath = directory.resolve(fileName);

        try {
            Files.createDirectories(directory);
            Files.writeString(filePath, gson.toJson(pageContent));
            index.put(pageContent.uuid(), filePath);
            saveIndex();
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + filePath);
            e.printStackTrace();
        }
    }

    public PageContent get(UUID uuid) {
        Path filePath = index.get(uuid);

        if(filePath == null) return null;

        try {
            String json = Files.readString(filePath);
            return gson.fromJson(json, PageContent.class);
        } catch (IOException e) {
            System.err.println("Erro ao ler: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    public PageContent delete(UUID uuid) {
        Path filePath = index.get(uuid);

        if(filePath == null) return null;

        try {
            String json = Files.readString(filePath);
            Files.deleteIfExists(filePath);
            index.remove(uuid);
            saveIndex();
            return gson.fromJson(json, PageContent.class);
        } catch (IOException e) {
            System.err.println("Erro ao deletar: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteAll() {
        for (Path filePath : index.values()) {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Erro ao deletar: " + filePath);
                e.printStackTrace();
            }
        }

        index.clear();
        saveIndex();
        return true;
    }


    public List<PageContent> getAll() {
        List<PageContent> results = new ArrayList<>();

        for (Path filePath : index.values()) {
            try {
                results.add(gson.fromJson(Files.readString(filePath), PageContent.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    private void loadIndex() {
        if(!Files.exists(INDEX_FILE)) return;

        try {
            String json = Files.readString(INDEX_FILE);

            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType();

            Map<String, String> map = gson.fromJson(json, type);

            for(Map.Entry<String, String> entry : map.entrySet()) {
                index.put(UUID.fromString(entry.getKey()), Path.of(entry.getValue()));
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler index: " + INDEX_FILE);
            e.printStackTrace();
        }
    }

    private void saveIndex() {
        try {
            Map<String, String> serializable = new HashMap<>();
            for (Map.Entry<UUID, Path> entry : index.entrySet()) {
                serializable.put(entry.getKey().toString(), entry.getValue().toString());
            }
            Files.createDirectories(OUTPUT_DIR);
            Files.writeString(INDEX_FILE, gson.toJson(serializable));
        } catch (IOException e) {
            System.err.println("Erro ao salvar índice: " + INDEX_FILE);
            e.printStackTrace();
        }
    }
}