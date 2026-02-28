import pandas as pd
import numpy as np

customers = pd.read_csv('customer_data.csv')
print("--- Age ---")
print(customers['Age'].unique())

print("--- Annual Income ---")
print(customers['Annual Income'].unique())

print("--- Education ---")
print(customers['Education Level'].unique())

print("--- Checking for numeric outliers ---")
for col in ['Age', 'Annual Income', 'Annual Disposal']:
    # Try converting to numeric, coercing errors to NaN to see values that are parseable
    numeric_col = pd.to_numeric(customers[col], errors='coerce')
    print(f"\n{col} numeric stats:")
    print(numeric_col.describe())
    
    # Print non-numeric values
    non_numeric = customers[col][numeric_col.isna() & customers[col].notna()]
    if len(non_numeric) > 0:
        print(f"Non-numeric {col} values:")
        print(non_numeric.unique())

