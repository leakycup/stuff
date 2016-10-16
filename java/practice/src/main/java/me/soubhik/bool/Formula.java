package me.soubhik.bool;

import me.soubhik.GeoDatabase.DataLoader;

import java.text.ParseException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Created by soubhik on 16-10-2016.
 */
public class Formula {
    private final Expression root;

    public Formula(Expression root) {
        this.root = root;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        root.toInfix(builder);
        return builder.toString();
    }

    public void print() {
        System.out.println(this.toString());
    }

    public boolean isTautology() {
        return root.falseAssignments().isEmpty();
    }

    public boolean isContradiction() {
        return root.truthAssignments().isEmpty();
    }

    public boolean isSatisfiable() {
        return !isContradiction();
    }

    public static class Builder {
        private static final char OP_OR = '|';
        private static final char OP_AND = '&';
        private static final char OP_NEGATION = '!';
        private static final char LEFT_PARENTHESIS = '(';
        private static final char RIGHT_PARENTHESIS = ')';
        private static final char CONSTANT_TRUE = 'T';
        private static final char CONSTANT_FALSE = 'F';

        private static final Set<Character> BINARY_OPERATORS;
        private static final Set<Character> UNARY_OPERATORS;
        private static final Set<Character> CONSTANT_VALUES;

        static {
            BINARY_OPERATORS = new HashSet<>();
            BINARY_OPERATORS.add(OP_OR);
            BINARY_OPERATORS.add(OP_AND);

            UNARY_OPERATORS = new HashSet<>();
            UNARY_OPERATORS.add(OP_NEGATION);

            CONSTANT_VALUES = new HashSet<>();
            CONSTANT_VALUES.add(CONSTANT_TRUE);
            CONSTANT_VALUES.add(CONSTANT_FALSE);
        }

        private final String expression;

        public Builder(String expression){
            if (expression == null) {
                throw new IllegalArgumentException("null expression");
            }
            this.expression = expression;
        }

        public Formula build() {
            Expression root = parse();
            return new Formula(root);
        }

        // shunting yard algorithm to parse an infix boolean expression into a binary expression tree.
        // see https://en.wikipedia.org/wiki/Shunting-yard_algorithm .
        // operators:
        //  unary: ! (negation)
        //  binary: | (OR) and & (AND)
        // precedence: unary > binary, binary operators have same precedence, are left associative.
        // parentheses can be used to enforce associativity.
        // single letter variable names, whitespaces ignored.
        // constants: T for true and F for false.
        private Expression parse() {
            Stack<Character> operators = new Stack<>();
            Stack<Expression> operands = new Stack<>();
            Map<String, Variable> variables = new HashMap<>();

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (Character.isWhitespace(c)) {
                    continue;
                }

                if (CONSTANT_VALUES.contains(c)) {
                    Expression e = toConstant(c);
                    operands.push(e);
                    continue;
                }

                if (isVariableName(c)) {
                    String variableName = Character.toString(c);
                    if (!variables.containsKey(variableName)) {
                        variables.put(variableName, new Variable(variableName));
                    }
                    Expression e = variables.get(variableName);
                    operands.push(e);
                    continue;
                }

                if ((c == LEFT_PARENTHESIS) || (UNARY_OPERATORS.contains(c))) {
                    operators.push(c);
                    continue;
                }

                if (BINARY_OPERATORS.contains(c)) {
                    char lastOperator;
                    while (!operators.empty() && ((lastOperator = operators.peek()) != LEFT_PARENTHESIS)) {
                        if (UNARY_OPERATORS.contains(lastOperator)) {
                            consumeUnary(operands, operators);
                        } else if (BINARY_OPERATORS.contains(lastOperator)) {
                            consumeBinary(operands, operators);
                        } else {
                            throw new RuntimeException("Unexpected operator on stack: " + lastOperator +
                                    ". Error parsing expression: " + expression);
                        }
                    }
                    operators.push(c);
                    continue;
                }

                if (c == RIGHT_PARENTHESIS) {
                    char lastOperator;
                    while (!operators.empty() && ((lastOperator = operators.peek()) != LEFT_PARENTHESIS)) {
                        if (UNARY_OPERATORS.contains(lastOperator)) {
                            consumeUnary(operands, operators);
                        } else if (BINARY_OPERATORS.contains(lastOperator)) {
                            consumeBinary(operands, operators);
                        } else {
                            throw new RuntimeException("Unexpected operator on stack: " + lastOperator +
                                    ". Error parsing expression: " + expression);
                        }
                    }
                    if (operators.empty() || ((lastOperator = operators.pop()) != LEFT_PARENTHESIS)) {
                        throw new IllegalArgumentException("Bad boolean expression: " + expression);
                    }
                    continue;
                }

                throw new IllegalArgumentException("Unexpected character: " + c +
                        ". Bad boolean expression: " + expression);
            }

