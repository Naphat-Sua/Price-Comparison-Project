import pandas as pd
import random
from datetime import datetime, timedelta
import numpy as np
import time

class Data_In_Loop:
    # V1: Functions inside the loop
    
    def __init__(self):
        self.current_id = 0
        self.setup_master_data()
    
    def setup_master_data(self):
        self.province_codes = ['BKK', 'CNX', 'NTB', 'PTT', 'NKP']
        self.country_codes = ['TH', 'US', 'GB', 'JP', 'SG']
        self.bank_codes = ['79601001', '79601002', '79601003']
        self.positions = ['Manager', 'Staff', 'Director', 'Supervisor']
        self.company_sizes = ['S', 'M', 'L']
        self.first_names = ['Somchai', 'Arthit', 'Kittisak', 'Naphat', 'Mali']
        self.last_names = ['Kasikorn', 'KBTG', 'K+', 'KhunThong', 'MeowJot']
        self.company_types = ['Trading', 'Technology', 'Services']
    
    def generate_thai_id(self):
        self.current_id += 1
        prefix = random.randint(1, 9)
        middle = str(self.current_id).zfill(9)
        check_digit = random.randint(0, 9)
        return f"{prefix}{middle}{check_digit}"
    
    def generate_person_name(self):
        return f"{random.choice(self.first_names)} {random.choice(self.last_names)}"
    
    def generate_company_name(self):
        return f"{random.choice(self.company_types)} {random.choice(['Group', 'Corp', 'Co', 'PLC'])} {self.current_id}"
    
    def generate_data_file(self, num_records, output_file):
        records = []
        num_corporate = int(num_records * 0.05)
        num_individual = num_records - num_corporate
        
        # Generate records with function calls inside the loop
        for _ in range(num_corporate + num_individual):
            is_corporate = len(records) < num_corporate
            
            if is_corporate:
                company_name = self.generate_company_name()
                record = {
                    'CUSTID': f"C{self.generate_thai_id()}",
                    'FULL_NAME': company_name,
                    'CUST_TYPE': 'P01',
                    'IDENTITY_NUM': self.generate_thai_id()
                }
            else:
                record = {
                    'CUSTID': f"I{self.generate_thai_id()}",
                    'FULL_NAME': self.generate_person_name(),
                    'CUST_TYPE': 'P02',
                    'IDENTITY_NUM': self.generate_thai_id()
                }
            records.append(record)
            
        df = pd.DataFrame(records)
        df.to_csv(output_file, index=False)
        return len(records)

class Data_Out_Loop:
    # V2: Data outside loop
    
    def __init__(self):
        self.current_id = 0
        self.setup_master_data()
        
    def setup_master_data(self):
        self.province_codes = ['BKK', 'CNX', 'NTB', 'PTT', 'NKP']
        self.country_codes = ['TH', 'US', 'GB', 'JP', 'SG']
        self.bank_codes = ['79601001', '79601002', '79601003']
        self.positions = ['Manager', 'Staff', 'Director', 'Supervisor']
        self.company_sizes = ['S', 'M', 'L']
        
        self.first_names = ['Somchai', 'Arthit', 'Kittisak', 'Naphat', 'Mali']
        self.last_names = ['Kasikorn', 'KBTG', 'K+', 'KhunThong', 'MeowJot']
        self.person_names = [f"{f} {l}" for f in self.first_names for l in self.last_names]
        
        company_types = ['Trading', 'Technology', 'Services']
        company_suffixes = ['Group', 'Corp', 'Co', 'PLC']
        self.company_base_names = [f"{t} {s}" for t in company_types for s in company_suffixes]
        
        self.id_prefixes = list(range(1, 10))
        self.id_check_digits = list(range(10))
        self.id_template = "{prefix}{middle:09d}{check}"
        self.current_id = 0
    
    def generate_batch_ids(self, batch_size):
        """Generate multiple IDs at once"""
        ids = []
        for _ in range(batch_size):
            self.current_id += 1
            ids.append(self.id_template.format(
                prefix=random.choice(self.id_prefixes),
                middle=self.current_id,
                check=random.choice(self.id_check_digits)
            ))
        return ids
    
    def generate_data_file(self, num_records, output_file):
        records = []
        num_corporate = int(num_records * 0.05)
        num_individual = num_records - num_corporate
        
        all_ids = self.generate_batch_ids(num_records * 2)  # Times 2 cause we need ID for both CUSTID and IDENTITY_NUM
        id_index = 0
        
        for i in range(num_corporate):
            company_name = f"{random.choice(self.company_base_names)} {i+1}"
            record = {
                'CUSTID': f"C{all_ids[id_index]}",
                'FULL_NAME': company_name,
                'CUST_TYPE': 'P01',
                'IDENTITY_NUM': all_ids[id_index + 1]
            }
            records.append(record)
            id_index += 2
        
        for _ in range(num_individual):
            record = {
                'CUSTID': f"I{all_ids[id_index]}",
                'FULL_NAME': random.choice(self.person_names),
                'CUST_TYPE': 'P02',
                'IDENTITY_NUM': all_ids[id_index + 1]
            }
            records.append(record)
            id_index += 2
            
        df = pd.DataFrame(records)
        df.to_csv(output_file, index=False)
        return len(records)

def run_performance_test(generator_class, sizes):
    # Run performance test for a given class
    results = []
    generator = generator_class()
    
    for size in sizes:
        start_time = time.time()
        output_file = f"customer_data_{generator_class.__name__}_{size}records.csv"
        
        print(f"\nGenerating {size} records using {generator_class.__name__}...")
        records_generated = generator.generate_data_file(size, output_file)
        
        duration = time.time() - start_time
        memory_usage = df = pd.read_csv(output_file).memory_usage(deep=True).sum() / 1024 / 1024 
        
        result = {
            'size': size,
            'duration': duration,
            'records_per_second': records_generated/duration,
            'memory_mb': memory_usage
        }
        results.append(result)
        
        print(f"Generated {records_generated} records in {duration:.2f} seconds")
        print(f"Speed: {records_generated/duration:.2f} records/second")
        print(f"Memory usage: {memory_usage:.2f} MB")
    
    return results

def main():
    sizes = [10000, 100000, 1000000]
    
    print("Testing function in loop...")
    in_loop_results = run_performance_test(Data_In_Loop, sizes)
    
    print("\nTesting function out loop...")
    optimized_results = run_performance_test(Data_Out_Loop, sizes)
    
    # Compare results
    print("\nPerformance Comparison:")
    print("\nSize\tMethod\t\tDuration\tSpeed\t\tMemory")
    print("-" * 70)
    
    for i, size in enumerate(sizes):
        in_loop = in_loop_results[i]
        opt = optimized_results[i]
        
        print(f"{size:,}\tIn Loop\t\t{in_loop['duration']:.2f}s\t{in_loop['records_per_second']:.0f} r/s\t{in_loop['memory_mb']:.1f}MB")
        print(f"{size:,}\tOptimized\t{opt['duration']:.2f}s\t{opt['records_per_second']:.0f} r/s\t{opt['memory_mb']:.1f}MB")
        print(f"Speedup: {in_loop['duration']/opt['duration']:.2f}x")
        print("-" * 70)

if __name__ == "__main__":
    main()