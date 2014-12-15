package in.blogspot.freemind_subwaywall.everything_else;

import java.util.List;
import java.util.ArrayList;

public class GenericMethodTest {
    static <T extends CharSequence>
        List<T> genericMethod(String input, Class<T> clazz) throws Exception {
            List<T> list = new ArrayList<T>();
            T item = clazz.getConstructor(String.class).newInstance(input);
            list.add(item);
            return (list);
        }

    public static void main(String[] args) {
        try {
            List<StringBuilder> list = genericMethod("foo", StringBuilder.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
