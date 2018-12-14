package me.soubhik.careercup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by soubhik on 14-12-2018.
 * from https://www.careercup.com/question?id=5745995036491776
 */
public class EvaluateExpression {
    private static abstract class Node {
        Node left;
        Node right;
    }
    private static class Operator extends Node {
        char op;
        Operator (char op) {
            super();
            this.op = op;
        }
    }
    private static class Operand extends Node {
        int value;
        Operand(int value) {
            super();
            this.value = value;
        }
    }

    Map<Character, Integer> idToValue;

    public EvaluateExpression(List<String> assignments) {
        this.idToValue = new HashMap<>();
        for (String assignment: assignments) {
            assignment = assignment.replaceAll(" ", "");
            String[] parts = assignment.split("=");
            char id = parts[0].charAt(0);
            int value = Integer.parseInt(parts[1]);
            idToValue.put(id, value);
        }
    }

    private static int skipSpaces(String expression, int i) {
        for (; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c != ' ') {
                break;
            }
        }

        return i;
    }

    public int parseExpression(String expression) {
        //represent the expression as a list of operands and operators
        Node head = null;
        Node tail = null;
        boolean expectOperand = true;
        for (int i = skipSpaces(expression, 0); i < expression.length(); i++, i = skipSpaces(expression, i)) {
            char c = expression.charAt(i);
            if (expectOperand) {
                if (!idToValue.containsKey(c)) {
                    throw new IllegalArgumentException();
                }
                int value = idToValue.get(c);
                Node operand = new Operand(value);
                if (head == null) {
                    head = operand;
                    tail = operand;
                } else {
                    operand.left = tail;
                    tail.right = operand;
                    tail = operand;
                }
                expectOperand = false;
            } else {
                if ((c != '+') && (c != '-') && (c != '*') && (c != '/')) {
                    throw new IllegalArgumentException();
                }
                Node operator = new Operator(c);
                operator.left = tail;
                tail.right = operator;
                tail = operator;
                expectOperand = true;
            }
        }

        if (expectOperand) {
            throw new IllegalArgumentException("compilation error");
        }

        //evaluate all * and /
        Set<Character> operatorsToEvaluate = new HashSet<>();
        operatorsToEvaluate.add('/');
        operatorsToEvaluate.add('*');
        head = evaluate(head, operatorsToEvaluate);

        //evaluare all + and -
        operatorsToEvaluate.remove('/');
        operatorsToEvaluate.remove('*');
        operatorsToEvaluate.add('+');
        operatorsToEvaluate.add('-');
        head = evaluate(head, operatorsToEvaluate);

        return ((Operand)head).value;
    }

    private int evaluate(Operator operator) {
        int operand1 = ((Operand)operator.left).value;
        int operand2 = ((Operand)operator.right).value;
        char op = operator.op;
        int value;
        switch(op) {
            case '+': value = operand1 + operand2; break;
            case '-': value = operand1 - operand2; break;
            case '*': value = operand1 * operand2; break;
            case '/': value = operand1 / operand2; break;
            default:
                throw new IllegalArgumentException();
        }

        return value;
    }

    private Node evaluate(Node head, Set<Character> operatorsToEvaluate) {
        for (Node node = head; node != null; node = node.right) {
            if (!(node instanceof Operator)) {
                continue;
            }
            Operator operator = (Operator)node;
            if (!operatorsToEvaluate.contains(operator.op)) {
                continue;
            }
            int value = evaluate(operator);
            Operand operand = new Operand(value);
            operand.left = operator.left.left;
            operand.right = operator.right.right;
            if (operator.left.left != null) {
                operator.left.left.right = operand;
            } else {
                head = operand;
            }
            if (operator.right.right != null) {
                operator.right.right.left = operand;
            }
            node = operand;
        }

        return head;
    }

    private static void test(EvaluateExpression evaluator, String expression, int expected) {
        int actual = evaluator.parseExpression(expression);
        assert (actual == expected);
    }

    private static void test() {
        List<String> assignments = Arrays.asList("a = 3", "b=8", " c =5", "d = 6 ");
        EvaluateExpression evaluator = new EvaluateExpression(assignments);

        test(evaluator, "a + b + c", 16);
        test(evaluator, "a + b - c", 6);
        test(evaluator, "b - a * c", -7);
        test(evaluator, "d / a + c - b", -1);
        test(evaluator, "a + b * c - d", 37);
        test(evaluator, "a*b*c - d", 114);
        test(evaluator, "b", 8);
        test(evaluator, "b + b", 16);
        test(evaluator, "b / b", 1);
        test(evaluator, "b / b / b", 0);
        test(evaluator, "b / a / b", 0);
        test(evaluator, "b / a + d", 8);
        test(evaluator, "c-a", 2);
        test(evaluator, "c-a ", 2);
        test(evaluator, "c- a ", 2);
        test(evaluator, "c -a ", 2);
        test(evaluator, " c-a", 2);
        test(evaluator, " c -   a", 2);
    }

    public static void main(String[] args) {
        test();
    }
}
