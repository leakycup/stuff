package me.soubhik.bool;

/**
 * Created by soubhik on 16-10-2016.
 */
public abstract class Expression {
    private final AssignmentSet truthAssignments, falseAssignments;

    protected Expression(AssignmentSet truthAssignments, AssignmentSet falseAssignments) {
        this.truthAssignments = truthAssignments;
        this.falseAssignments = falseAssignments;
    }

    public abstract void toInfix(StringBuilder builder);

    public AssignmentSet truthAssignments() {
        return truthAssignments;
    }

    public AssignmentSet falseAssignments() {
        return falseAssignments;
    }
}
