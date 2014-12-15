package in.blogspot.freemind_subwaywall.generics;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        superclass o1 = new superclass();
        subclass1 o2 = new subclass1();
        subclass2 o3 = new subclass2();

        o1 = o2;
        o1 = o3;
        // COMPILATION FAILUREs
        //o2 = o1;
        //o2 = o3;

        generic1<superclass> o4 = new generic1<superclass>();
        generic1<subclass1> o5 = new generic1<subclass1>();
        generic1<Main> o6 = new generic1<Main>();
        // COMPILATION FAILUREs
        //o4 = o5;
        //o5 = o4;
        //o6 = o4;

        generic2<superclass> o7 = new generic2<superclass>();
        generic2<subclass1> o8 = new generic2<subclass1>();
        // COMPILATION FAILUREs
        //generic2<Main> o9 = new generic2<Main>();
        //o7 = o8;
        //o8 = o7;

        generic2<? extends superclass> o10 = o7;
        generic2<? extends superclass> o11 = o8;

        // COMPILATION FAILUREs
        //o7 = o10;
        //o8 = o11;

        o10 = o11;
        o11 = o10;

        o7.method1(new superclass());
        o7.method2();
        o7.method3(0);

        // COMPILATION FAILUREs
        //o8.method1(new superclass());
        o8.method1(new subclass1());
        o8.method2();
        o8.method3(0);

        // COMPILATION FAILUREs
        //o10.method1(new superclass());
        //o10.method1(o1);
        //o10.method1(o2);
        o10.method2();
        o10.method3(0);

        List<String> ls = new ArrayList<String>();
        ls.add(new String("foo"));
        ls.hashCode();
        ls.get(0);

        List<? extends Object> lo;
        lo = ls;
        lo.hashCode();
        lo.get(0);
        // COMPILATION FAILUREs
        //lo.add(new Object());
        //lo.add(new String());

        // COMPILATION FAILUREs
        //lo = new ArrayList<? extends Object>();

        lo = new ArrayList<Object>();
        // COMPILATION FAILUREs
        //lo.add(new Object());
        lo.hashCode();
        lo.get(0);
    }

    private static class superclass {
        void method3(int arg) {
        }
    }

    private static class subclass1 extends superclass {
    }

    private static class subclass2 extends superclass {
    }

    private static class generic1<superclass> {
        superclass f1;
        private void method1(superclass arg) {
        }
        private superclass method2() {
            return (f1);
        }
        private void method3(int arg) {
        }
    }

    private static class generic2<T extends superclass> {
        T f1;
        private void method1(T arg) {
        }
        private T method2() {
            return (f1);
        }
        private void method3(int arg) {
            f1.method3(arg);
        }
    }

    private static class generic3<T> {
        T f1;
        private void method1(T arg) {
        }
        private T method2() {
            return (f1);
        }
        private void method3(int arg) {
        }
    }
}
