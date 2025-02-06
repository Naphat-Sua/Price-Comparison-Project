import random
from datetime import datetime, timedelta
import csv
import time
import psutil  # Import psutil for memory monitoring
import os  # Import os for file path handling

class Ex2:
    def __init__(self):
        self.current_date = datetime.now()
        self.corporate_branches = ['80001']
        self.individual_branches = ['90001']
        self.provinces = ['HN', 'HCM', 'DN', 'HP', 'CT']
        self.positions = ['Manager', 'Staff', 'Engineer', 'Accountant', 'Director']
        self.company_sizes = ['S', 'M', 'L']
    
    def generate_date_of_birth(self):
        age = random.randint(20, 60)
        birth_date = self.current_date - timedelta(days=age*365 + random.randint(0, 365))
        return birth_date.strftime('%Y-%m-%d')
    
    def generate_individual_customer(self):
        dob = self.generate_date_of_birth()
        customer_id = f"I{random.randint(1000000, 9999999)}"
        
        return {
            'TransactionDate': self.current_date.strftime('%Y-%m-%d'),
            'BRID': random.choice(self.individual_branches),
            'CUSTID': customer_id,
            'BANKCODE': '79601001',
            'RESIDENT': random.choice(['0', '1']),
            'CUST_TYPE': 'P02',  # Individual
            'BIRTHDAY': dob,
            'FULL_NAME': f"Individual Customer {customer_id}",
            'PHONE': f"0{random.randint(100000000, 999999999)}",
            'EMAIL': f"customer_{customer_id}@email.com",
            'POSITION': random.choice(self.positions),
            'INCOME': random.randint(5000000, 50000000),
            'PROVINCE_CODE': random.choice(self.provinces),
            'COUNTRY_CODE': 'VN',
            'IDENTITY_NUM': f"{random.randint(100000000, 999999999)}",
            'TOTAL_ASSETS': random.randint(100000000, 1000000000),
            'TOTAL_INCOME': random.randint(50000000, 500000000),
            'TOTAL_LIABILITY': random.randint(10000000, 100000000)
        }

    def generate_corporate_customer(self):
        customer_id = f"C{random.randint(1000000, 9999999)}"
        
        return {
            'TransactionDate': self.current_date.strftime('%Y-%m-%d'),
            'BRID': random.choice(self.corporate_branches),
            'CUSTID': customer_id,
            'BANKCODE': '79601001',
            'RESIDENT': '1',
            'CUST_TYPE': 'P01',  # Corporate
            'FULL_NAME': f"Corporation {customer_id}",
            'PHONE': f"0{random.randint(100000000, 999999999)}",
            'EMAIL': f"corp_{customer_id.lower()}@corp.com",
            'PROVINCE_CODE': random.choice(self.provinces),
            'COUNTRY_CODE': 'VN',
            'TOTAL_ASSETS': random.randint(1000000000, 10000000000),
            'TOTAL_INCOME': random.randint(500000000, 5000000000),
            'TOTAL_LIABILITY': random.randint(100000000, 1000000000),
            'BIRTHDAY': ''
        }

    def generate_file(self, num_records, filename):
        start_time = time.time()
        start_memory = psutil.virtual_memory().used / (1024 * 1024)  # Memory in MB
        
        num_corporate = int(num_records * 0.05)         
        num_individual = num_records - num_corporate 
        
        fieldnames = list(self.generate_individual_customer().keys())
        
        with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames, delimiter=',')
            writer.writeheader()
            
            for _ in range(num_corporate):
                writer.writerow(self.generate_corporate_customer())
                
            for _ in range(num_individual):
                writer.writerow(self.generate_individual_customer())
        
        end_time = time.time()
        end_memory = psutil.virtual_memory().used / (1024 * 1024)  # Memory in MB
        
        return end_time - start_time, end_memory - start_memory

def main():
    generator = Ex2()
    record_counts = [10000, 100000, 1000000]
    
    print("Starting generate test data...")
    print(f"{'Records':<10} {'Time (seconds)':<15} {'Speed (records/sec)':<20} {'Memory Used (MB)'}")
    print("-" * 100)
    
    for count in record_counts:
        filename = f'Customer_extract_{datetime.now().strftime("%Y%m%d")}_{count}.csv'
        execution_time, memory_used = generator.generate_file(count, filename)
        speed = count / execution_time
        print(f"{count:<10} {execution_time:<15.2f} {speed:<20.2f} {memory_used:.2f}")
    
    print("-" * 100)

if __name__ == "__main__":
    main()
