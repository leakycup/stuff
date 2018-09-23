package me.soubhik.GeoDatabase;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Map;

/**
 * Created by soubhik on 01-09-2016.
 */
public class StringToLatLongBingMap implements StringToLatLong {
    private static final int BUF_SIZE = 1024;
    private static final String COUNTRY_REGION = "countryRegion";
    private static final String ADDRESS_LINE = "addressLine";
    private static final String API_KEY = "key";
    private static final String USER_LOCATION = "userLocation";
    private static final String UNSUPPORTED_CHARS = ",.:-";

    private final AddressFieldExtractor extractor;
    private final String mapApiPrefix;
    private final String mapApiKey;
    private final String country;
    private final String encoding;
    private final LatLong seedLocation;

    StringToLatLongBingMap(AddressFieldExtractor extractor, String mapApiPrefix, String key,
                           String country, String encoding, LatLong seedLocation) {
        this.extractor = extractor;
        this.mapApiPrefix = mapApiPrefix;
        this.country = country;
        this.encoding = encoding;
        this.mapApiKey = key;
        this.seedLocation = seedLocation;
    }

    @Override
    public LatLong latLong(String addressLine) {
        Map<String, String> structuredFields = extractor.extract(addressLine);

        StringBuilder urlBuilder = new StringBuilder();

        urlBuilder.append(mapApiPrefix);
        urlBuilder.append(COUNTRY_REGION).append("=").append(country);
        for (Map.Entry<String, String> entry: structuredFields.entrySet()) {
            String field = entry.getKey();
            field = internalToBingFieldName(field);
            String value = entry.getValue();
            try {
                value = URLEncoder.encode(value, encoding);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
            urlBuilder.append("&").append(field).append("=").append(value);
        }

        try {
            addressLine = dropUnsupportedChars(addressLine);
            addressLine = URLEncoder.encode(addressLine, encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        urlBuilder.append("&").append(ADDRESS_LINE).append("=").append(addressLine);
        urlBuilder.append("&").append(API_KEY).append("=").append(mapApiKey);
        urlBuilder.append("&").append(USER_LOCATION).append("=").append(seedLocation.toString());

        URL url;
        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            return null;
        }

        Reader reader = null;
        String response;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            reader = new InputStreamReader(urlConnection.getInputStream());
            StringBuilder responseBuilder = new StringBuilder();
            char[] buffer = new char[BUF_SIZE];
            while (reader.read(buffer) > 0) {
                responseBuilder.append(buffer);
            }
            response = responseBuilder.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }

        boolean valid = validateResponse(response, structuredFields);
        if (!valid) {
            return null;
        }
        return responseToLatLong(response);
    }

    private boolean validateResponse(String response, Map<String, String> extractedFields) {
        //validate response against extracted fields like PIN, country.
        //stub for now.
        return true;
    }

    private LatLong responseToLatLong(String response) {
        JSONParser parser = new JSONParser();
        JSONObject topLevelResponse;
        try {
            topLevelResponse = (JSONObject)parser.parse(response);
        } catch (org.json.simple.parser.ParseException e) {
            return null;
        }
        int status = (Integer)topLevelResponse.get("statusCode");
        if (status != 200) {
            return null;
        }

        //write rest of json decoding logic here

        return null;
    }

    //identity function for now.
    private String internalToBingFieldName(String fieldName) {
        return fieldName;
    }

    private String dropUnsupportedChars(String s) {
        return s.replaceAll(UNSUPPORTED_CHARS, " ");
    }
}
