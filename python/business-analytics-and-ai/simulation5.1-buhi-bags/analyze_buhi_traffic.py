import pandas as pd

try:
    xl = pd.ExcelFile('buhi.xlsx')
    traffic = xl.parse('traffic data')
    
    # Clean up the column if necessary (drop any NaNs and sum)
    total_organic_visits = traffic['Organic Visits'].sum()
    
    print(f"Total Organic Visits in Q1 2019: {total_organic_visits}")
except Exception as e:
    import traceback
    traceback.print_exc()

