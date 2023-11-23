package uk.ac.ed.inf.IO;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import java.net.URL;


public class RetrieveRestData {

    public RetrieveRestData() {
    }

    /**
     * Generic method that retrieves any list data type from REST.
     * Order - Retrieves orders for a particular date.
     * Restaurant - Retrieves available restaurants.
     * NoFlyZone - Retrieves no-fly zones.
     *
     * @param apiUrl The URL that the data is retrieved from.
     * @param dataClass The class of the data that is retrieved (Type.class).
     * @return An array of the data retrieved.
     * @param <T> Order, Restaurant, NamedRegion
     */
    public <T> T[] retrieveData(String apiUrl, String extension, Class<T> dataClass) {
        T[] data = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!apiUrl.endsWith("/")){
            apiUrl += "/";
        }

        try {
            URL url = new URL(apiUrl + extension);
            JavaType type = TypeFactory.defaultInstance().constructArrayType(dataClass);

            // Maps JSON lines to an array of the specified type
            data = mapper.readValue(url, type);
        } catch (Exception e) {
            System.err.println("Failed to obtain REST data for: " + dataClass);
            System.err.println("The error that occurred is: " + e);
            System.exit(1);
        }
        return data;
    }

        /**
         * Retrieves the central area.
         * @param apiUrl The URL of the Rest API.
         * @return A NamedRegion, if an error occurs, then null is returned.
         */
        public NamedRegion retrieveCentralArea(String apiUrl, String extension) {
            NamedRegion centralArea = null;

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            // Adds a slash to end of domain if it is not there.
            if (!apiUrl.endsWith("/")){
                apiUrl += "/";
            }

            // Tries to receive no-fly zones from REST API.
            try {
                URL url = new URL(apiUrl + extension);
                centralArea = mapper.readValue(url, NamedRegion.class);
            } catch (Exception e) {
                System.err.println("Failed to obtain REST data for: " + NamedRegion.class);
                System.err.println("The error that occurred is: " + e);
                System.exit(1);
            }

            return centralArea;
    }
}
