import random
import time
import psutil  # Import for memory monitoring
from datetime import datetime

class Ex1:
    def __init__(self):
        self.current_date = datetime.now()
        self.business_date = self.current_date.strftime('%Y-%m-%d')
        self.system_date = self.current_date.strftime('%Y-%m-%dT%H:%M:%S')

    def generate_header(self):
        return (
            'H01'
            f'{self.system_date:<33}'
            f'{self.business_date:<10}'
            '494  '
            'AcctInf '
            '000001'
            + ' ' * 485
            + '\n'
        )

    def generate_transaction(self, operation_type, trn_amt, fee_amt):
        src_uid = f'500022223455123_5hu12e2{random.randint(1000, 9999):<40}'
        rq_uid = f'494_{self.current_date.strftime("%Y%m%d")}_{random.randint(100000, 999999):<47}'

        return (
            f'{src_uid:<40}'
            f'{rq_uid:<47}'
            f'{operation_type:<2}'
            '01'
            '0001'
            'K0999999'
            'A04CIS01'
            f'{self.business_date:<10}'
            f'Transaction for testing:{"":<45}'
            f'TEST{random.randint(1000, 9999):<55}'
            f'{random.randint(1000000000, 9999999999):0>10}'
            f'{int(trn_amt * 100):0>18}'
            f'{int(fee_amt * 100):0>18}'
            '9180'
            f'KB{random.randint(100000, 999999):<15}'
            '001'
            f'{self.business_date:<10}'
            'N'
            '001'
            + ' ' * 247
            + '\n'
        )

    def generate_trailer(self, total_records, total_debit_sum, total_credit_sum):
        total_sum = total_debit_sum + total_credit_sum
        return (
            'T01'
            + f'{total_records:0>15}'
            + f'{int(total_sum * 100):0>18}'
            + f'{int(total_debit_sum * 100):0>18}'
            + f'{int(total_credit_sum * 100):0>18}'
            + ' ' * 478
            + '\n'
        )

    def generate_file(self, num_records, debit_ratio=0.6, base_amount=1000):
        start_time = time.time()
        total_debit_sum = 0
        total_credit_sum = 0
        transactions = []

        # Memory tracking before execution
        process = psutil.Process()
        memory_before = process.memory_info().rss / (1024 * 1024)  # Convert to MB

        for _ in range(num_records):
            is_debit = random.random() < debit_ratio
            operation_type = 'DR' if is_debit else 'CR'
            trn_amt = random.uniform(base_amount * 0.5, base_amount * 1.5)
            fee_amt = random.uniform(10, 50)

            if is_debit:
                total_debit_sum += trn_amt
            else:
                total_credit_sum += trn_amt

            transactions.append(self.generate_transaction(operation_type, trn_amt, fee_amt))

        # Memory tracking after execution
        memory_after = process.memory_info().rss / (1024 * 1024)  # Convert to MB

        execution_time = time.time() - start_time
        return execution_time, memory_after - memory_before  # Time and RAM usage


def main():
    generator = Ex1()
    record_counts = [10000, 100000, 1000000]

    print("Starting generate test data...")
    print(f"{'Records':<10} {'Time (s)':<10} {'RAM Used (MB)'}")
    print("-" * 40)

    for count in record_counts:
        execution_time, memory_used = generator.generate_file(count)
        print(f"{count:<10} {execution_time:<10.2f} {memory_used:.2f}")

    print("-" * 40)


if __name__ == "__main__":
    main()
