import random
from datetime import datetime, timedelta
import csv
import time
import psutil
import os

class Ex3:
    def __init__(self):
        self.current_date = datetime.now()
        self.branches = {
            'corporate': ['80001'],
            'individual': ['90001']
        }
        self.province_codes = ['BKK', 'CNX', 'NTB', 'PTT', 'NKP']  # Updated from provinces
        self.positions = ['Manager', 'Staff', 'Engineer', 'Accountant', 'Director']
        self.company_sizes = ['S', 'M', 'L']
        self.sic_groups = ['9', 'K', 'A', 'C']
        self.fieldnames = None

    def generate_date_of_birth(self):
        age = random.randint(20, 60)
        birth_date = self.current_date - timedelta(days=age*365 + random.randint(0, 365))
        return birth_date.strftime('%Y-%m-%d')

    def get_memory_usage(self):
        # Returns memory usage in MB
        return psutil.Process(os.getpid()).memory_info().rss / 1024 / 1024

    def generate_within_loop(self, num_records, filename):
        start_time = time.time()
        start_memory = self.get_memory_usage()
        
        num_corporate = int(num_records * 0.05)
        num_individual = num_records - num_corporate
        
        with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
            writer = csv.writer(csvfile, delimiter=',')
            writer.writerow([
                'TransactionDate', 'BRID', 'CUSTID', 'CUST_TYPE', 'FULL_NAME',
                'BIRTHDAY', 'ADDRESS', 'PROVINCE_CODE', 'TOTAL_ASSETS'
            ])
            
            for i in range(num_records):
                is_corporate = i < num_corporate
                
                trans_date = datetime.now().strftime('%Y-%m-%d')
                brid = '80001' if is_corporate else '90001'
                cust_id = f"{'C' if is_corporate else 'I'}{random.randint(1000000, 9999999)}"
                cust_type = 'P01' if is_corporate else 'P02'
                full_name = f"{'Corporation' if is_corporate else 'Individual'} {cust_id}"
                birthday = '' if is_corporate else self.generate_date_of_birth()
                address = f"{'Corporate' if is_corporate else 'Home'} Address {random.randint(1, 1000)}"
                province = random.choice(['HN', 'HCM', 'DN', 'HP', 'CT'])
                total_assets = random.randint(1000000000, 10000000000) if is_corporate else random.randint(100000000, 1000000000)
                
                writer.writerow([
                    trans_date, brid, cust_id, cust_type, full_name,
                    birthday, address, province, total_assets
                ])

                # Check memory usage every 100 records
                if (i + 1) % 100 == 0:
                    current_memory = self.get_memory_usage()
                    print(f"Processed {i + 1} records, memory used: {current_memory:.2f} MB")
        
        end_time = time.time()
        end_memory = self.get_memory_usage()
        
        return {
            'execution_time': end_time - start_time,
            'memory_used': end_memory - start_memory
        }

    def generate_with_predefined(self, num_records, filename):
        start_time = time.time()
        start_memory = self.get_memory_usage()
        
        trans_date = self.current_date.strftime('%Y-%m-%d')
        num_corporate = int(num_records * 0.05)
        num_individual = num_records - num_corporate
        
        customer_types = {
            'corporate': 'P01',
            'individual': 'P02'
        }
        
        address_prefixes = {
            'corporate': 'Corporate Address',
            'individual': 'Home Address'
        }
        
        with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
            writer = csv.writer(csvfile, delimiter=',')
            writer.writerow([
                'TransactionDate', 'BRID', 'CUSTID', 'CUST_TYPE', 'FULL_NAME',
                'BIRTHDAY', 'ADDRESS', 'PROVINCE_CODE', 'TOTAL_ASSETS'
            ])
            
            for i in range(num_records):
                is_corporate = i < num_corporate
                cust_type = 'corporate' if is_corporate else 'individual'
                
                writer.writerow([
                    trans_date,
                    self.branches[cust_type][0],
                    f"{'C' if is_corporate else 'I'}{random.randint(1000000, 9999999)}",
                    customer_types[cust_type],
                    f"{cust_type.title()} Customer {i+1}",
                    '' if is_corporate else self.generate_date_of_birth(),
                    f"{address_prefixes[cust_type]} {random.randint(1, 1000)}",
                    random.choice(self.province_codes),  # Fixed here
                    random.randint(1000000000, 10000000000) if is_corporate else random.randint(100000000, 1000000000)
                ])

                # Check memory usage every 100 records
                if (i + 1) % 100 == 0:
                    current_memory = self.get_memory_usage()
                    print(f"Processed {i + 1} records, memory used: {current_memory:.2f} MB")
        
        end_time = time.time()
        end_memory = self.get_memory_usage()
        
        return {
            'execution_time': end_time - start_time,
            'memory_used': end_memory - start_memory
        }

def main():
    generator = Ex3()
    record_counts = [10000, 100000, 1000000]
    
    print("\nPerformance Comparison Results:")
    print("-" * 80)
    print(f"{'Records':<10} {'Approach':<15} {'Time (sec)':<12} {'Memory (MB)':<12} {'Speed (rec/sec)'}")
    print("-" * 80)
    
    for count in record_counts:
        result1 = generator.generate_within_loop(
            count, 
            f'customer_data_Within_Loop_{count}.csv'
        )
        
        result2 = generator.generate_with_predefined(
            count, 
            f'customer_data_Predefined_{count}.csv'
        )
    
        print(f"{count:<10} {'Within Loop':<15} {result1['execution_time']:<12.2f} "
              f"{result1['memory_used']:<12.2f} {count/result1['execution_time']:.2f}")
        print(f"{'':<10} {'Pre-defined':<15} {result2['execution_time']:<12.2f} "
              f"{result2['memory_used']:<12.2f} {count/result2['execution_time']:.2f}")
        print("-" * 80)

if __name__ == "__main__":
    main()