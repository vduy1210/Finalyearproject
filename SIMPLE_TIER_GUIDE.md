# Simple Customer Tier Configuration

## Overview
A simple tier system that stores configuration in **Java Preferences** (no database table needed!). Admin can configure tier thresholds with one button click.

## How It Works

### Storage
- Tier thresholds and discounts are stored using **Java Preferences API**
- Saved automatically to your system (Windows Registry on Windows, file on Linux/Mac)
- No need to create database tables or run SQL scripts

### Default Tiers

| Tier     | Points Range     | Discount |
|----------|------------------|----------|
| Bronze   | 0 - 999          | 0%       |
| Silver   | 1,000 - 4,999    | 5%       |
| Gold     | 5,000 - 9,999    | 10%      |
| Platinum | 10,000+          | 15%      |

## Usage

### For Admin:

1. **Open Customer Management Panel**
   - You'll see customers with their accumulated points
   - The "Tier" column shows their current tier and discount

2. **Configure Tiers**
   - Click the **"Configure Tiers"** button
   - A dialog will open with current settings:
     - Bronze discount %
     - Silver minimum points and discount %
     - Gold minimum points and discount %
     - Platinum minimum points and discount %
   
3. **Modify Settings**
   - Change any values you want
   - Example: Change Silver min from 1000 to 2000
   - Example: Change Gold discount from 10% to 12%
   
4. **Save**
   - Click OK
   - Settings are saved immediately
   - Customer list refreshes with new tier calculations

### For Developers:

#### Get Discount for a Customer:
```java
// In checkout or order processing
float points = customer.getAccumulatedPoint();
float discountPercent = CustomerManagementPanel.getDiscountForPoints(points);
float discountAmount = orderTotal * discountPercent / 100;
float finalTotal = orderTotal - discountAmount;
```

#### Tier Calculation Logic:
```java
// Automatically calculates tier based on points and saved configuration
private String calculateTier(float points) {
    if (points >= platinum_min) return "Platinum (15%)";
    if (points >= gold_min) return "Gold (10%)";
    if (points >= silver_min) return "Silver (5%)";
    return "Bronze (0%)";
}
```

## Advantages

✅ **No Database Changes** - Works with your existing `customers` table  
✅ **Simple Configuration** - One button, one dialog  
✅ **Instant Updates** - Changes apply immediately  
✅ **Persistent** - Settings saved automatically  
✅ **Flexible** - Easy to change thresholds and discounts  
✅ **No SQL Scripts** - No setup needed  

## Configuration Locations

### Windows
```
Registry: HKEY_CURRENT_USER\Software\JavaSoft\Prefs\view\CustomerManagementPanel
```

### Linux/Mac
```
File: ~/.java/.userPrefs/view/CustomerManagementPanel/prefs.xml
```

## Examples

### Scenario 1: Change Silver Tier Threshold
**Before:**
- Silver: 1,000 - 4,999 points (5% discount)

**Admin Action:**
1. Click "Configure Tiers"
2. Change "Silver Min Points" from 1000 to 2000
3. Click OK

**After:**
- Bronze: 0 - 1,999 points (0% discount)
- Silver: 2,000 - 4,999 points (5% discount)

### Scenario 2: Increase Platinum Benefits
**Before:**
- Platinum: 10,000+ points (15% discount)

**Admin Action:**
1. Click "Configure Tiers"
2. Change "Platinum Discount" from 15 to 20
3. Change "Platinum Min Points" from 10000 to 8000
4. Click OK

**After:**
- Platinum: 8,000+ points (20% discount)

## Customer Table Display

The customer table now shows:
```
| ID | Customer | Phone | Email | Orders | Total Spent | Points | Tier            | Last Visit |
|----|----------|-------|-------|--------|-------------|--------|-----------------|------------|
| 1  | John Doe | ...   | ...   | 15     | 5,000,000đ  | 5500   | Gold (10%)      | 25/10/2025 |
| 2  | Jane S.  | ...   | ...   | 25     | 12,000,000đ | 12000  | Platinum (15%)  | 26/10/2025 |
| 3  | Bob M.   | ...   | ...   | 5      | 800,000đ    | 800    | Bronze (0%)     | 20/10/2025 |
```

## Notes

- Tier is calculated **dynamically** based on current accumulated points
- No need to "update" or "sync" tiers
- When customer earns more points, their tier automatically updates on next load
- Settings are per-user on the computer running the application
- If you want company-wide settings, consider moving to database storage later

## That's It!

No SQL scripts to run, no database tables to create. Just click "Configure Tiers" and set your thresholds!
