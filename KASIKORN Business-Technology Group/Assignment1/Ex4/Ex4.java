package Assignment1.Ex4;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Ex4 {
    private int currentId = 0;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Pre-defined data
    private final String[] provinceCodes = {"HN", "HCM", "DN", "HP", "CT"};
    private final String[] countryCodes = {"VN", "US", "GB", "JP", "SG"};
    private final String[] bankCodes = {"79601001", "79601002", "79601003"};
    private final String[] positions = {"Manager", "Staff", "Director", "Supervisor", "Specialist"};
    private final String[] companySizes = {"S", "M", "L"};
    
    // Simulated name data
    private final String[] firstNames = {"Minh", "Linh", "Tuan", "Hoa", "Nam", "Mai", "Duc", "Lan", "Thanh", "Hong"};
    private final String[] lastNames = {"Nguyen", "Tran", "Le", "Pham", "Hoang", "Phan", "Vu", "Dang", "Bui", "Do"};
    private final String[] companyTypes = {"Trading", "Technology", "Services", "Manufacturing", "Consulting"};

    private String generateThaiId() {
        currentId++;
        int prefix = ThreadLocalRandom.current().nextInt(1, 10);
        String middle = String.format("%09d", currentId);
        int checkDigit = ThreadLocalRandom.current().nextInt(10);
        return String.format("%d%s%d", prefix, middle, checkDigit);
    }

    private String generatePersonName() {
        return lastNames[ThreadLocalRandom.current().nextInt(lastNames.length)] + " " +
               firstNames[ThreadLocalRandom.current().nextInt(firstNames.length)];
    }

    private String generateCompanyName() {
        String companyType = companyTypes[ThreadLocalRandom.current().nextInt(companyTypes.length)];
        String suffix = new String[]{"Group", "Corporation", "Company", "Enterprise"}[ThreadLocalRandom.current().nextInt(4)];
        return String.format("%s %s %d", companyType, suffix, currentId);
    }

    private Map<String, String> generateCustomerRecord(boolean isCorporate) {
        Map<String, String> record = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        
        if (isCorporate) {
            String companyName = generateCompanyName();
            record.put("TransactionDate", today.format(dateFormatter));
            record.put("BRID", "80001");
            record.put("CUSTID", "C" + generateThaiId());
            record.put("BANKCODE", bankCodes[ThreadLocalRandom.current().nextInt(bankCodes.length)]);
            record.put("RESIDENT", String.valueOf(ThreadLocalRandom.current().nextInt(2)));
            record.put("CUST_TYPE", "P01");
            record.put("BIRTHDAY", "");
            record.put("FULL_NAME", companyName);
            record.put("TRADENAME", "Trading As " + companyName);
            record.put("ABBNAME", "CO" + currentId);
            record.put("SEX", "");
            record.put("ADDRESS", String.format("Building %d, Street %d, %s",
                    ThreadLocalRandom.current().nextInt(1, 1000),
                    ThreadLocalRandom.current().nextInt(1, 101),
                    provinceCodes[ThreadLocalRandom.current().nextInt(provinceCodes.length)]));
            record.put("PHONE", String.format("+84%09d", ThreadLocalRandom.current().nextInt(100000000, 1000000000)));
            record.put("EMAIL", String.format("contact%d@%s.com", 
                    currentId, companyName.toLowerCase().replace(" ", "")));
            record.put("PROVINCE_CODE", provinceCodes[ThreadLocalRandom.current().nextInt(provinceCodes.length)]);
            record.put("COUNTRY_CODE", countryCodes[ThreadLocalRandom.current().nextInt(countryCodes.length)]);
            record.put("IDENTITY_NUM", String.format("%010d", ThreadLocalRandom.current().nextInt(1000000000, 2000000000)));
            record.put("IDENTITY_DATE", today.minusDays(ThreadLocalRandom.current().nextInt(365, 3651)).format(dateFormatter));
            record.put("SIZEOFCO", companySizes[ThreadLocalRandom.current().nextInt(companySizes.length)]);
            record.put("TOTAL_EMPLOYEE", String.valueOf(ThreadLocalRandom.current().nextInt(10, 1001)));
            record.put("TOTAL_ASSETS", String.format("%.2f", ThreadLocalRandom.current().nextDouble(1000000, 100000001)));
            record.put("CAPITAL", String.format("%.2f", ThreadLocalRandom.current().nextDouble(100000, 10000001)));
            record.put("ECOTYPE", String.format("IN%02d", ThreadLocalRandom.current().nextInt(1, 100)));
            record.put("OPNDATE", today.minusDays(ThreadLocalRandom.current().nextInt(365, 3651)).format(dateFormatter));
            record.put("INC_DATE", today.minusDays(ThreadLocalRandom.current().nextInt(365, 3651)).format(dateFormatter));
            record.put("TAXCODE", String.format("%010d", ThreadLocalRandom.current().nextInt(1000000000, 2000000000)));
        } else {
            LocalDate birthDate = today.minusDays(ThreadLocalRandom.current().nextInt(20 * 365, 60 * 365 + 1));
            String fullName = generatePersonName();
            
            record.put("TransactionDate", today.format(dateFormatter));
            record.put("BRID", "90001");
            record.put("CUSTID", "I" + generateThaiId());
            record.put("BANKCODE", bankCodes[ThreadLocalRandom.current().nextInt(bankCodes.length)]);
            record.put("RESIDENT", String.valueOf(ThreadLocalRandom.current().nextInt(2)));
            record.put("CUST_TYPE", "P02");
            record.put("BIRTHDAY", birthDate.format(dateFormatter));
            record.put("FULL_NAME", fullName);
            record.put("TRADENAME", "");
            record.put("ABBNAME", "");
            record.put("SEX", String.valueOf(ThreadLocalRandom.current().nextInt(2)));
            record.put("ADDRESS", String.format("House %d, Street %d, %s",
                    ThreadLocalRandom.current().nextInt(1, 1000),
                    ThreadLocalRandom.current().nextInt(1, 101),
                    provinceCodes[ThreadLocalRandom.current().nextInt(provinceCodes.length)]));
            record.put("PHONE", String.format("+84%09d", ThreadLocalRandom.current().nextInt(100000000, 1000000000)));
            record.put("EMAIL", String.format("%s%d@example.com",
                    fullName.toLowerCase().replace(" ", "."),
                    ThreadLocalRandom.current().nextInt(1, 1000)));
            record.put("PROVINCE_CODE", provinceCodes[ThreadLocalRandom.current().nextInt(provinceCodes.length)]);
            record.put("COUNTRY_CODE", countryCodes[ThreadLocalRandom.current().nextInt(countryCodes.length)]);
            record.put("IDENTITY_NUM", generateThaiId());
            record.put("IDENTITY_DATE", today.minusDays(ThreadLocalRandom.current().nextInt(365, 3651)).format(dateFormatter));
            record.put("POSITION", positions[ThreadLocalRandom.current().nextInt(positions.length)]);
            record.put("INCOME", String.format("%.2f", ThreadLocalRandom.current().nextDouble(15000, 150001)));
            record.put("TAXCODE", String.format("%010d", ThreadLocalRandom.current().nextInt(1000000000, 2000000000)));
            record.put("OPNDATE", today.minusDays(ThreadLocalRandom.current().nextInt(365, 3651)).format(dateFormatter));
        }
        
        return record;
    }

    public int generateDataFile(int numRecords, String outputFile) throws IOException {
        int numCorporate = (int) (numRecords * 0.05);
        int numIndividual = numRecords - numCorporate;
        List<Map<String, String>> records = new ArrayList<>();

        // Generate corporate records
        for (int i = 0; i < numCorporate; i++) {
            records.add(generateCustomerRecord(true));
        }

        // Generate individual records
        for (int i = 0; i < numIndividual; i++) {
            records.add(generateCustomerRecord(false));
        }

        // Write to CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write header
            if (!records.isEmpty()) {
                writer.write(String.join(",", records.get(0).keySet()));
                writer.newLine();
            }

            // Write records
            for (Map<String, String> record : records) {
                writer.write(String.join(",", record.values()));
                writer.newLine();
            }
        }

        return records.size();
    }

    public static void main(String[] args) {
        Ex4 generator = new Ex4();
        int[] sizes = {10000, 100000, 1000000};

        for (int size : sizes) {
            System.out.printf("%nGenerating %d records...%n", size);
            long startTime = System.currentTimeMillis();
            String outputFile = String.format("customer_data_%drecords.csv", size);

            try {
                int recordsGenerated = generator.generateDataFile(size, outputFile);
                long endTime = System.currentTimeMillis();
                double duration = (endTime - startTime) / 1000.0;

                System.out.printf("Generated %d records in %s%n", recordsGenerated, outputFile);
                System.out.printf("Time taken: %.2f seconds%n", duration);
                System.out.printf("Records per second: %.2f%n", recordsGenerated / duration);
            } catch (IOException e) {
                System.err.println("Error generating data file: " + e.getMessage());
            }
        }
    }
}