package org.cuelang.cue.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class NativeLibraryLoader {
    public static void loadLibcue() {
        String osName = System.getProperty("os.name").toLowerCase();

        String binaryName = switch (osName) {
            case String os when os.contains("mac") -> "darwin-arm64";
            case String os when os.contains("linux") -> "linux-amd64";
            default -> throw new IllegalStateException("Unknown Platform!");
        };

        try (InputStream lib = NativeLibraryLoader.class.getResourceAsStream(File.separator + binaryName)) {
            File tempFile = File.createTempFile("native_", "_" + binaryName);
            tempFile.deleteOnExit();
            Files.copy(lib, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.load(tempFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
