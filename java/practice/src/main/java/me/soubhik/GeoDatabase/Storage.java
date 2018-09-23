package me.soubhik.GeoDatabase;

import java.util.Set;

/**
 * Created by soubhik on 01-09-2016.
 */
public interface Storage {
    public boolean insert(String key, LatLong latLong);
    public boolean build();
    public Set<String> find(String key, int radius);
}
