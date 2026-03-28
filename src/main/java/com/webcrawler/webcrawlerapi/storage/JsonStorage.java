package com.webcrawler.webcrawlerapi.storage;

import com.google.gson.*;
import com.webcrawler.webcrawlerapi.model.PageContent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonStorage {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Instant.class,
                    (JsonSerializer<Instant>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(Instant.class,
                    (JsonDeserializer<Instant>) (json, typeOfJson, context) -> Instant.parse(json.getAsString()))
            .create();

    private final Map<UUID, Path> index = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Path OUTPUT_DIR = Path.of("output");
    private static final Path INDEX_FILE = OUTPUT_DIR.resolve("index.json");

    public JsonStorage() {
        loadIndex();
    }

    public void save(PageContent pageContent) {
        lock.writeLock().lock();
        try {
            String fileName = pageContent.uuid() + ".json";
            Path directory = Path.of("output", pageContent.domain());
            Path filePath = directory.resolve(fileName);

            Files.createDirectories(directory);
            Files.writeString(filePath, gson.toJson(pageContent));
            index.put(pageContent.uuid(), filePath);
            saveIndex();
        } catch (IOException e) {
            System.err.println("Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public PageContent get(UUID uuid) {
        lock.readLock().lock();
        try {
            Path filePath = index.get(uuid);
            if (filePath == null) return null;
            return readFile(filePath);
        } finally {
            lock.readLock().unlock();
        }
    }

    public PageContent delete(UUID uuid) {
        lock.writeLock().lock();
        try {
            Path filePath = index.get(uuid);
            if (filePath == null) return null;

            PageContent content = readFile(filePath);
            deleteFile(filePath);
            index.remove(uuid);
            saveIndex();
            return content;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean deleteAll() {
        lock.writeLock().lock();
        try {
            index.values().forEach(this::deleteFile);
            index.clear();
            saveIndex();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<PageContent> getAll() {
        lock.readLock().lock();
        try {
            return index.values().stream()
                    .map(this::readFile)
                    .filter(Objects::nonNull)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    private PageContent readFile(Path filePath) {
        try {
            return gson.fromJson(Files.readString(filePath), PageContent.class);
        } catch (IOException e) {
            System.err.println("Erro ao ler: " + filePath);
            return null;
        }
    }

    private void deleteFile(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Erro ao deletar: " + filePath);
        }
    }

    private void loadIndex() {
        if (!Files.exists(INDEX_FILE)) return;

        try {
            String json = Files.readString(INDEX_FILE);
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = gson.fromJson(json, type);

            for (Map.Entry<String, String> entry : map.entrySet()) {
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