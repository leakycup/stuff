package in.blogspot.freemind_subwaywall.scope_rules;

public class b {
    public static void main(String[] args) {
        a aObj = new a();

        // access fields with various scopes from class 'a'
        System.out.println(aObj.aDefaultField);
        System.out.println(aObj.aPublicField);
        System.out.println(aObj.aProtectedField);
        /*
         * COMPILATION FAILURE
        System.out.println(aObj.aPrivateField);
         */

        // access method with various scopes from class 'a'
        aObj.aDefaultMethod();
        aObj.aPublicMethod();
        aObj.aProtectedMethod();
        /*
         * COMPILATION FAILURE
        aObj.aPrivateMethod();
         */

        // instantiate static nested classes defined in class 'a'
        // and access members of the static nested classes
        a.aDefaultStaticNestedClass a1 = new a.aDefaultStaticNestedClass();
        System.out.println(a1.defaultField);
        System.out.println(a1.publicField);
        System.out.println(a1.protectedField);
        /*
         * COMPILATION FAILURE
        System.out.println(a1.privateField);
         */

        a.aPublicStaticNestedClass a2 = new a.aPublicStaticNestedClass();
        System.out.println(a2.defaultField);
        System.out.println(a2.publicField);
        System.out.println(a2.protectedField);
        /*
         * COMPILATION FAILURE
        System.out.println(a2.privateField);
         */
        a.aProtectedStaticNestedClass a3 = new a.aProtectedStaticNestedClass();
        System.out.println(a3.defaultField);
        System.out.println(a3.publicField);
        System.out.println(a3.protectedField);
        /*
         * COMPILATION FAILURE
        System.out.println(a3.privateField);
         */
        /*
         * COMPILATION FAILURE
        a.aPrivateStaticNestedClass a4 = new a.aPrivateStaticNestedClass();
         */

        // access fields of class 'a' which are instances of static nested classes
        /*
         * COMPILATION FAILURE
        System.out.println(aObj.a4.publicField);
        System.out.println(aObj.a5.publicField);
         */
        a.aPublicStaticNestedClass a5 = aObj.a5;
        System.out.println(a5.defaultField);
        System.out.println(a5.publicField);
        System.out.println(a5.protectedField);
        /*
         * COMPILATION FAILURE
        System.out.println(a5.privateField);
         */
        a.aPublicStaticNestedClass a6 = aObj.a6;
        System.out.println(a6.defaultField);
        System.out.println(a6.publicField);
        System.out.println(a6.protectedField);
        a6.publicMethod();
        /*
         * COMPILATION FAILURE
        aObj.a6.publicMethod();
         */

        // test overriding between nested classes
        a.aPublicStaticNestedClass a7 = aObj.a7;
        System.out.println(a7.defaultField + " | " + aObj.a7.defaultField);
        System.out.println(a7.publicField + " | " + aObj.a7.publicField);
        System.out.println(a7.protectedField + " | " + aObj.a7.publicField);
        a7.publicMethod();
        aObj.a7.publicMethod();

        System.out.println(aObj.a8);

        aObj.a9.publicMethod();
    }
}