            while (!operators.empty()) {
                char operator = operators.peek();
                if (UNARY_OPERATORS.contains(operator)) {
                    consumeUnary(operands, operators);
                } else if (BINARY_OPERATORS.contains(operator)) {
                    consumeBinary(operands, operators);
                } else {
                    throw new IllegalArgumentException("Unexpected operator on stack: " + operator +
                            ". Error parsing expression: " + expression);
                }
            }

            if (operands.size() != 1) {
                throw new IllegalArgumentException("Bad boolean expression: " + expression);
            }

            return operands.pop();
        }

        private static boolean isVariableName(char c) {
            return Character.isLetter(c);
        }

        private Constant toConstant(char c) {
            if (c == CONSTANT_TRUE) {
                return Constant.TRUE;
            } else {
                return Constant.FALSE;
            }
        }

        private void consumeUnary(Stack<Expression> operands, Stack<Character> operators) {
            char lastOperator = operators.pop();
            if (lastOperator == OP_NEGATION) {
                Expression operand;
                try {
                    operand = operands.pop();
                } catch (EmptyStackException e) {
                    throw new IllegalArgumentException("Bad boolean expression: " + expression, e);
                }
                Expression e = new UnaryOperatorExpression(UnaryOperatorExpression.Operator.NEGATE, operand);
                operands.push(e);
            } else {
                throw new IllegalArgumentException("Bad boolean expression: " + expression);
            }
        }

