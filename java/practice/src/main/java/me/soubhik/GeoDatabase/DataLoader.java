package me.soubhik.GeoDatabase;

import me.soubhik.IntegerEncoding;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by soubhik on 01-09-2016.
 */
public class DataLoader {
    /**
     * represents the status of a data load request.
     * the status is either a success, which implies all lines loaded successfully, OR
     * some lines (possibly all) lines failed to load.
     * success == true iff all lines were successfully loaded. otherwise,
     * overallCause if no line was loaded, this indicates the cause of the overall failure.
     * the Map<Integer, String> cause stores the cause of failure for each line number that failed to load.
     * line numbers start from 1.
     */
    public static class Status {
        boolean success;
        String overallCause;
        Map<Integer, String> cause;
    }
    public static final Status SUCCESS;
    static {
        SUCCESS = new Status();
        SUCCESS.success = true;
    }

    private final String charset;
    private final StringToLatLong stringToLatLong;
    private final Storage storage;

    public DataLoader(String charset, StringToLatLong stringToLatLong, Storage storage) {
        this.charset = charset;
        this.stringToLatLong = stringToLatLong;
        this.storage = storage;
    }

    public Status loadFromCSV(String fileName, String delimiter) {
        Status status = SUCCESS;

        int numFailures = 0;
        Map<Integer, String> cause = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), charset))) {
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(delimiter);
                if (parts.length < 2) {
                    numFailures++;
                    cause.put(lineNo, "Malformed line in CSV");
                    continue;
                }
                String id = parts[0];
                String address = parts[1];
                LatLong latLong = stringToLatLong.latLong(address);
                storage.insert(id, latLong);

                lineNo++;
            }
        } catch (IOException e) {
            Status fail = new Status();
            fail.success = false;
            fail.overallCause = e.toString();
            fail.cause = cause;
            return fail;
        }

        storage.build();

        if (numFailures > 0) {
            status = new Status();
            status.success = false;
            status.overallCause = "some lines failed to load";
            status.cause = cause;
        }
        return status;
    }
}
