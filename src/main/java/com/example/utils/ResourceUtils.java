package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceUtils {

    public static Properties loadPropertiesFromResources(String fileName) throws IOException {
        try (InputStream resourceAsStream = ResourceUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            if (resourceAsStream == null) {
                throw new IOException("File " + fileName + " not found");
            }
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            return properties;
        }
    }

    public static Stream<String> readStreamFromResources(String fileName) throws IOException {
        try {
            return Files.lines(Path.of(ResourceUtils.class.getClassLoader().getResource(fileName).toURI()));
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    public static String loadTextFileFromResources(String s) throws IOException {
        return readStreamFromResources(s).collect(Collectors.joining("\n"));
    }
}