        private void consumeBinary(Stack<Expression> operands, Stack<Character> operators) {
            char lastOperator = operators.pop();
            Expression right, left;
            try {
                right = operands.pop();
                left = operands.pop();
            } catch (EmptyStackException e) {
                throw new IllegalArgumentException("Bad boolean expression: " + expression, e);
            }
            BinaryOperatorExpression.Operator operator = (lastOperator == OP_OR) ?
                    BinaryOperatorExpression.Operator.OR : BinaryOperatorExpression.Operator.AND;
            Expression e = new BinaryOperatorExpression(operator, left, right);
            operands.push(e);
        }
    }

    private static void testFormulaParsing(String formula, String expected) {
        //parse the given formula, compare with expected
        System.out.println("input: " + formula);
        String actual = new Formula.Builder(formula).build().toString();
        System.out.println("parsed: " + actual);
        assert expected.equals(actual);

        //parse the parsed formula, compare with itself
        System.out.println("input: " + expected);
        actual = new Formula.Builder(expected).build().toString();
        System.out.println("parsed: " + actual);
        assert expected.equals(actual);
    }

    private static void testFormulaParsing(String formula) {
        System.out.println("expected: " + formula);
        System.out.print("actual: ");
        new Formula.Builder(formula).build().print();
        //test manually. extra parenthesis in parsed output ok.
    }

    private static void testFormulaProperties(String formula, boolean isTautology, boolean isContradiction,
                                              boolean isSatisfiable) {
        System.out.println(formula + ": tautology==" + isTautology + ", contradiction==" + isContradiction +
                ", satisfiable==" + isSatisfiable);
        Formula actual = new Formula.Builder(formula).build();
        assert (actual.isTautology() == isTautology);
        assert (actual.isContradiction() == isContradiction);
        assert (actual.isSatisfiable() == isSatisfiable);
    }

    public static void main(String[] args) {
        System.out.println("===================================");
        System.out.println("Testing formula parsing.");
        System.out.println("===================================");
        testFormulaParsing("a", "a");
        testFormulaParsing("!a", "!a");
        testFormulaParsing("a|b", "(a | b)");
        testFormulaParsing("a | b", "(a | b)");
        testFormulaParsing("a | !b", "(a | !b)");
        testFormulaParsing("a | !!!b", "(a | !!!b)");
        testFormulaParsing("!!a | !!!b", "(!!a | !!!b)");
        testFormulaParsing("(a)", "a");
        testFormulaParsing("(a | a)", "(a | a)");
        testFormulaParsing("(a | b)", "(a | b)");
        testFormulaParsing("(a | b) | c", "((a | b) | c)");
        testFormulaParsing("(a | b) & c", "((a | b) & c)");
        testFormulaParsing("a | (b | c)", "(a | (b | c))");
        testFormulaParsing("a | (b & c)", "(a | (b & c))");
        testFormulaParsing("a | (b & !c)", "(a | (b & !c))");
        testFormulaParsing("a | (!b & c)", "(a | (!b & c))");
        testFormulaParsing("a | (!b & !c)", "(a | (!b & !c))");
        testFormulaParsing("a | (!b & !!c)", "(a | (!b & !!c))");
        testFormulaParsing("a | !(!b & !!c)", "(a | !(!b & !!c))");
        testFormulaParsing("a | b | c", "((a | b) | c)");
        testFormulaParsing("a | b & c", "((a | b) & c)");
        testFormulaParsing("a | !b & c", "((a | !b) & c)");
        testFormulaParsing("a | !b & c & d", "(((a | !b) & c) & d)");
        testFormulaParsing("(a | !b) & c & d", "(((a | !b) & c) & d)");
        testFormulaParsing("a | (!b & c) & d", "((a | (!b & c)) & d)");
        testFormulaParsing("a | !b & (c & d)", "((a | !b) & (c & d))");
        testFormulaParsing("a | (!b & c & d)", "(a | ((!b & c) & d))");
        testFormulaParsing("a | (!b & (c & d))", "(a | (!b & (c & d)))");
        testFormulaParsing("a | (!b & ((((c & d)))))", "(a | (!b & (c & d)))");
        testFormulaParsing("(a | b) & (a | (c & d & !(d | e & (f | (b & !(a | c))))))",
                "((a | b) & (a | ((c & d) & !((d | e) & (f | (b & !(a | c)))))))");
        testFormulaParsing("T", "T");
        testFormulaParsing("F", "F");
        testFormulaParsing("(T|F)", "(T | F)");
        testFormulaParsing("a |F & !(b|T)", "((a | F) & !(b | T))");
        System.out.println("Formula parsing tests passed.");

        System.out.println("===================================");
        System.out.println("Testing formula properties.");
        System.out.println("===================================");
        testFormulaProperties("T", true, false, true);
        testFormulaProperties("F", false, true, false);
        testFormulaProperties("T|F", true, false, true);
        testFormulaProperties("T&F", false, true, false);
        testFormulaProperties("!F", true, false, true);
        testFormulaProperties("a", false, false, true);
        testFormulaProperties("a|b", false, false, true);
        testFormulaProperties("a|!a", true, false, true);
        testFormulaProperties("a&!a", false, true, false);
        testFormulaProperties("a & b", false, false, true);
        testFormulaProperties("a & (b | c)", false, false, true);
        testFormulaProperties("!a & !b", false, false, true);
        testFormulaProperties(" (a & (!b | b)) | (!a & (!b | b))", true, false, true);
        System.out.println("Formula properties tests passed.");
    }
}
