import pandas as pd
import numpy as np
import re
from word2number import w2n

def safe_w2n(x):
    try:
        if isinstance(x, str):
            # Map common typos or things w2n might not get
            x = x.lower().strip()
            # Replace 'o' or 'O' with '0' if it looks like a number string
            if re.match(r'^[0-9,]+o+$', x.lower()):
                x = x.replace('o', '0')
            if 'o' in x.lower() and any(char.isdigit() for char in x):
                x = x.lower().replace('o', '0')
            x = x.replace(',', '')
            if x.isdigit() or (x.startswith('-') and x[1:].isdigit()):
                return float(x)
            return float(w2n.word_to_num(x))
        return float(x)
    except:
        return np.nan

# Load data
customers = pd.read_csv('customer_data.csv')
demographics = pd.read_csv('us_demographics_data.csv')

# Normalize column names in demographics for easier merging
demo_zip_map = demographics.set_index('Zip').to_dict('index')

# Function to fix gender
def clean_gender(g):
    if pd.isna(g): return np.nan
    g = str(g).lower().strip()
    if g in ['m', 'male', 'mail', 'mallll', 'boy', 'man']:
        return 'Male'
    if g in ['f', 'female', 'girl', 'woman', 'femal']:
        return 'Female'
    if g in ['other', 'non-binary']:
        return 'Other'
    return np.nan

def clean_education(e):
    if pd.isna(e): return np.nan
    e = str(e).lower().strip()
    if 'high school' in e:
        return 'High School Diploma'
    if 'some college' in e or 'associate' in e:
        return 'Some College'
    if 'bachelor' in e:
        return "Bachelor's Degree"
    if 'master' in e:
        return "Master's Degree"
    if 'doctor' in e or 'phd' in e:
        return 'Doctorate Degree'
    if 'ged' in e or 'geeee' in e:
        return 'GED'
    return np.nan

print("Original shape:", customers.shape)

cleaned_data = customers.copy()

# 1. Correct Incorrect Value Formats & Remove Impossible Values
cleaned_data['Gender'] = cleaned_data['Gender'].apply(clean_gender)
cleaned_data['Education Level'] = cleaned_data['Education Level'].apply(clean_education)
cleaned_data['Age'] = cleaned_data['Age'].apply(safe_w2n)
cleaned_data['Annual Income'] = cleaned_data['Annual Income'].apply(safe_w2n)
cleaned_data['Annual Disposal'] = cleaned_data['Annual Disposal'].apply(safe_w2n)

# Remove impossible numeric values (set to NaN to be interpolated)
cleaned_data.loc[(cleaned_data['Age'] < 13) | (cleaned_data['Age'] > 120), 'Age'] = np.nan
cleaned_data.loc[cleaned_data['Annual Income'] < 0, 'Annual Income'] = np.nan
cleaned_data.loc[cleaned_data['Annual Disposal'] < 0, 'Annual Disposal'] = np.nan

# Remove Extreme Outliers (Winsorize or remove based on IQR)
for col in ['Annual Income', 'Annual Disposal']:
    Q1 = cleaned_data[col].quantile(0.25)
    Q3 = cleaned_data[col].quantile(0.75)
    IQR = Q3 - Q1
    upper_bound = Q3 + 3 * IQR # Using 3x IQR for extreme outliers
    # We will remove these entirely or set to NaN
    cleaned_data.loc[cleaned_data[col] > upper_bound, col] = np.nan

# Complex problems: Identify if state matches Zip code state
# First, let's fix missing or incorrect state with Zip Code
for idx, row in cleaned_data.iterrows():
    zip_code = row['Zip Code']
    if zip_code in demo_zip_map:
        demo_info = demo_zip_map[zip_code]
        correct_state = demo_info['State']
        
        # Identify categories from granular data (State from Zip Code)
        # If State is wrong, missing, or belongs to Canada/Mexico but Zip is US
        if pd.isna(row['State']) or str(row['State']).strip().lower() not in [correct_state.lower(), 'unkown', 'state']:
            # Either it's completely wrong or just a typo. Let's overwrite it with the demographic state
            cleaned_data.at[idx, 'State'] = correct_state
            
        # 3. Interpolate missing data using estimated values from demographics
        if pd.isna(row['Age']):
            cleaned_data.at[idx, 'Age'] = demo_info['Average age']
        if pd.isna(row['Annual Income']):
            cleaned_data.at[idx, 'Annual Income'] = demo_info['Average Annual Income']
        if pd.isna(row['Annual Disposal']):
            cleaned_data.at[idx, 'Annual Disposal'] = demo_info['Average annual disposable funds']
        if pd.isna(row['Education Level']):
            cleaned_data.at[idx, 'Education Level'] = clean_education(demo_info['Education'])
        if pd.isna(row['Gender']):
            # Predict based on majority in zip code?
            f_pct = demo_info.get('Female', 0.5)
            cleaned_data.at[idx, 'Gender'] = 'Female' if f_pct >= 0.5 else 'Male'

# Final check for missing values
print("Missing values after cleaning:")
print(cleaned_data.isnull().sum())

# Keep only valid records or drop remaining completely unfixable data
# But based on instructions: "save the output in the same directory"
cleaned_data.to_csv('cleaned_customer_data.csv', index=False)
print("Data cleaned and saved to cleaned_customer_data.csv")

