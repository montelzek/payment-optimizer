package com.montelzek.paymentoptimizer.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> List<T> mapToPojo(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return objectMapper.readValue(file, new TypeReference<>() {});
    }

}
