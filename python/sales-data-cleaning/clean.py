"""
Sales Data Cleaner
Reads mar-sales-data.csv, replaces suspicious/erroneous values with blanks,
and writes the result to mar-sales-data-cleaned.csv.

Row numbers below are 1-indexed (row 1 = header, row 2 = first data row).
"""

import csv
import os

INPUT_FILE  = os.path.join(os.path.dirname(__file__), "mar-sales-data.csv")
OUTPUT_FILE = os.path.join(os.path.dirname(__file__), "mar-sales-data-cleaned.csv")

US_STATES = {
    "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
    "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
    "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
    "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
    "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
    "New Hampshire", "New Jersey", "New Mexico", "New York",
    "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
    "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
    "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
    "West Virginia", "Wisconsin", "Wyoming",
}

# ---------------------------------------------------------------------------
# Per-row corrections keyed by 1-based row number (row 1 = header).
# Each entry maps column name -> '' (blank it out).
# ---------------------------------------------------------------------------
FIXES: dict[int, dict[str, str]] = {
    # Row 8:  invalid date, Name='No Info', Product='No Info', OrderNum='No Info'
    8:  {"Date of Purchase": "", "Name": "", "Product Purchased": "", "Order Number": ""},
    # Row 31: Product Purchased is numeric ('47')
    31: {"Product Purchased": ""},
    # Row 50: extreme Quantity outlier (12345), Name='No Info', Product numeric ('23')
    50: {"Quantity of Item Purchased": "", "Name": "", "Product Purchased": ""},
    # Row 52: Name matches a US state ('Florida')
    52: {"Name": ""},
    # Row 55: non-numeric Price ('Kale'), Order Number is 'XXXXX'
    55: {"Price paid per item": "", "Order Number": ""},
    # Row 78: date not in March 2019
    78: {"Date of Purchase": ""},
    # Row 83: non-numeric Quantity, non-numeric Price, Name is a US state
    83: {"Quantity of Item Purchased": "", "Price paid per item": "", "Name": ""},
    # Row 102: Name matches a US state ('New York')
    102: {"Name": ""},
    # Row 143: Product Purchased looks like a person's name; Order Number is a US state
    143: {"Product Purchased": "", "Order Number": ""},
    # Row 225: negative Quantity (-12)
    225: {"Quantity of Item Purchased": ""},
    # Row 228: non-numeric Quantity ('Jan-54'); Shipping Address State is an address
    228: {"Quantity of Item Purchased": "", "Shipping Address State": ""},
    # Row 300: invalid date ('20/20/20')
    300: {"Date of Purchase": ""},
    # Row 309: extreme Price outlier ($3277)
    309: {"Price paid per item": ""},
    # Row 341: extreme Quantity outlier (8326)
    341: {"Quantity of Item Purchased": ""},
    # Row 391: Name matches a US state ('Kentucky')
    391: {"Name": ""},
    # Row 443: Shipping Address State is an address ('1690E 13th W')
    443: {"Shipping Address State": ""},
    # Row 454: negative Quantity (-11)
    454: {"Quantity of Item Purchased": ""},
    # Row 466: date not in March 2019, extreme Quantity (7767), State is an address
    466: {"Date of Purchase": "", "Quantity of Item Purchased": "", "Shipping Address State": ""},
    # Row 499: non-numeric Quantity ('X') and non-numeric Price ('X')
    499: {"Quantity of Item Purchased": "", "Price paid per item": ""},
    # Row 517: non-numeric Price ('0="')
    517: {"Price paid per item": ""},
    # Row 557: Shipping Address State is not a valid US state ('Interstate Highway')
    557: {"Shipping Address State": ""},
    # Row 699: extreme Price outlier ($32319)
    699: {"Price paid per item": ""},
    # Row 739: invalid date, non-numeric Quantity, Name is a US state, Order='No Info'
    739: {"Date of Purchase": "", "Quantity of Item Purchased": "", "Name": "", "Order Number": ""},
    # Row 743: non-numeric Quantity ('XII')
    743: {"Quantity of Item Purchased": ""},
    # Row 749: non-numeric Quantity ('""'), Shipping Address State is an address
    749: {"Quantity of Item Purchased": "", "Shipping Address State": ""},
    # Row 816: invalid date ('Alabama')
    816: {"Date of Purchase": ""},
    # Row 837: extreme Quantity outlier (111111)
    837: {"Quantity of Item Purchased": ""},
    # Row 842: Shipping Address State is an address ('S. Iona Rd.')
    842: {"Shipping Address State": ""},
    # Row 851: date not in March 2019, non-numeric Price ('2016-02-02')
    851: {"Date of Purchase": "", "Price paid per item": ""},
    # Row 861: date not in March 2019, Shipping Address State is an address
    861: {"Date of Purchase": "", "Shipping Address State": ""},
    # Row 887: invalid date ('14/13/19')
    887: {"Date of Purchase": ""},
    # Row 937: invalid date ('123456'), negative Quantity (-1)
    937: {"Date of Purchase": "", "Quantity of Item Purchased": ""},
    # Row 950: date not in March 2019, State is not a valid state ('State Drive')
    950: {"Date of Purchase": "", "Shipping Address State": ""},
    # Row 969: invalid date ('31817')
    969: {"Date of Purchase": ""},
    # Row 978: negative Price (-71), Name matches a US state ('South Carolina')
    978: {"Price paid per item": "", "Name": ""},
    # Row 986: extreme Price outlier ($494), Order Number is '0'
    986: {"Price paid per item": "", "Order Number": ""},
}


def apply_fixes(row_num: int, row: dict) -> dict:
    """Return the row with suspicious fields blanked out."""
    if row_num not in FIXES:
        return row
    corrections = FIXES[row_num]
    for col, blank in corrections.items():
        if col in row:
            row[col] = blank
    return row


def main():
    with open(INPUT_FILE, newline="", encoding="utf-8") as infile:
        reader = csv.DictReader(infile)
        fieldnames = reader.fieldnames  # preserve original column order

        rows = []
        for data_row_idx, row in enumerate(reader, start=2):   # row 1 = header
            cleaned = apply_fixes(data_row_idx, dict(row))
            rows.append(cleaned)

    with open(OUTPUT_FILE, "w", newline="", encoding="utf-8") as outfile:
        writer = csv.DictWriter(outfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)

    print(f"Done. Cleaned file written to: {OUTPUT_FILE}")
    print(f"Total data rows processed: {len(rows)}")
    print(f"Total rows with corrections: {len(FIXES)}")


if __name__ == "__main__":
    main()
