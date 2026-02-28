import urllib.request
import pandas as pd
import traceback

try:
    url = 'https://docs.google.com/spreadsheets/d/1eWvDjsAcer3ZJpRV7GTFswZ9qQf7BOvqqR7HZ6JlTpw/export?format=xlsx'
    urllib.request.urlretrieve(url, 'buhi.xlsx')
    xl = pd.ExcelFile('buhi.xlsx')
    print("Sheet names:", xl.sheet_names)
    for sheet in xl.sheet_names:
        print(f"\n--- SHEET: {sheet} ---")
        df = xl.parse(sheet)
        print("Columns:", df.columns.tolist())
        print("First row:", df.iloc[0].to_dict() if len(df) > 0 else "Empty")
except Exception as e:
    traceback.print_exc()
