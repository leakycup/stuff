package in.blogspot.freemind_subwaywall.everything_else;

public class KWayPartition<T> {
    private static interface Partitioner<T> {
        public int numPartitions();
        public int getPartition(T item);
    }

    private static class IntegerPartitioner implements Partitioner<Integer> {
        int[] endPoints;

        IntegerPartitioner(int[] endPoints) {
            this.endPoints = endPoints;
        }

        public int numPartitions() {
            return (endPoints.length + 1);
        }

        public int getPartition(Integer item) {
            int i;
            for (i = 0; i < endPoints.length; i++) {
                if (item < endPoints[i]) {
                    return (i);
                }
            }

            return (endPoints.length);
        }
    }

    private static class LSDRadixPartitioner implements Partitioner<Integer> {
        int x, y;

        //position can be 0, 1, 2, ... (from right or LSD)
        //position == 0 => least significant digit.
        LSDRadixPartitioner(int position) {
            x = 10;
            y = 1;

            while (position > 0) {
                x *= 10;
                y *= 10;
                position--;
            }
        }

        public int numPartitions() {
            return (10);
        }

        //returns the digit at 'position' as the key
        public int getPartition(Integer item) {
            return ((item % x) / y);
        }
    }

    private static class MSDRadixPartitioner implements Partitioner<Integer> {
        int min;
        int stop;

        //position can be 0, 1, 2, ... (from left or MSD)
        //position == 0 => most significant digit.
        MSDRadixPartitioner(int position) {
            min = 1;
            stop = 10;
            while (position > 0) {
                min *= 10;
                stop *= 10;
                position--;
            }
        }

        //10 partitions for 10 digits: 0, 1, ..., 9 and then another one if 
        //the given number does not have positon-th digit from left.
        public int numPartitions() {
            return (11);
        }

        //returns the (digit at 'position' (from left) + 1) as the key
        public int getPartition(Integer item) {
            if ((position == 0) && (item == 0)) {
                return (1);
            }
            //item does not have position-th digit from left.
            if (item < min) {
                return (0);
            }

            //keep dividing till the position-th digit is the LSD
            while (item >= stop) {
                item /= 10;
            }

            //extract LSD and return
            return ((item % 10) + 1);
        }
    }

    public int[] partition(ArrayList<T> a, Partitioner<T> partitioner) {
        int n = a.size();
        int k = partitioner.numPartitions();

        int[] size = new int[k]; //0-initialized by Java
        for (T item: a) {
            int p = partitioner.getPartition(item);
            size[p]++;
        }

        int[] startIndex = new int[k]; //0-initialized by Java
        for (p = 1; p < k; p++) {
            startIndex[p] = startIndex[p - 1] + size[p - 1]; //inclusive
        }

        int[] endIndex = new int[k];
        for (p = 0; p < (k - 1); p++) {
            endIndex[p] = starIndex[p + 1]; //exclusive
        }
        endIndex[k - 1] = n;

        int[] currentIndex = Arrays.copyOf(startIndex, k);

        int currentPartition = 0;
        //each iteration of this loop ensures element a[i] belongs to the current partition.
        for (int i = 0; i < n; i++) {
            //we're falling off the current partiion. also, skip the 0-length partitions
            //that follow.
            while (i == endIndex[currentPartition]) {
                currentPartition++;
            }
            T item = a[i];
            int p = partitioner.getPartition(item);
            if (p == currentPartition) {
                continue;
            }
            if (p < currentPartition) {
                //previous iterations of the loop has ensured all partitions that
                //precede the current partion are full.
                throw new RuntimeException("Internal error");
            }
            //each iteration ensures 'item' is moved to the partition it belongs to.
            while (p != currentPartition) { //O(n^2)
                T displaced = a[currentIndex[p]];
                int q = partitioner.getPartition(displaced);
                //find the first element in partiion p that does not belong to
                //partition p.
                while (q == p) {
                    currentIndex[p]++;
                    displaced = a[currentIndex[p]];
                    q = partitioner.getPartition(displaced);
                }
                a[currentIndex[p]] = item;
                currentIndex[p]++;
                item = displaced;
                p = partitioner.getPartition(item);
            }
            a[i] = item;
            //NOTE:
            //this is a loop inside a loop. 
            //the outer loop iterates n times, one to find destination for each element of the input array.
            //the inner loop can iterate (n - 1) times in the worst case: e.g. if number of partitions is 2 and all but the last element of the 2nd partition does not belong to the 2nd partition.
            //thus, the algo is O(n^2).
            //some optimization can be done to ensure we have O(n) swaps though the number of comparisions still remains O(n^2):
            //we can ensure that 'displaced' is an element that does not belong to partition 'p'. this means that for each 'out-of-place' element in input array, there's exactly one swap.
            //current code actually does O(n) swaps and O(n^2) comparisons using the above idea.
            //LSDRadixPartitioner returns i-th element from right, which is suitable for LSD radix sort and unsuitable for an MSD radix sort. for MSD radix sort, we need a partitioner that returns i-th element from left.
            //MSDRadixPartitioner can be used to implement a 10-way partition suitable for an in-place MSD Radix sort (American National flag sort: http://en.wikipedia.org/wiki/American_flag_sort). however, the case where an item does not have position-th digit from left needs to be special handled in partition().
        }

        return (endIndex);
    }
}
