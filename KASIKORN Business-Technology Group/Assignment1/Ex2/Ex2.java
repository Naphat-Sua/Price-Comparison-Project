package Assignment1.Ex2;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Ex2 {
    private final LocalDateTime currentDate;
    private final List<String> corporateBranches;
    private final List<String> individualBranches;
    private final List<String> provinces;
    private final List<String> positions;
    private final List<String> companySizes;
    private final Random random;

    public Ex2() {
        this.currentDate = LocalDateTime.now();
        this.corporateBranches = Collections.singletonList("80001");
        this.individualBranches = Collections.singletonList("90001");
        this.provinces = Arrays.asList("HN", "HCM", "DN", "HP", "CT");
        this.positions = Arrays.asList("Manager", "Staff", "Engineer", "Accountant", "Director");
        this.companySizes = Arrays.asList("S", "M", "L");
        this.random = new Random();
    }

    private String generateDateOfBirth() {
        int age = random.nextInt(41) + 20; // 20 to 60
        LocalDateTime birthDate = currentDate.minusDays(age * 365L + random.nextInt(365));
        return birthDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String generateRandomDate(int minDays, int maxDays) {
        LocalDateTime date = currentDate.minusDays(random.nextInt(maxDays - minDays) + minDays);
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private Map<String, String> generateCustomer(boolean isCorporate) {
        String customerId = (isCorporate ? "C" : "I") + String.format("%07d", random.nextInt(9000000) + 1000000);
        String incDate = generateRandomDate(365, 7300);

        Map<String, String> customer = new LinkedHashMap<>();
        customer.put("TransactionDate", currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        customer.put("BRID", isCorporate ? corporateBranches.get(0) : individualBranches.get(0));
        customer.put("CUSTID", customerId);
        customer.put("BANKCODE", "79601001");
        customer.put("RESIDENT", isCorporate ? "1" : (random.nextBoolean() ? "1" : "0"));
        customer.put("CUST_TYPE", isCorporate ? "P01" : "P02");
        customer.put("FULL_NAME", (isCorporate ? "Corporation " : "Individual Customer ") + customerId);
        customer.put("ADDRESS", isCorporate ? "Corporate Address" : "Personal Address");
        customer.put("PHONE", String.format("0%09d", random.nextInt(900000000) + 100000000));
        customer.put("EMAIL", String.format("%s_%s@email.com", isCorporate ? "corp" : "customer", customerId.toLowerCase()));
        customer.put("PROVINCE_CODE", provinces.get(random.nextInt(provinces.size())));
        customer.put("COUNTRY_CODE", "VN");
        customer.put("IDENTITY_NUM", String.format("%09d", random.nextInt(900000000) + 100000000));
        customer.put("IDENTITY_DATE", isCorporate ? incDate : generateRandomDate(365, 3650));
        customer.put("SIZEOFCO", isCorporate ? companySizes.get(random.nextInt(companySizes.size())) : "");
        customer.put("TOTAL_ASSETS", String.valueOf(random.nextInt(900000001) + 100000000));
        customer.put("TOTAL_INCOME", String.valueOf(random.nextInt(450000001) + 50000000));
        customer.put("TOTAL_LIABILITY", String.valueOf(random.nextInt(90000001) + 10000000));
        return customer;
    }

    public double generateFile(int numRecords, String filename) throws IOException {
        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        int numCorporate = (int) (numRecords * 0.05);
        int numIndividual = numRecords - numCorporate;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.join(",", generateCustomer(false).keySet()));
            writer.newLine();

            for (int i = 0; i < numCorporate; i++) {
                writer.write(String.join(",", generateCustomer(true).values()));
                writer.newLine();
            }
            for (int i = 0; i < numIndividual; i++) {
                writer.write(String.join(",", generateCustomer(false).values()));
                writer.newLine();
            }
        }

        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        double executionTime = (System.currentTimeMillis() - startTime) / 1000.0;
        double memoryUsage = (finalMemory - initialMemory) / (1024.0 * 1024.0);
        System.out.printf("Memory used: %.2f MB\n", memoryUsage);
        return executionTime;
    }

    public static void main(String[] args) {
        Ex2 generator = new Ex2();
        int[] recordCounts = {10000, 100000, 1000000};

        System.out.println("Starting generate test data...");
        System.out.printf("%-10s %-15s %-15s %-15s%n", "Records", "Time (sec)", "Speed (rec/sec)", "Memory (MB)");
        System.out.println("-".repeat(80));

        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = LocalDateTime.now().format(fileFormatter);
        Runtime runtime = Runtime.getRuntime();

        for (int count : recordCounts) {
            String filename = String.format("Customer_extract_%s_%d.csv", dateStr, count);
            try {
                long initialMemory = runtime.totalMemory() - runtime.freeMemory();
                double executionTime = generator.generateFile(count, filename);
                double speed = count / executionTime;
                long finalMemory = runtime.totalMemory() - runtime.freeMemory();
                double memoryUsage = (finalMemory - initialMemory) / (1024.0 * 1024.0);
                System.out.printf("%-10d %-15.2f %-15.2f %-15.2f%n", count, executionTime, speed, memoryUsage);
            } catch (IOException e) {
                System.err.println("Error generating file: " + e.getMessage());
            }
        }
        System.out.println("-".repeat(80));
    }
}
