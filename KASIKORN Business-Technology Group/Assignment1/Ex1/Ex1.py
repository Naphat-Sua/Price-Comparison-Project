import random
from datetime import datetime, timedelta
import time

class Ex1:
    def __init__(self):
        self.current_date = datetime.now()
        self.business_date = self.current_date.strftime('%Y-%m-%d')
        self.system_date = self.current_date.strftime('%Y-%m-%dT%H:%M:%S')

    def generate_header(self):
        header = (
            'H01'  # RecType
            f'{self.system_date:<33}'  # SysDt
            f'{self.business_date:<10}'  # BusinessDt
            '494  '  # SrcAppId
            'AcctInf '  # FileType
            '000001'  # FileSeqNum
            ' ' * 485  # Filler
        )
        return header

    def generate_transaction(self, operation_type, trn_amt, fee_amt):
        src_uid = f'500022223455123_5hu12e2{random.randint(1000, 9999):<40}'
        rq_uid = f'494_{self.current_date.strftime("%Y%m%d")}_{random.randint(100000, 999999):<47}'
        
        transaction = (
            f'{src_uid:<40}'  # SrcUID
            f'{rq_uid:<47}'  # RqUID
            f'{operation_type:<2}'  # OperationType (DR/CR)
            '01'  # OperationCode
            '0001'  # SubOperationCode
            'K0999999'  # UserId
            'A04CIS01'  # TerminalId
            f'{self.business_date:<10}'  # ValueDt
            f'{"Transaction for testing":<45}'  # Concept1
            f'{"TEST" + str(random.randint(1000, 9999)):<55}'  # Concept2
            f'{str(random.randint(1000000000, 9999999999)):0>10}'  # AcctId
            f'{int(trn_amt * 100):0>18}'  # TrnAmt
            f'{int(fee_amt * 100):0>18}'  # FeeAmt
            '9180'  # SvcBranchId
            f'{"KB" + str(random.randint(100000, 999999)):<15}'  # AuthUserId
            '001'  # AuthLevel
            f'{self.business_date:<10}'  # ExtAcctDt
            'N'  # UseSvcBranch
            '001'  # ICA
            ' ' * 247  # Filler
        )
        return transaction

    def generate_trailer(self, total_records, total_debit_sum, total_credit_sum):
        total_sum = total_debit_sum + total_credit_sum
        trailer = (
            'T01'  # RecType
            f'{total_records:0>15}'  # TotalRec
            f'{int(total_sum * 100):0>18}'  # TotalSum
            f'{int(total_debit_sum * 100):0>18}'  # TotalDebitSum
            f'{int(total_credit_sum * 100):0>18}'  # TotalCreditSum
            ' ' * 478  # Filler
        )
        return trailer

    def generate_file(self, num_records, debit_ratio=0.6, base_amount=1000):
        start_time = time.time()
        
        total_debit_sum = 0
        total_credit_sum = 0
        
        filename = f'SHARC.EDCMP.FCS3D01.RETAIL.DEBIT.TCB_{num_records}'
        
        with open(filename, 'w') as f:
            f.write(self.generate_header() + '\n')
            
            # Transactions
            for _ in range(num_records):
                is_debit = random.random() < debit_ratio
                operation_type = 'DR' if is_debit else 'CR'
                
                trn_amt = random.uniform(base_amount * 0.5, base_amount * 1.5)
                fee_amt = random.uniform(10, 50)
                
                if is_debit:
                    total_debit_sum += trn_amt
                else:
                    total_credit_sum += trn_amt
                
                transaction = self.generate_transaction(operation_type, trn_amt, fee_amt)
                f.write(transaction + '\n')
            
            # Trailer
            trailer = self.generate_trailer(num_records, total_debit_sum, total_credit_sum)
            f.write(trailer)
        
        end_time = time.time()
        return end_time - start_time

def main():
    generator = Ex1()
    record_counts = [10000, 100000]#, 1000000]
    
    print("Starting generate test data...")
    print(f"{'Records':<10} {'Time (seconds)':<15} {'Speed (records/sec)'}")
    print("-" * 80)
    
    for count in record_counts:
        execution_time = generator.generate_file(count)
        speed = count / execution_time
        print(f"{count:<10} {execution_time:<15.2f} {speed:.2f}")

    print("-" * 80)

if __name__ == "__main__":
    main()