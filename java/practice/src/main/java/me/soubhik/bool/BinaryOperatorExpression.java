package me.soubhik.bool;

/**
 * Created by soubhik on 16-10-2016.
 */
public class BinaryOperatorExpression implements Expression {
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
}
