package me.soubhik.bool;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by soubhik on 16-10-2016.
 * a compact representation of a set of assignments to boolean variables.
 * in the following discussion, T: true, F: false, X: don't care.
 * (a=T, b=X, c=X) is represented as a collection of a single map {(a --> T)}
 * (a=T, b=X, c=X), (a=X, b=F, c=X) is represented as a collection of 2 maps {(a --> T)}, {(b --> F)}
 * (a=T, b=F, c=X) is represented as a collection of a single map {(a --> T), (b --> F)}
 * (a=X, b=X, c=X) is represented as a collection of a single empty map {}
 */
public class AssignmentSet {
    public static final Map<String, Boolean> ALL_X = Collections.emptyMap(); //all variables are don't care

    private final Set<Map<String, Boolean>> maps;

    private AssignmentSet() {
        this.maps = new HashSet<>();
    }

    private AssignmentSet(Collection<Map<String, Boolean>> maps) {
        this.maps = new HashSet<>(maps);
    }

    public AssignmentSet union(AssignmentSet that) {
        AssignmentSet result = new AssignmentSet();
        result.maps.addAll(this.maps);
        result.maps.addAll(that.maps);

        return result;
    }

    public AssignmentSet intersect(AssignmentSet that) {
        AssignmentSet result = new AssignmentSet();

        for (Map<String, Boolean> map1: this.maps) {
            for (Map<String, Boolean> map2: that.maps) {
                Map<String, Boolean> intersection = intersect(map1, map2);
                if (intersection != null) {
                    result.maps.add(intersection);
                }
            }
        }

        return result;
    }

    public boolean isEmpty() {
        return maps.isEmpty();
    }

    private static Map<String, Boolean> intersect(Map<String, Boolean> first, Map<String, Boolean> second) {
        Map<String, Boolean> result = new HashMap<>(first);

        for (String variable: second.keySet()) {
            if (result.containsKey(variable)) {
                Boolean value = result.get(variable);
                if (!value.equals(second.get(variable))) {
                    //conflict: empty intersection
                    return null;
                }
            } else {
                result.put(variable, second.get(variable));
            }
        }

        return result;
    }

    public static class Builder {
        private final Set<Map<String, Boolean>> maps;

        public Builder() {
            maps = new HashSet<>();
        }

        public Builder add(Map<String, Boolean> map) {
            maps.add(Collections.unmodifiableMap(map));
            return this;
        }

        public AssignmentSet build() {
            return new AssignmentSet(maps);
        }
    }
}
