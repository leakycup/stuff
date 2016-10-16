package me.soubhik.bool;

/**
 * Created by soubhik on 16-10-2016.
 */
public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    public void toInfix(StringBuilder builder) {
        builder.append(name);
    }
}
