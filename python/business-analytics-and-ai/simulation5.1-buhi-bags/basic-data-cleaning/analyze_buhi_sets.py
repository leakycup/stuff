import pandas as pd

xl = pd.ExcelFile('buhi.xlsx')
jan = xl.parse('Jan sales')
feb = xl.parse('Feb sales')
mar = xl.parse('Mar sales')

jan_buyers = set(jan['Name'].dropna().unique())
feb_buyers = set(feb['Name'].dropna().unique())
mar_buyers = set(mar['Name'].dropna().unique())

union_jan_feb = jan_buyers.union(feb_buyers)

# 3. Buyers who made their first purchase in Mar
first_mar = mar_buyers - union_jan_feb

# 4. Buyers who purchased in Jan/Feb and returned in Mar
returning_mar = mar_buyers.intersection(union_jan_feb)

print(f"1. Unique buyers: Jan={len(jan_buyers)}, Feb={len(feb_buyers)}, Mar={len(mar_buyers)}")
print(f"2. Union of Jan and Feb: {len(union_jan_feb)}")
print(f"3. First purchase in Mar (Mar - Union): {len(first_mar)}")
if first_mar:
    print(f"   3 Names: {list(first_mar)[:3]}")
print(f"4. Returning in Mar (Mar INTERSECT Union): {len(returning_mar)}")
if returning_mar:
    print(f"   3 Names: {list(returning_mar)[:3]}")
