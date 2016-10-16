package me.soubhik.bool;

import java.util.Collections;
import java.util.Map;

/**
 * Created by soubhik on 16-10-2016.
 */
public class Constant extends Expression {
    public static Constant TRUE = new Constant(true);
    public static Constant FALSE = new Constant(false);

    private final boolean value;

    private Constant(boolean value) {
        super(getTruthAssignments(value), getFalseAssignments(value));
        this.value = value;
    }

    @Override
    public void toInfix(StringBuilder builder) {
        String stringValue = (value) ? "T" : "F";
        builder.append(stringValue);
    }

    private static AssignmentSet getTruthAssignments(boolean value) {
        if (value) {
            return new AssignmentSet.Builder().add(AssignmentSet.ALL_X).build();
        } else {
            return new AssignmentSet.Builder().build();
        }
    }

    private static AssignmentSet getFalseAssignments(boolean value) {
        if (value) {
            return new AssignmentSet.Builder().build();
        } else {
            return new AssignmentSet.Builder().add(AssignmentSet.ALL_X).build();
        }
    }
}
