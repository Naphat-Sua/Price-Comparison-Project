package Assignment1.Ex3;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

class CustomerDataGenerator {
    private static final List<String> PROVINCE_CODES = Arrays.asList("BKK", "CNX", "NTB", "PTT", "NKP");
    private static final List<String> POSITIONS = Arrays.asList("Manager", "Staff", "Engineer", "Accountant", "Director");
    private static final List<String> COMPANY_SIZES = Arrays.asList("S", "M", "L");
    private static final List<String> SIC_GROUPS = Arrays.asList("9", "K", "A", "C");

    private LocalDate currentDate;
    private Map<String, List<String>> branches;

    public CustomerDataGenerator() {
        this.currentDate = LocalDate.now();
        this.branches = new HashMap<>();
        this.branches.put("corporate", Arrays.asList("80001"));
        this.branches.put("individual", Arrays.asList("90001"));
    }

    private String generateDateOfBirth() {
        Random random = new Random();
        int age = random.nextInt(41) + 20; // Age between 20 to 60
        LocalDate birthDate = currentDate.minusYears(age).minusDays(random.nextInt(365));
        return birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private Map<String, Double> generateCustomerData(int numRecords, String filename, boolean usePredefined) {
        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        int numCorporate = (int) (numRecords * 0.05); // 5% corporate

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write header
            writer.write("TransactionDate,BRID,CUSTID,CUST_TYPE,FULL_NAME,BIRTHDAY,ADDRESS,PROVINCE_CODE,TOTAL_ASSETS\n");

            Random random = new Random();
            String transDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

            for (int i = 0; i < numRecords; i++) {
                boolean isCorporate = i < numCorporate;
                String custType = isCorporate ? "P01" : "P02";
                String brid = isCorporate ? "80001" : "90001";
                String custId = String.format("%s%07d", isCorporate ? "C" : "I", random.nextInt(9000000) + 1000000);
                String fullName = String.format("%s %s", isCorporate ? "Corporation" : "Individual", custId);
                String birthday = isCorporate ? "" : generateDateOfBirth();
                String address = String.format("%s Address %d", isCorporate ? "Corporate" : "Home", random.nextInt(1000) + 1);
                String province = PROVINCE_CODES.get(random.nextInt(PROVINCE_CODES.size()));
                long totalAssets = isCorporate ? random.nextLong(9000000000L) + 1000000000L : random.nextLong(900000000L) + 100000000L;

                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%d\n",
                        transDate, brid, custId, custType, fullName,
                        birthday, address, province, totalAssets));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        Map<String, Double> results = new HashMap<>();
        results.put("execution_time", (endTime - startTime) / 1000.0);
        results.put("memory_used", (endMemory - startMemory) / (1024.0 * 1024.0));

        return results;
    }

    public Map<String, Double> generateWithLoop(int numRecords, String filename) {
        return generateCustomerData(numRecords, filename, false); // Use loop-based generation
    }

    public Map<String, Double> generateWithPredefined(int numRecords, String filename) {
        return generateCustomerData(numRecords, filename, true); // Use predefined values generation
    }

    public static void main(String[] args) {
        CustomerDataGenerator generator = new CustomerDataGenerator();
        int[] recordCounts = {10000, 100000, 1000000};

        System.out.println("\nPerformance Comparison Results:");
        System.out.println("-".repeat(80));
        System.out.printf("%-10s %-15s %-12s %-12s %s%n", 
            "Records", "Approach", "Time (sec)", "Memory (MB)", "Speed (rec/sec)");
        System.out.println("-".repeat(80));

        for (int count : recordCounts) {
            Map<String, Double> result1 = generator.generateWithLoop(
                count,
                String.format("customer_data_Loop_%d.csv", count)
            );

            Map<String, Double> result2 = generator.generateWithPredefined(
                count,
                String.format("customer_data_Predefined_%d.csv", count)
            );

            System.out.printf("%-10d %-15s %-12.2f %-12.2f %.2f%n",
                count, "Loop",
                result1.get("execution_time"),
                result1.get("memory_used"),
                count / result1.get("execution_time"));

            System.out.printf("%-10s %-15s %-12.2f %-12.2f %.2f%n",
                "", "Predefined",
                result2.get("execution_time"),
                result2.get("memory_used"),
                count / result2.get("execution_time"));

            System.out.println("-".repeat(80));
        }
    }
}
