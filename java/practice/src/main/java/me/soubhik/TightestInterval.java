package me.soubhik;

/**
 * Created by sb8 on 9/23/18.
 */
public class TightestInterval {
    private static class Interval {
        final int start; //inclusive
        final int end; //exclusive

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        boolean overlaps(Interval that) {
            return ((this.start < that.end) && (that.start < this.end));
        }

        boolean isLeft(Interval that) {
            return (that.start <= this.start);
        }
    }

    private static class BstForTightestInterval {
        private static class  SubInterval extends Interval {
            Interval tightest;

            SubInterval(int start, int end) {
                super(start, end);
            }
        }

        private static class Node {
            Node parent;
            Node left;
            Node right;

            SubInterval subInterval;
            int rightMostEnd;
        }

        Node root;

        Node findOverlapping(Interval interval) {
            Node node = root;
            while (node != null) {
                if (node.subInterval.overlaps(interval)) {
                    return node;
                }
                if (node.subInterval.isLeft(interval)) {
                    node = node.left;
                } else {
                    node = node.right;
                }
            }

            return null;
        }

        Interval findTightest(int point) {
            Interval interval = new Interval(point, point);
            Node overlapping = findOverlapping(interval);
            if (overlapping == null) {
                return null;
            }

            return overlapping.subInterval.tightest;
        }
    }
}
