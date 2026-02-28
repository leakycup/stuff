import pandas as pd
import os

url = "https://docs.google.com/spreadsheets/d/1usAR8z5IAsTFCjPaelbytIR7SKw7fia1cRKiMPVkehc/export?format=xlsx"

print(f"Downloading data from: {url}")
try:
    xls = pd.read_excel(url, sheet_name=None)
    for sheet_name, df in xls.items():
        # Create a safe filename
        safe_name = sheet_name.replace(" ", "_").lower()
        filename = f"{safe_name}.csv"
        print(f"Saving {sheet_name} to {filename}...")
        df.to_csv(filename, index=False)
    print("Download complete.")
except Exception as e:
    print(f"Error downloading or processing sheet: {e}")
