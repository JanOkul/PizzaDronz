package uk.ac.ed.inf.IO;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.io.IOException;
import java.net.URL;

/**
 * Retrieves input data from a REST Sever.
 */
public class RetrieveRestData {

    public RetrieveRestData() {
    }

    /**
     * Retrieves Order/Restaurant/NoFlyZone data from the REST API.
     *
     * @param apiUrl    Rest API URL.
     * @param urlPath   The url path to the website for different data types.
     * @param dataClass The class that the data retrieved should be mapped to.
     * @param <T>       Generic type as the procedure for getting data is the same for Order/Restaurant/NoFlyZone.
     * @return An array of REST API data.
     */
    public <T> T[] retrieveData(String apiUrl, String urlPath, Class<T> dataClass) throws IOException {
        T[] data;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }

        // Tries to retrieve data from REST API.
        try {
            URL url = new URL(apiUrl + urlPath);
            JavaType type = TypeFactory.defaultInstance().constructArrayType(dataClass);    // Creates an array type of the data class.
            data = objectMapper.readValue(url, type);
        } catch (Exception e) {
            throw new IOException("RetrieveRestData: Failed to obtain REST data for: " + dataClass);
            }

        return data;

    }

    /**
     * Retrieves the central area.
     *
     * @param apiUrl The URL of the Rest API.
     * @return The central area.
     */
    public NamedRegion retrieveCentralArea(String apiUrl, String extension) throws IOException {
        NamedRegion centralArea;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!apiUrl.endsWith("/")) {
            apiUrl += "/";
        }

        // Tries to receive no-fly zones from REST API.
        try {
            URL url = new URL(apiUrl + extension);
            centralArea = objectMapper.readValue(url, NamedRegion.class);
        } catch (Exception e) {
            throw new IOException("RetrieveRestData: Failed to obtain REST data for: " + NamedRegion.class);

        }

        return centralArea;
    }
}
