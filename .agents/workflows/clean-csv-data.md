---
description: read a csv file and write a cleaned version of it
---

This workflow automates the process of cleaning a CSV data file based on specific criteria for suspicious values.

1.  **Prompt for Input File**: Ask the user for the absolute or relative path to the input CSV file that needs to be cleaned. Wait for their response. Do not proceed until you have the correct file path.

2.  **Read and Analyze Data**: Read the provided CSV file (the first row is the header). Spot suspicious values such as:
    *   Age, number of impressions, sales, or other inherently positive metrics being negative.
    *   Numeric values being extraordinarily high outliers relative to the rest of the column.
    *   Dates and numeric values that fail to parse correctly (e.g., text like "yesterday" in a date column or "twelve" in a numeric column).
    *   Obviously incorrect values for standard categories (e.g. incorrect US states).
    *   Unlikely values for text fields like names (e.g. "null", "empty", "enter name", "ID 123", "large blue tote bag", etc.).

3.  **Present Findings**: Present all identified suspicious values to the user in a tabular format detailing: the row number, the column(s) affected, and what the problem is, sorted by row number.

4.  **Wait for Confirmation**: Ask the user to confirm the findings. Do not write any code until the user confirms.

5.  **Generate Cleaning Script**: Once confirmed, write a Python script in the same folder as the input file. The python script should:
    *   Read the input file.
    *   Clean the data by blanking out (replacing with an empty string) the specific cells that contained suspicious values confirmed in the previous step.
    *   Write the cleaned output to a new CSV file in the same directory, named `<input-file-name-without-extension>-cleaned.csv`.
    *   Ensure the number and order of columns match the original file exactly.

6.  **Execute and Test**: Run the Python script to verify it executes successfully and manually read a few lines of the output file to confirm the data was accurately cleaned according to the plan.
