package in.blogspot.freemind_subwaywall.everything_else;

public class InterfaceUnionTest {
    static interface BehaviorOne {
        int foo();
    }
    static interface BehaviorTwo {
        int bar();
    }
    static interface Union extends BehaviorOne, BehaviorTwo {
    }
    static class ImplementedUnion implements BehaviorOne, BehaviorTwo {
        int i, j;
        ImplementedUnion(int i, int j) {
            this.i = i;
            this.j = j;
        }
        public int foo() {
            return i;
        }
        public int bar() {
            return j;
        }
    }
    static class ImplementedUnionAgain extends ImplementedUnion implements Union {
        ImplementedUnionAgain(int i, int j) {
            super(i, j);
        }
    }

    static class IWantUnion {
        Union u;

        IWantUnion(Union u) {
            this.u = u;
        }

        int doSomeWork() {
            return (u.foo() + u.bar());
        }
    }

    public static void main(String[] args) {
        ImplementedUnion iu = new ImplementedUnion(5,6);
        //IWantUnion iwu = new IWantUnion(iu); //syntax error by javac
        //IWantUnion iwu = new IWantUnion((Union)iu); //ClassCastException
        ImplementedUnionAgain iua = new ImplementedUnionAgain(5,6);
        IWantUnion iwu = new IWantUnion(iua);
        System.out.println("doSomeWork: " + iwu.doSomeWork()); //works
    }
}
