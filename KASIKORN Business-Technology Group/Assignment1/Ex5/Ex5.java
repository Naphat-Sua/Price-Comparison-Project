package Assignment1.Ex5;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Record {
    String custId;
    String fullName;
    String custType;
    String identityNum;

    Record(String custId, String fullName, String custType, String identityNum) {
        this.custId = custId;
        this.fullName = fullName;
        this.custType = custType;
        this.identityNum = identityNum;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s", custId, fullName, custType, identityNum);
    }
}

class DataInLoop {
    private int currentId = 0;
    private final Random random = new Random();
    private final List<String> provinceCodes;
    private final List<String> countryCodes;
    private final List<String> bankCodes;
    private final List<String> positions;
    private final List<String> companySizes;
    private final List<String> firstNames;
    private final List<String> lastNames;
    private final List<String> companyTypes;

    public DataInLoop() {
        this.provinceCodes = Arrays.asList("BKK", "CNX", "NTB", "PTT", "NKP");
        this.countryCodes = Arrays.asList("TH", "US", "GB", "JP", "SG");
        this.bankCodes = Arrays.asList("79601001", "79601002", "79601003");
        this.positions = Arrays.asList("Manager", "Staff", "Director", "Supervisor");
        this.companySizes = Arrays.asList("S", "M", "L");
        this.firstNames = Arrays.asList("Somchai", "Arthit", "Kittisak", "Naphat", "Mali");
        this.lastNames = Arrays.asList("Kasikorn", "KBTG", "K+", "KhunThong", "MeowJot");
        this.companyTypes = Arrays.asList("Trading", "Technology", "Services");
    }

    private String generateThaiId() {
        currentId++;
        int prefix = random.nextInt(9) + 1;
        String middle = String.format("%09d", currentId);
        int checkDigit = random.nextInt(10);
        return prefix + middle + checkDigit;
    }

    private String generatePersonName() {
        return firstNames.get(random.nextInt(firstNames.size())) + " " +
               lastNames.get(random.nextInt(lastNames.size()));
    }

    private String generateCompanyName() {
        String companyType = companyTypes.get(random.nextInt(companyTypes.size()));
        String suffix = Arrays.asList("Group", "Corp", "Co", "PLC").get(random.nextInt(4));
        return String.format("%s %s %d", companyType, suffix, currentId);
    }

    public int generateDataFile(int numRecords, String outputFile) throws IOException {
        List<Record> records = new ArrayList<>();
        int numCorporate = (int)(numRecords * 0.05);
        int numIndividual = numRecords - numCorporate;

        for (int i = 0; i < numRecords; i++) {
            boolean isCorporate = i < numCorporate;
            Record record;

            if (isCorporate) {
                String companyName = generateCompanyName();
                record = new Record(
                    "C" + generateThaiId(),
                    companyName,
                    "P01",
                    generateThaiId()
                );
            } else {
                record = new Record(
                    "I" + generateThaiId(),
                    generatePersonName(),
                    "P02",
                    generateThaiId()
                );
            }
            records.add(record);
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("CUSTID,FULL_NAME,CUST_TYPE,IDENTITY_NUM\n");
            for (Record record : records) {
                writer.write(record.toString() + "\n");
            }
        }

        return records.size();
    }
}

class DataOutLoop {
    private int currentId = 0;
    private final Random random = new Random();
    private final List<String> provinceCodes;
    private final List<String> countryCodes;
    private final List<String> bankCodes;
    private final List<String> positions;
    private final List<String> companySizes;
    private final List<String> personNames;
    private final List<String> companyBaseNames;
    private final List<Integer> idPrefixes;
    private final List<Integer> idCheckDigits;

