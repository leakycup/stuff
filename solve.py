import csv
from datetime import datetime

with open('q1_sales.csv', 'r', encoding='utf-8') as f:
    reader = csv.DictReader(f)
    
    # Store first purchase date for each customer
    first_purchases = {}
    customer_names = {}
    
    for row in reader:
        cust_id = row['ID']
        date_str = row['Date of Purchase']
        name = row['Name']
        
        # Parse the date
        try:
            dt = datetime.strptime(date_str, '%m/%d/%y')
        except ValueError:
            try:
                dt = datetime.strptime(date_str, '%m/%d/%Y')
            except ValueError:
                continue
                
        if cust_id not in first_purchases:
            first_purchases[cust_id] = dt
            customer_names[cust_id] = name
        else:
            if dt < first_purchases[cust_id]:
                first_purchases[cust_id] = dt
                customer_names[cust_id] = name

    jan_feb_first = []
    mar_first = []
    
    for cust_id, date in first_purchases.items():
        if date.month in [1, 2]:
            jan_feb_first.append(customer_names[cust_id])
        elif date.month == 3:
            mar_first.append(customer_names[cust_id])

    print("3 customers who made their first purchase in Jan/Feb:")
    for name in jan_feb_first[:3]:
        print(f"- {name}")
        
    print("\n3 customers who made their first purchase in March:")
    for name in mar_first[:3]:
        print(f"- {name}")
