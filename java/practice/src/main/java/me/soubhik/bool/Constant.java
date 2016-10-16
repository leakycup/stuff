package me.soubhik.bool;

/**
 * Created by soubhik on 16-10-2016.
 */
public class Constant implements Expression {
    public static Constant TRUE = new Constant(true);
    public static Constant FALSE = new Constant(false);

    private final boolean value;

    private Constant(boolean value) {
        this.value = value;
    }

    public void toInfix(StringBuilder builder) {
        String stringValue = (value) ? "T" : "F";
        builder.append(stringValue);
    }
}
