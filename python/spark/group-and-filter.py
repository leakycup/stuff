from __future__ import print_function

import sys
from pyspark.sql import SparkSession

def filter_words(t1, t2):
    filter_set = set(t2)
    filtered = []
    for s in t1:
        if s in filter_set:
            filtered.append(s)

    return tuple(filtered)


# 1. group strings from input file by their length
# 2. from each group, remove the strings that are not present in the filter file
# 3. drop the groups that have <= 1 members
# 4. output the remaining groups
# sample usage: ~/Applications/spark-2.3.2-bin-hadoop2.7/bin/spark-submit group-and-filter.py random-strings-variable-len-1.txt filter-file.txt
if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: spark-submit group-and-filter.py <input file> <filter file>", file=sys.stderr)
        exit(-1)

    input_file = sys.argv[1]
    filter_file = sys.argv[2]

    spark = SparkSession.builder.appName("GroupAndFilter").getOrCreate()

    lines = spark.read.text(input_file)
    grouped_lines = lines.rdd.map(lambda x: x[0]).groupBy(len)

    filters = spark.read.text(filter_file)
    filters = filters.rdd.map(lambda x: x[0]).groupBy(len)

    filtered = grouped_lines.join(filters).map(lambda (k, (v1, v2)): filter_words(v1, v2))
    filtered = filtered.filter(lambda x: len(x) > 1)

    filtered_df = filtered.toDF()
    filtered_df.explain(True)

# for debugging
    #grouped_lines.flatMap(lambda (x, y): y).foreach(print)
    #filters.foreach(print)
    #filtered.foreach(print)

    output = filtered.collect()
    for t in output:
        print(t)

    spark.stop()
