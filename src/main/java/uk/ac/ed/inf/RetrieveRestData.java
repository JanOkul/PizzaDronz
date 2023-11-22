package uk.ac.ed.inf;


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
     * @param api_url The URL that the data is retrieved from.
     * @param data_class The class of the data that is retrieved (Type.class).
     * @return An array of the data retrieved.
     * @param <T> Order, Restaurant, NamedRegion
     */
    public <T> T[] retrieveData(String api_url, String extension, Class<T> data_class) {
        T[] data = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        // Adds a slash to end of domain if it is not there.
        if (!api_url.endsWith("/")){
            api_url += "/";
        }

        try {
            URL url = new URL(api_url + extension);
            JavaType type = TypeFactory.defaultInstance().constructArrayType(data_class);

            // Maps JSON lines to an array of the specified type
            data = mapper.readValue(url, type);
        } catch (Exception e) {
            System.err.println("Failed to obtain REST data for: " + data_class);
            System.err.println("The error that occurred is: " + e);
            System.exit(1);
        }
        return data;
    }

        /**
         * Retrieves the central area.
         * @param api_url The URL of the Rest API.
         * @return A NamedRegion, if an error occurs, then null is returned.
         */
        public NamedRegion retrieveCentralArea(String api_url, String extension) {
            NamedRegion central_area = null;

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            // Adds a slash to end of domain if it is not there.
            if (!api_url.endsWith("/")){
                api_url += "/";
            }

            // Tries to receive no-fly zones from REST API.
            try {
                URL central_area_url = new URL(api_url + extension);
                central_area = mapper.readValue(central_area_url, NamedRegion.class);
            } catch (Exception e) {
                System.err.println("Failed to obtain REST data for: " + NamedRegion.class);
                System.err.println("The error that occurred is: " + e);
                System.exit(1);
            }

            return central_area;
    }
}
