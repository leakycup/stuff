import pandas as pd
import sys

try:
    dfs = pd.read_excel('q1_data.xlsx', sheet_name=None)
except Exception as e:
    print(f"Error reading excel: {e}")
    sys.exit(1)

all_data = []
for sheet_name, df in dfs.items():
    if 'ID' in df.columns and 'Date of Purchase' in df.columns and 'Name' in df.columns:
        all_data.append(df[['ID', 'Name', 'Date of Purchase']])

if not all_data:
    print("No valid data found in sheets.")
    sys.exit(1)

combined_df = pd.concat(all_data, ignore_index=True)
combined_df['Date of Purchase'] = pd.to_datetime(combined_df['Date of Purchase'], errors='coerce')
combined_df = combined_df.dropna(subset=['Date of Purchase'])

combined_df = combined_df.sort_values('Date of Purchase')
first_purchases = combined_df.drop_duplicates(subset=['ID'], keep='first')

jan_feb = first_purchases[first_purchases['Date of Purchase'].dt.month.isin([1, 2])]['Name'].tolist()
mar = first_purchases[first_purchases['Date of Purchase'].dt.month == 3]['Name'].tolist()

print("3 customers who made their first purchase in Jan/Feb:")
for name in jan_feb[:3]:
    print(f"- {name}")

print("\n3 customers who made their first purchase in March:")
for name in mar[:3]:
    print(f"- {name}")
