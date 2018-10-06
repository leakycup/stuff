from __future__ import print_function

import sys
from pyspark.sql import SparkSession

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: try <file>", file=sys.stderr)
        exit(-1)

    input_file = sys.argv[1]

    spark = SparkSession.builder.appName("PrintLines").getOrCreate()

    lines = spark.read.text(input_file)
    lines.foreach(print)

    spark.stop()
