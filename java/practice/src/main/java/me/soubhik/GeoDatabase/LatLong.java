package me.soubhik.GeoDatabase;

/**
 * Created by soubhik on 01-09-2016.
 */
public class LatLong {
    private final float latitude;
    private final float longitude;
    private final String stringRepresentation;

    public LatLong(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.stringRepresentation = Float.toString(latitude) + "," + Float.toString(longitude);
    }

    public float latitude() {
        return this.latitude;
    }

    public float longitude() {
        return this.longitude;
    }

    public String toString() {
        return this.stringRepresentation;
    }
}
