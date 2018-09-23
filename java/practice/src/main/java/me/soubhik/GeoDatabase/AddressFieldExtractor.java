package me.soubhik.GeoDatabase;

import java.util.Map;

/**
 * Created by soubhik on 01-09-2016.
 */
public interface AddressFieldExtractor {
    public Map<String, String> extract(String addressLine);
}
