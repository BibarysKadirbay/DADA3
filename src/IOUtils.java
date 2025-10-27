import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class IOUtils {
    public static String readAllText(String path) throws IOException {
        return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
    }
    public static void writeAllText(String path, String text) throws IOException {
        Files.writeString(Paths.get(path), text, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}