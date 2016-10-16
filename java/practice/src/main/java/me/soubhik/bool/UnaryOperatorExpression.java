package me.soubhik.bool;

/**
 * Created by soubhik on 16-10-2016.
 */
public class UnaryOperatorExpression extends Expression {
    public enum Operator {
        NEGATE("!");

        private final String stringValue;
        Operator(String stringValue) {
            this.stringValue = stringValue;
        }

        public String toString() {
            return stringValue;
        }
    }

    private final Operator operator;
    private final Expression child;

    public UnaryOperatorExpression(Operator operator, Expression child) {
        super(getTruthAssignments(operator, child), getFalseAssignments(operator, child));
        this.operator = operator;
        this.child = child;
    }

    public void toInfix(StringBuilder builder) {
        builder.append(operator);
        child.toInfix(builder);
    }

    private static AssignmentSet getTruthAssignments(Operator operator, Expression child) {
        if (operator == Operator.NEGATE) {
            return child.falseAssignments();
        }

        throw new IllegalArgumentException("Operator: " + operator + " not yet supported");
    }

    private static AssignmentSet getFalseAssignments(Operator operator, Expression child) {
        if (operator == Operator.NEGATE) {
            return child.truthAssignments();
        }

        throw new IllegalArgumentException("Operator: " + operator + " not yet supported");
    }
}
