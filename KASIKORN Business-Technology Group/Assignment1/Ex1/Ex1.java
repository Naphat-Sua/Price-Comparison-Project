package Assignment1.Ex1;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Ex1 {
    private final LocalDateTime currentDate;
    private final String businessDate;
    private final String systemDate;
    private final Random random;

    public Ex1() {
        this.currentDate = LocalDateTime.now();
        this.businessDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.systemDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        this.random = new Random();
    }

    private String generateHeader() {
        return String.format("%-3s%-33s%-10s%-5s%-8s%-6s%-485s",
            "H01",             // RecType
            systemDate,        // SysDt
            businessDate,      // BusinessDt
            "494",            // SrcAppId
            "AcctInf",        // FileType
            "000001",         // FileSeqNum
            ""               // Filler
        );
    }

    private String generateTransaction(String operationType, double trnAmt, double feeAmt) {
        String srcUid = String.format("500022223455123_5hu12e2%04d", random.nextInt(9000) + 1000);
        String rqUid = String.format("494_%s_%06d",
            currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
            random.nextInt(900000) + 100000);

        return String.format("%-40s%-47s%-2s%-2s%-4s%-8s%-8s%-10s%-45s%-55s%010d%018d%018d%-4s%-15s%-3s%-10s%-1s%-3s%-247s",
            srcUid,                                            // SrcUID
            rqUid,                                            // RqUID
            operationType,                                    // OperationType (DR/CR)
            "01",                                            // OperationCode
            "0001",                                          // SubOperationCode
            "K0999999",                                      // UserId
            "A04CIS01",                                      // TerminalId
            businessDate,                                    // ValueDt
            "Transaction for testing",                       // Concept1
            "TEST" + (random.nextInt(9000) + 1000),         // Concept2
            random.nextInt(9000000) + 1000000000,           // AcctId
            (int)(trnAmt * 100),                            // TrnAmt
            (int)(feeAmt * 100),                            // FeeAmt
            "9180",                                         // SvcBranchId
            "KB" + (random.nextInt(900000) + 100000),       // AuthUserId
            "001",                                          // AuthLevel
            businessDate,                                    // ExtAcctDt
            "N",                                            // UseSvcBranch
            "001",                                          // ICA
            ""                                              // Filler
        );
    }

    private String generateTrailer(int totalRecords, double totalDebitSum, double totalCreditSum) {
        double totalSum = totalDebitSum + totalCreditSum;
        return String.format("%-3s%015d%018d%018d%018d%-478s",
            "T01",                         // RecType
            totalRecords,                  // TotalRec
            (int)(totalSum * 100),         // TotalSum
            (int)(totalDebitSum * 100),    // TotalDebitSum
            (int)(totalCreditSum * 100),   // TotalCreditSum
            ""                            // Filler
        );
    }

    public double generateFile(int numRecords, double debitRatio, double baseAmount) throws IOException {
        long startTime = System.currentTimeMillis();
        
        double totalDebitSum = 0;
        double totalCreditSum = 0;
        
        String filename = String.format("SHARC.EDCMP.FCS3D01.RETAIL.DEBIT.TCB_%d", numRecords);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(generateHeader());
            writer.newLine();
            
            // Transactions
            for (int i = 0; i < numRecords; i++) {
                boolean isDebit = random.nextDouble() < debitRatio;
                String operationType = isDebit ? "DR" : "CR";
                
                double trnAmt = baseAmount * (0.5 + random.nextDouble());
                double feeAmt = 10 + random.nextDouble() * 40;
                
                if (isDebit) {
                    totalDebitSum += trnAmt;
                } else {
                    totalCreditSum += trnAmt;
                }
                
                writer.write(generateTransaction(operationType, trnAmt, feeAmt));
                writer.newLine();
            }
            
            // Trailer
            writer.write(generateTrailer(numRecords, totalDebitSum, totalCreditSum));
        }
        
        return (System.currentTimeMillis() - startTime) / 1000.0;
    }

    public static void main(String[] args) {
        Ex1 generator = new Ex1();
        int[] recordCounts = {10000, 100000, 1000000};
        
        System.out.println("Starting generate test data...");
        System.out.printf("%-10s %-15s %-15s%n", "Records", "Time (seconds)", "Speed (records/sec)");
        System.out.println("-".repeat(80));
        
        for (int count : recordCounts) {
            try {
                double executionTime = generator.generateFile(count, 0.6, 1000);
                double speed = count / executionTime;
                System.out.printf("%-10d %-15.2f %.2f%n", count, executionTime, speed);
            } catch (IOException e) {
                System.err.println("Error generating file: " + e.getMessage());
            }
        }
        
        System.out.println("-".repeat(80));
    }
}