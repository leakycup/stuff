package in.blogspot.freemind_subwaywall.scope_rules;

public class a {
    // fields with different scopes
    String aDefaultField = "default field";
    public String aPublicField = "public field";
    protected String aProtectedField = "protected field";
    private String aPrivateField = "private field";

    // methods with different scopes
    void aDefaultMethod() {
        System.out.println("method with default scope");
    }
    public void aPublicMethod() {
        System.out.println("method with public scope");
    }
    protected void aProtectedMethod() {
        System.out.println("method with protected scope");
    }
    private void aPrivateMethod() {
        System.out.println("method with private scope");
    }

    // static nested classes with different scopes
    static class aDefaultStaticNestedClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
    }
    public static class aPublicStaticNestedClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
        public void publicMethod () {
            System.out.println("public method in aPublicStaticNestedClass");
        }
    }
    protected static class aProtectedStaticNestedClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
    }
    private static class aPrivateStaticNestedClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
        public void publicMethod () {
            System.out.println("public method in aPrivateStaticNestedClass");
        }
    }

    // a private static nested class extends a public static nested class
    // and the derived class is empty
    private static class aPrivateStaticNestedClass_1 extends
        aPublicStaticNestedClass {
    }
    // a private static nested class extends a public static nested class
    // and is non-empty (overrides one public method of the super class)
    private static class aPrivateStaticNestedClass_2 extends
        aPublicStaticNestedClass {
        String defaultField = "default (in aPrivateStaticNestedClass_2)";
        public String publicField = "public (in aPrivateStaticNestedClass_2)";
        protected String protectedField = "protected (in aPrivateStaticNestedClass_2)";
        private String privateField = "private (in aPrivateStaticNestedClass_2)";
        public void publicMethod () {
            System.out.println("public method in aPrivateStaticNestedClass_2");
        }
    }
    // a public static nested class extends another public static nested class
    // and is non-empty (overrides one public method of the super class)
    public static class aPublicStaticNestedClass_1 extends
        aPublicStaticNestedClass {
        String defaultField = "default (in aPublicStaticNestedClass_1)";
        public String publicField = "public (in aPublicStaticNestedClass_1)";
        protected String protectedField = "protected (in aPublicStaticNestedClass_1)";
        private String privateField = "private (in aPublicStaticNestedClass_1)";
        public void publicMethod () {
            System.out.println("public method in aPublicStaticNestedClass_1");
        }
    }
    // a public static nested class extends a private static nested class
    // and the derived class is empty
    public static class aPublicStaticNestedClass_2 extends
        aPrivateStaticNestedClass {
    }

    // various instances of the static nested classes defined above
    public aDefaultStaticNestedClass a1 = new aDefaultStaticNestedClass();
    public aPublicStaticNestedClass a2 = new aPublicStaticNestedClass();
    public aProtectedStaticNestedClass a3 = new aProtectedStaticNestedClass();
    public aPrivateStaticNestedClass a4 = new aPrivateStaticNestedClass();
    public aPrivateStaticNestedClass_1 a5 = new aPrivateStaticNestedClass_1();
    public aPrivateStaticNestedClass_2 a6 = new aPrivateStaticNestedClass_2();
    public aPublicStaticNestedClass_1 a7 = new aPublicStaticNestedClass_1();
    public String a8 = a4.privateField;
    public aPublicStaticNestedClass_2 a9 = new aPublicStaticNestedClass_2();

    // access a private member of a static nested class
    public void accessPrivateMembers() {
        System.out.println(a1.privateField);
        System.out.println(a2.privateField);
        System.out.println(a3.privateField);
        System.out.println(a4.privateField);
        /*
         * COMPILATION FAILURE: javac bug? if the derived class is empty, access
         * to a private member of the super class is disallowed in the enclosing
         * class.
         * ok this makes sense for the following reason:
         * private member of a class is not accessible from a sub-class. however,
         * if the derived class has a member whose name is same as that of a
         * private member of the super class, the enclosing class can access the
         * homonymous member of the derived class.
        System.out.println(a5.privateField);
         */
        System.out.println(a6.privateField);
        System.out.println(a7.privateField);
        /*
         * COMPILATION FAILURE: javac bug? if the derived class is empty, access
         * to a private member of the super class is disallowed in the enclosing
         * class.
         * ok this makes sense for the following reason:
         * private member of a class is not accessible from a sub-class. however,
         * if the derived class has a member whose name is same as that of a
         * private member of the super class, the enclosing class can access the
         * homonymous member of the derived class.
        System.out.println(a9.privateField);
         */
    }

    //inner classes with various scopes
    class aDefaultInnerClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
    }
    public class aPublicInnerClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
    }
    protected class aProtectedInnerClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
    }
    private class aPrivateInnerClass {
        String defaultField = "default";
        public String publicField = "public";
        protected String protectedField = "protected";
        private String privateField = "private";
    }

    // various instances of the inner classes defined above
    aDefaultInnerClass a10 = new aDefaultInnerClass();
    aPublicInnerClass a11 = new aPublicInnerClass();
    aProtectedInnerClass a12 = new aProtectedInnerClass();
    aPrivateInnerClass a13 = new aPrivateInnerClass();
}
