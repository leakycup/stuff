package me.soubhik.bool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by soubhik on 16-10-2016.
 */
public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        super(getTruthAssignments(name), getFalseAssignments(name));
        if (name == null) {
            throw new IllegalArgumentException("variable name can't be null");
        }
        this.name = name;
    }

    public void toInfix(StringBuilder builder) {
        builder.append(name);
    }

    private static AssignmentSet getTruthAssignments(String variable) {
        Map<String, Boolean> assignment = new HashMap<>();
        assignment.put(variable, true);
        return new AssignmentSet.Builder().add(assignment).build();
    }

    private static AssignmentSet getFalseAssignments(String variable) {
        Map<String, Boolean> assignment = new HashMap<>();
        assignment.put(variable, false);
        return new AssignmentSet.Builder().add(assignment).build();
    }
}
