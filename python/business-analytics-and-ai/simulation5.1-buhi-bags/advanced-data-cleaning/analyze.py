import pandas as pd
import json

customers = pd.read_csv('customer_data.csv')
demographics = pd.read_csv('us_demographics_data.csv')

def get_stats(df, name):
    print(f"--- {name} ---")
    print("Shape:", df.shape)
    print("Columns:", df.columns.tolist())
    print("Missing values:")
    print(df.isnull().sum())
    print("\nHead:")
    print(df.head(3))
    print("\n")

get_stats(customers, "Customer Data")
get_stats(demographics, "US Demographics Data")

# Look at specific problems
print("--- Unique Genders ---")
if 'Gender' in customers.columns:
    print(customers['Gender'].unique())

print("--- Unique States ---")
if 'State' in customers.columns:
    print(customers['State'].unique())
    
print("--- Demographics Zip Codes ---")
if 'Zip code' in demographics.columns:
    print("Demos missing zips:", demographics['Zip code'].isnull().sum())
    print(demographics[['Zip code', 'State']].head())
elif 'Zip Code' in demographics.columns:
    print("Demos missing zips:", demographics['Zip Code'].isnull().sum())
    print(demographics[['Zip Code', 'State', 'Median Income']].head())