    public DataOutLoop() {
        this.provinceCodes = Arrays.asList("BKK", "CNX", "NTB", "PTT", "NKP");
        this.countryCodes = Arrays.asList("TH", "US", "GB", "JP", "SG");
        this.bankCodes = Arrays.asList("79601001", "79601002", "79601003");
        this.positions = Arrays.asList("Manager", "Staff", "Director", "Supervisor");
        this.companySizes = Arrays.asList("S", "M", "L");

        List<String> firstNames = Arrays.asList("Somchai", "Arthit", "Kittisak", "Naphat", "Mali");
        List<String> lastNames = Arrays.asList("Kasikorn", "KBTG", "K+", "KhunThong", "MeowJot");
        
        this.personNames = firstNames.stream()
            .flatMap(f -> lastNames.stream().map(l -> f + " " + l))
            .collect(Collectors.toList());

        List<String> companyTypes = Arrays.asList("Trading", "Technology", "Services");
        List<String> companySuffixes = Arrays.asList("Group", "Corp", "Co", "PLC");
        
        this.companyBaseNames = companyTypes.stream()
            .flatMap(t -> companySuffixes.stream().map(s -> t + " " + s))
            .collect(Collectors.toList());

        this.idPrefixes = IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList());
        this.idCheckDigits = IntStream.range(0, 10).boxed().collect(Collectors.toList());
    }

    private List<String> generateBatchIds(int batchSize) {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < batchSize; i++) {
            currentId++;
            ids.add(String.format("%d%09d%d",
                idPrefixes.get(random.nextInt(idPrefixes.size())),
                currentId,
                idCheckDigits.get(random.nextInt(idCheckDigits.size()))
            ));
        }
        return ids;
    }

    public int generateDataFile(int numRecords, String outputFile) throws IOException {
        List<Record> records = new ArrayList<>();
        int numCorporate = (int)(numRecords * 0.05);
        int numIndividual = numRecords - numCorporate;

        List<String> allIds = generateBatchIds(numRecords * 2);
        int idIndex = 0;

        for (int i = 0; i < numCorporate; i++) {
            String companyName = companyBaseNames.get(random.nextInt(companyBaseNames.size())) + " " + (i + 1);
            records.add(new Record(
                "C" + allIds.get(idIndex),
                companyName,
                "P01",
                allIds.get(idIndex + 1)
            ));
            idIndex += 2;
        }

        for (int i = 0; i < numIndividual; i++) {
            records.add(new Record(
                "I" + allIds.get(idIndex),
                personNames.get(random.nextInt(personNames.size())),
                "P02",
                allIds.get(idIndex + 1)
            ));
            idIndex += 2;
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("CUSTID,FULL_NAME,CUST_TYPE,IDENTITY_NUM\n");
            for (Record record : records) {
                writer.write(record.toString() + "\n");
            }
        }

        return records.size();
    }
}

class Result {
    int size;
    double duration;
    double recordsPerSecond;
    long memoryMb;

    Result(int size, double duration, double recordsPerSecond, long memoryMb) {
        this.size = size;
        this.duration = duration;
        this.recordsPerSecond = recordsPerSecond;
        this.memoryMb = memoryMb;
    }
}

public class Ex5 {
    private static List<Result> runPerformanceTest(String className, List<Integer> sizes) {
        List<Result> results = new ArrayList<>();
        Object generator = className.equals("DataInLoop") ? new DataInLoop() : new DataOutLoop();

        for (int size : sizes) {
            System.out.printf("\nGenerating %d records using %s...\n", size, className);
            
            long startTime = Instant.now().toEpochMilli();
            String outputFile = String.format("customer_data_%s_%drecords.csv", className, size);
            
            try {
                int recordsGenerated;
                if (generator instanceof DataInLoop) {
                    recordsGenerated = ((DataInLoop)generator).generateDataFile(size, outputFile);
                } else {
                    recordsGenerated = ((DataOutLoop)generator).generateDataFile(size, outputFile);
                }

                double duration = (Instant.now().toEpochMilli() - startTime) / 1000.0;
                long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                memoryUsage = memoryUsage / (1024 * 1024); // Convert to MB

                Result result = new Result(
                    size,
                    duration,
                    recordsGenerated / duration,
                    memoryUsage
                );
                results.add(result);

                System.out.printf("Generated %d records in %.2f seconds\n", recordsGenerated, duration);
                System.out.printf("Speed: %.2f records/second\n", recordsGenerated / duration);
                System.out.printf("Memory usage: %d MB\n", memoryUsage);

            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }
        }

        return results;
    }

    public static void main(String[] args) {
        List<Integer> sizes = Arrays.asList(10000, 100000, 1000000);

        System.out.println("Testing function in loop...");
        List<Result> inLoopResults = runPerformanceTest("DataInLoop", sizes);

        System.out.println("\nTesting function out loop...");
        List<Result> optimizedResults = runPerformanceTest("DataOutLoop", sizes);

        // Compare results
        System.out.println("\nPerformance Comparison:");
        System.out.println("\nSize\tMethod\t\tDuration\tSpeed\t\tMemory");
        System.out.println("-".repeat(70));

        for (int i = 0; i < sizes.size(); i++) {
            Result inLoop = inLoopResults.get(i);
            Result opt = optimizedResults.get(i);

            System.out.printf("%,d\tIn Loop\t\t%.2fs\t%.0f r/s\t%dMB\n",
                inLoop.size, inLoop.duration, inLoop.recordsPerSecond, inLoop.memoryMb);
            System.out.printf("%,d\tOptimized\t%.2fs\t%.0f r/s\t%dMB\n",
                opt.size, opt.duration, opt.recordsPerSecond, opt.memoryMb);
            System.out.printf("Speedup: %.2fx\n", inLoop.duration / opt.duration);
            System.out.println("-".repeat(70));
        }
    }
}