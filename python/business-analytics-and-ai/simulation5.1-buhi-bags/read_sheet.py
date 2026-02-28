import pandas as pd
import urllib.request

url = "https://docs.google.com/spreadsheets/d/1eWvDjsAcer3ZJpRV7GTFswZ9qQf7BOvqqR7HZ6JlTpw/export?format=xlsx"
urllib.request.urlretrieve(url, "temp.xlsx")

xls = pd.ExcelFile("temp.xlsx")
for sheet in xls.sheet_names:
    print(f"Sheet: {sheet}")
    df = pd.read_excel("temp.xlsx", sheet_name=sheet)
    print(df.columns)
    if 'Organic Visits' in df.columns:
        print(f"Sum of Organic Visits: {df['Organic Visits'].sum()}")
