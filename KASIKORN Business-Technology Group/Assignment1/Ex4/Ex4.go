package main

import (
	"encoding/csv"
	"fmt"
	"math/rand"
	"os"
	"path/filepath"
	"runtime"
	"time"
)

// MemStats holds memory statistics
type MemStats struct {
	HeapAlloc    uint64
	TotalAlloc   uint64
	Sys          uint64
	NumGC        uint32
	PauseTotalNs uint64
}

// getMemStats returns current memory statistics
func getMemStats() MemStats {
	var m runtime.MemStats
	runtime.ReadMemStats(&m)
	return MemStats{
		HeapAlloc:    m.HeapAlloc,
		TotalAlloc:   m.TotalAlloc,
		Sys:          m.Sys,
		NumGC:        m.NumGC,
		PauseTotalNs: m.PauseTotalNs,
	}
}

// printMemStats prints memory statistics
func printMemStats(start, current MemStats) {
	fmt.Printf("\nMemory Usage:\n")
	fmt.Printf("Final Heap Allocation: %d MB\n", current.HeapAlloc/1024/1024)
	fmt.Printf("Total Memory Allocated: %d MB\n", current.TotalAlloc/1024/1024)
	fmt.Printf("System Memory: %d MB\n", current.Sys/1024/1024)
	fmt.Printf("GC Runs: %d\n", current.NumGC)
}

// CustomerDataGenerator holds the state for generating customer data
type CustomerDataGenerator struct {
	currentID       int
	provinceCodes   []string
	countryCodes    []string
	bankCodes       []string
	positions       []string
	companySizes    []string
	firstNames      []string
	lastNames       []string
	companyTypes    []string
	companySuffixes []string
}

// generateCustomerRecord generates a customer record
func (g *CustomerDataGenerator) generateCustomerRecord(isCorporate bool) map[string]string {
	record := make(map[string]string)
	if isCorporate {
		record["ID"] = fmt.Sprintf("C%06d", g.currentID)
		record["Type"] = "Corporate"
		record["CompanyName"] = fmt.Sprintf("%s %s %s", g.companyTypes[rand.Intn(len(g.companyTypes))], g.firstNames[rand.Intn(len(g.firstNames))], g.companySuffixes[rand.Intn(len(g.companySuffixes))])
		record["Position"] = g.positions[rand.Intn(len(g.positions))]
		record["CompanySize"] = g.companySizes[rand.Intn(len(g.companySizes))]
		record["BankCode"] = g.bankCodes[rand.Intn(len(g.bankCodes))]
		record["CountryCode"] = g.countryCodes[rand.Intn(len(g.countryCodes))]
		record["ProvinceCode"] = g.provinceCodes[rand.Intn(len(g.provinceCodes))]
	} else {
		record["ID"] = fmt.Sprintf("I%06d", g.currentID)
		record["Type"] = "Individual"
		record["FirstName"] = g.firstNames[rand.Intn(len(g.firstNames))]
		record["LastName"] = g.lastNames[rand.Intn(len(g.lastNames))]
		record["BankCode"] = g.bankCodes[rand.Intn(len(g.bankCodes))]
		record["CountryCode"] = g.countryCodes[rand.Intn(len(g.countryCodes))]
		record["ProvinceCode"] = g.provinceCodes[rand.Intn(len(g.provinceCodes))]
	}
	g.currentID++
	return record
}

func (g *CustomerDataGenerator) generateDataFile(numRecords int, outputFile string) (int, error) {
	startStats := getMemStats()

	file, err := os.Create(outputFile)
	if err != nil {
		return 0, fmt.Errorf("error creating file: %w", err)
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	numCorporate := int(float64(numRecords) * 0.05)
	numIndividual := numRecords - numCorporate

	// Generate one record to get headers
	sampleRecord := g.generateCustomerRecord(true)
	headers := make([]string, 0, len(sampleRecord))
	for key := range sampleRecord {
		headers = append(headers, key)
	}
	if err := writer.Write(headers); err != nil {
		return 0, fmt.Errorf("error writing headers: %w", err)
	}

	recordsWritten := 0

	// Write corporate records
	for i := 0; i < numCorporate; i++ {
		record := g.generateCustomerRecord(true)
		values := make([]string, len(headers))
		for i, header := range headers {
			values[i] = record[header]
		}
		if err := writer.Write(values); err != nil {
			return recordsWritten, fmt.Errorf("error writing corporate record: %w", err)
		}
		recordsWritten++
	}

	// Write individual records
	for i := 0; i < numIndividual; i++ {
		record := g.generateCustomerRecord(false)
		values := make([]string, len(headers))
		for i, header := range headers {
			values[i] = record[header]
		}
		if err := writer.Write(values); err != nil {
			return recordsWritten, fmt.Errorf("error writing individual record: %w", err)
		}
		recordsWritten++
	}

	// Print final memory stats
	finalStats := getMemStats()
	printMemStats(startStats, finalStats)

	return recordsWritten, nil
}

// NewCustomerDataGenerator initializes a new CustomerDataGenerator with sample data
func NewCustomerDataGenerator() *CustomerDataGenerator {
	return &CustomerDataGenerator{
		currentID:       1,
		provinceCodes:   []string{"01", "02", "03"},
		countryCodes:    []string{"TH", "US", "CN"},
		bankCodes:       []string{"001", "002", "003"},
		positions:       []string{"Manager", "Director", "CEO"},
		companySizes:    []string{"Small", "Medium", "Large"},
		firstNames:      []string{"John", "Jane", "Alex"},
		lastNames:       []string{"Doe", "Smith", "Brown"},
		companyTypes:    []string{"Tech", "Finance", "Retail"},
		companySuffixes: []string{"Inc", "LLC", "Corp"},
	}
}

func main() {
	rand.Seed(time.Now().UnixNano())
	generator := NewCustomerDataGenerator()
	sizes := []int{10000, 100000, 1000000}

	for _, size := range sizes {
		fmt.Printf("\nGenerating %d records...\n", size)
		outputFile := filepath.Join(".", fmt.Sprintf("customer_data_%drecords.csv", size))

		startTime := time.Now()
		recordsGenerated, err := generator.generateDataFile(size, outputFile)
		if err != nil {
			fmt.Printf("Error generating data file: %v\n", err)
			continue
		}
		duration := time.Since(startTime).Seconds()

		fmt.Printf("Generated %d records in %s\n", recordsGenerated, outputFile)
		fmt.Printf("Time taken: %.2f seconds\n", duration)
		fmt.Printf("Records per second: %.2f\n", float64(recordsGenerated)/duration)
	}
}
