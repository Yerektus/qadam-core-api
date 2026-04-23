package io.yerektus.qadam.coreapi.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalStorageService {

    private final String basePath;

    public LocalStorageService(@Value("${storage.local.base-path}") String basePath) {
        this.basePath = basePath;
    }

    /**
     * Stores a file at {basePath}/{userId}/{documentId}/{fileName}.
     * Creates directories if they don't exist.
     *
     * @return absolute path of the stored file
     */
    public Mono<String> store(UUID userId, UUID documentId, String fileName, byte[] bytes) {
        return Mono.fromCallable(() -> {
            Path dir = Path.of(basePath, userId.toString(), documentId.toString());
            Files.createDirectories(dir);
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, bytes);
            return filePath.toAbsolutePath().toString();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Deletes a file at the given absolute path.
     */
    public Mono<Void> delete(String filePath) {
        return Mono.fromCallable(() -> {
            Files.deleteIfExists(Path.of(filePath));
            return (Void) null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
