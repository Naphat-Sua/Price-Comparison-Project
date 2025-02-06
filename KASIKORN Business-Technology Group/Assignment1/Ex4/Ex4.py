import pandas as pd
import random
from datetime import datetime, timedelta
import numpy as np
import psutil

class CustomerDataGenerator:
    def __init__(self):
        # Simulated master data
        self.current_id = 0
        
        # Pre-defined data
        self.province_codes = ['HN', 'HCM', 'DN', 'HP', 'CT']
        self.country_codes = ['VN', 'US', 'GB', 'JP', 'SG']
        self.bank_codes = ['79601001', '79601002', '79601003']
        self.positions = ['Manager', 'Staff', 'Director', 'Supervisor', 'Specialist']
        self.company_sizes = ['S', 'M', 'L']
        
        # Simulated name data
        self.first_names = ['Minh', 'Linh', 'Tuan', 'Hoa', 'Nam', 'Mai', 'Duc', 'Lan', 'Thanh', 'Hong']
        self.last_names = ['Nguyen', 'Tran', 'Le', 'Pham', 'Hoang', 'Phan', 'Vu', 'Dang', 'Bui', 'Do']
        self.company_types = ['Trading', 'Technology', 'Services', 'Manufacturing', 'Consulting']
        
    def generate_thai_id(self):
        """Generate unique Thai ID number following specific format"""
        self.current_id += 1
        prefix = random.randint(1, 9)
        middle = str(self.current_id).zfill(9)
        # In real implementation, would include checksum calculation
        check_digit = random.randint(0, 9)
        return f"{prefix}{middle}{check_digit}"
    
    def generate_person_name(self):
        """Generate a random Vietnamese person name"""
        return f"{random.choice(self.last_names)} {random.choice(self.first_names)}"
    
    def generate_company_name(self):
        """Generate a random company name"""
        return f"{random.choice(self.company_types)} {random.choice(['Group', 'Corporation', 'Company', 'Enterprise'])} {self.current_id}"
    
    def generate_customer_record(self, is_corporate=False):
        today = datetime.now()
        
        if is_corporate:
            company_name = self.generate_company_name()
            record = {
                'TransactionDate': today.strftime('%Y-%m-%d'),
                'BRID': '80001',
                'CUSTID': f"C{self.generate_thai_id()}",
                'BANKCODE': random.choice(self.bank_codes),
                'RESIDENT': random.choice(['0', '1']),
                'CUST_TYPE': 'P01',
                'BIRTHDAY': None,
                'FULL_NAME': company_name,
                'TRADENAME': f"Trading As {company_name}",
                'ABBNAME': f"CO{self.current_id}",
                'SEX': None,
                'ADDRESS': f"Building {random.randint(1,999)}, Street {random.randint(1,100)}, {random.choice(self.province_codes)}",
                'PHONE': f"+84{random.randint(100000000,999999999)}",
                'EMAIL': f"contact{self.current_id}@{company_name.lower().replace(' ', '')}.com",
                'PROVINCE_CODE': random.choice(self.province_codes),
                'COUNTRY_CODE': random.choice(self.country_codes),
                'IDENTITY_NUM': str(random.randint(1000000000, 9999999999)),
                'IDENTITY_DATE': (today - timedelta(days=random.randint(365, 3650))).strftime('%Y-%m-%d'),
                'SIZEOFCO': random.choice(self.company_sizes),
                'TOTAL_EMPLOYEE': random.randint(10, 1000),
                'TOTAL_ASSETS': random.uniform(1000000, 100000000),
                'CAPITAL': random.uniform(100000, 10000000),
                'ECOTYPE': f"IN{random.randint(1,99):02d}",
                'OPNDATE': (today - timedelta(days=random.randint(365, 3650))).strftime('%Y-%m-%d'),
                'INC_DATE': (today - timedelta(days=random.randint(365, 3650))).strftime('%Y-%m-%d'),
                'TAXCODE': str(random.randint(1000000000, 9999999999))
            }
        else:
            # Generate random birth date for age between 20-60
            birth_date = today - timedelta(days=random.randint(20*365, 60*365))
            full_name = self.generate_person_name()
            
            record = {
                'TransactionDate': today.strftime('%Y-%m-%d'),
                'BRID': '90001',
                'CUSTID': f"I{self.generate_thai_id()}",
                'BANKCODE': random.choice(self.bank_codes),
                'RESIDENT': random.choice(['0', '1']),
                'CUST_TYPE': 'P02',
                'BIRTHDAY': birth_date.strftime('%Y-%m-%d'),
                'FULL_NAME': full_name,
                'TRADENAME': None,
                'ABBNAME': None,
                'SEX': random.choice(['0', '1']),
                'ADDRESS': f"House {random.randint(1,999)}, Street {random.randint(1,100)}, {random.choice(self.province_codes)}",
                'PHONE': f"+84{random.randint(100000000,999999999)}",
                'EMAIL': f"{full_name.lower().replace(' ', '.')}{random.randint(1,999)}@example.com",
                'PROVINCE_CODE': random.choice(self.province_codes),
                'COUNTRY_CODE': random.choice(self.country_codes),
                'IDENTITY_NUM': self.generate_thai_id(),
                'IDENTITY_DATE': (today - timedelta(days=random.randint(365, 3650))).strftime('%Y-%m-%d'),
                'POSITION': random.choice(self.positions),
                'INCOME': random.uniform(15000, 150000),
                'TAXCODE': str(random.randint(1000000000, 9999999999)),
                'OPNDATE': (today - timedelta(days=random.randint(365, 3650))).strftime('%Y-%m-%d')
            }
            
        return record

    def generate_data_file(self, num_records, output_file):
        """Generate customer data file with specified number of records"""
        records = []
        
        # Calculate number of corporate vs individual customers (5:95 ratio)
        num_corporate = int(num_records * 0.05)
        num_individual = num_records - num_corporate
        
        # Generate corporate records
        for _ in range(num_corporate):
            records.append(self.generate_customer_record(is_corporate=True))
            
        # Generate individual records
        for _ in range(num_individual):
            records.append(self.generate_customer_record(is_corporate=False))
            
        # Convert to DataFrame and save
        df = pd.DataFrame(records)
        df.to_csv(output_file, index=False, encoding='utf-8')
        return len(records)

def main():
    # Initialize generator
    generator = CustomerDataGenerator()
    
    # Generate files with different sizes
    sizes = [10000, 100000, 1000000]
    
    for size in sizes:
        start_time = datetime.now()
        output_file = f"customer_data_{size}records.csv"
        
        # RAM usage before starting
        start_memory = psutil.virtual_memory().used / (1024 ** 2)  # Convert to MB
        print(f"\nGenerating {size} records...")
        
        # Track RAM usage during the process
        records_generated = generator.generate_data_file(size, output_file)
        
        end_time = datetime.now()
        end_memory = psutil.virtual_memory().used / (1024 ** 2)  # Convert to MB
        
        duration = (end_time - start_time).total_seconds()
        
        print(f"Generated {records_generated} records in {output_file}")
        print(f"Time taken: {duration:.2f} seconds")
        print(f"Records per second: {records_generated/duration:.2f}")
        
        # Display memory usage results
        print(f"Memory used before: {start_memory:.2f} MB")
        print(f"Memory used after: {end_memory:.2f} MB")
        print(f"Memory increase: {end_memory - start_memory:.2f} MB")

if __name__ == "__main__":
    main()
