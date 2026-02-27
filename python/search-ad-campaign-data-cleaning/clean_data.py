import csv
import os

input_file = "Q1-campaign-data.csv"
output_file = "Q1-campaign-data-cleaned.csv"

def is_suspicious_date(val):
    if not val or val.strip().lower() == 'null':
        return True
    parts = val.split('/')
    if len(parts) == 3:
        if parts[2] != '19':
            return True
    else:
        return True
    return False

def is_suspicious_numeric(val, col_name):
    # Remove $ and commas, spaces
    clean_val = val.replace('$', '').replace(',', '').strip()
    if clean_val.lower() == 'null' or clean_val == '':
        return val.lower() == 'null'
    
    try:
        num = float(clean_val)
    except ValueError:
        return True
    
    if num < 0:
        return True
        
    if "Budget" in col_name and num > 5000:
        return True
    if "Impressions" in col_name and (num < 10 or num > 30000):
        return True
    if "clicks" in col_name and num > 5000:
        return True
    if "Total Cost" in col_name and num > 10000:
        return True
    if "Conversions" in col_name and num > 1000:
        return True
    if "Total Revenue" in col_name and num > 50000:
        return True
    if "Cost Per Click" in col_name and num > 50:
        return True
        
    return False

def clean():
    # Make sure we're in the right directory
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    
    with open(input_file, mode='r', encoding='utf-8', newline='') as infile, \
         open(output_file, mode='w', encoding='utf-8', newline='') as outfile:
        
        # Read with tab delimiter as the original data seemed to be tab separated or comma-separated depending on the file
        # Actually wait, let's auto-detect dialect or assume comma.
        # Oh, in the file preview, it space-separated like: "ID	Ad Campaign Name"
        # Wait, the viewer showed tabs: `1: ID\tAd Campaign Name\tDate`
        # Wait, I should better treat it as CSV or check first line.
        # I will use csv.Sniffer() to detect comma or tab.
        
        sample = infile.read(1024)
        infile.seek(0)
        
        try:
            dialect = csv.Sniffer().sniff(sample)
        except:
            dialect = csv.excel
        
        reader = csv.reader(infile, dialect=dialect)
        writer = csv.writer(outfile, dialect=dialect)
        
        headers = next(reader)
        writer.writerow(headers)
        
        for row in reader:
            cleaned_row = list(row)
            
            # Date is index 2
            if len(cleaned_row) > 2 and is_suspicious_date(cleaned_row[2]):
                cleaned_row[2] = ""
                
            # Numeric fields: indices 3 to 9
            for i in range(3, min(10, len(cleaned_row))):
                col_name = headers[i]
                if is_suspicious_numeric(cleaned_row[i], col_name):
                    cleaned_row[i] = ""
                    
            writer.writerow(cleaned_row)

if __name__ == "__main__":
    clean()
    print(f"Data cleaned and saved to {output_file}")
