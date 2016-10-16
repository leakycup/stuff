package me.soubhik.bool;

/**
 * Created by soubhik on 16-10-2016.
 */
public class UnaryOperatorExpression implements Expression {
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
        this.operator = operator;
        this.child = child;
    }

    public void toInfix(StringBuilder builder) {
        builder.append(operator);
        child.toInfix(builder);
    }
}
