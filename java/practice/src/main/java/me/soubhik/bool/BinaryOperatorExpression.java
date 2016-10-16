package me.soubhik.bool;

/**
 * Created by soubhik on 16-10-2016.
 */
public class BinaryOperatorExpression extends Expression {
    public enum Operator {
        OR("|"),
        AND("&");

        private final String stringValue;
        Operator(String stringValue) {
            this.stringValue = stringValue;
        }

        public String toString() {
            return stringValue;
        }
    }

    private final Operator operator;
    private final Expression left;
    private final Expression right;

    public BinaryOperatorExpression(Operator operator, Expression left, Expression right) {
        super(getTruthAssignments(operator, left, right), getFalseAssignments(operator, left, right));
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public void toInfix(StringBuilder builder) {
        builder.append('(');
        left.toInfix(builder);
        builder.append(' ');
        builder.append(operator);
        builder.append(' ');
        right.toInfix(builder);
        builder.append(')');
    }

    private static AssignmentSet getTruthAssignments(Operator operator, Expression left, Expression right) {
        if (operator == Operator.OR) {
            return left.truthAssignments().union(right.truthAssignments());
        } else if (operator == Operator.AND) {
            return left.truthAssignments().intersect(right.truthAssignments());
        }

        throw new IllegalArgumentException("Operator: " + operator + "not supported");
    }

   private static AssignmentSet getFalseAssignments(Operator operator, Expression left, Expression right) {
       if (operator == Operator.OR) {
           return left.falseAssignments().intersect(right.falseAssignments());
       } else if (operator == Operator.AND) {
           return left.falseAssignments().union(right.falseAssignments());
       }

       throw new IllegalArgumentException("Operator: " + operator + "not supported");
   }
}
