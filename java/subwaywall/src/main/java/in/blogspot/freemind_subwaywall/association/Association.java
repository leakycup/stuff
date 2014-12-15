package in.blogspot.freemind_subwaywall.association;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
// a class to associate appples and oranges
// enjoy the LISP inflicted parentheses in java 8 lambda syntax
public class Association <X,Y> {
public enum Move {
moveFirst,
moveSecond
}
public static class Pair<T,U> {
final T first;
final U second;
public Pair(T first, U second) {
this.first = first;
this.second = second;
}
}
List<X> first;
List<Y> second;
BiFunction<X,Y,Move> nextStepFunction;
BiPredicate<X,Y> associationPredicate;
Association(List<X> first, List<Y> second, BiFunction<X,Y,Move> nextStepFunction, BiPredicate<X,Y> associationPredicate) {
this.first = first;
this.second = second;
this.nextStepFunction = nextStepFunction;
this.associationPredicate = associationPredicate;
}
List<Pair<X,Y>> associate() {
List<Pair<X,Y>> resultList = new ArrayList<>();
for (int i=0, j=0; i < first.size() && j < second.size(); ) {
X firstElement = first.get(i);
Y secondElement = second.get(j);
if (associationPredicate.test(firstElement, secondElement)) {
resultList.add(new Pair<X,Y>(firstElement, secondElement));
}
if (nextStepFunction.apply(firstElement, secondElement) == Move.moveFirst) {
i++;
} else {
j++;
}
}
return (resultList);
}
public static void main(String[] args) {
List<Integer> integerList = new ArrayList<>();
List<Double> doubleList = new ArrayList<>();
integerList.add(2);
integerList.add(6);
integerList.add(3);
integerList.add(9);
Collections.sort(integerList);
doubleList.add(2.1);
doubleList.add(3.1);
doubleList.add(4.5);
Collections.sort(doubleList);
final double delta = 0.5;
Association<Integer, Double> myAssociation = new Association<>(integerList, doubleList, ((i,d) -> ((double)i <= d) ? Move.moveFirst : Move.moveSecond), ((i,d) -> (Math.abs(d - (double)i) < delta)));
List<Pair<Integer, Double>> associattionList = myAssociation.associate();
for (Pair<Integer, Double> pair: associattionList) {
int i = pair.first;
double d = pair.second;
System.out.println("Integer: " + i + ", Double: " + d);
}
}
}
