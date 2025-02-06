package main

import (
	"encoding/csv"
	"fmt"
	"math/rand"
	"os"
	"path/filepath"
	"runtime"
	"strings"
	"time"
)

// Ex3 represents the customer data generator
type Ex3 struct {
	currentDate   time.Time
	branches      map[string][]string
	provinceCodes []string
	positions     []string
	companySizes  []string
	sicGroups     []string
	fieldnames    []string
}

// NewEx3 creates a new instance of Ex3
func NewEx3() *Ex3 {
	return &Ex3{
		currentDate: time.Now(),
		branches: map[string][]string{
			"corporate":  {"80001"},
			"individual": {"90001"},
		},
		provinceCodes: []string{"BKK", "CNX", "NTB", "PTT", "NKP"},
		positions:     []string{"Manager", "Staff", "Engineer", "Accountant", "Director"},
		companySizes:  []string{"S", "M", "L"},
		sicGroups:     []string{"9", "K", "A", "C"},
	}
}

// generateDateOfBirth generates a random date of birth
func (e *Ex3) generateDateOfBirth() string {
	age := rand.Intn(41) + 20 // 20 to 60 years
	days := age*365 + rand.Intn(365)
	birthDate := e.currentDate.AddDate(0, 0, -days)
	return birthDate.Format("2006-01-02")
}

// getMemoryUsage returns current memory usage in MB
func getMemoryUsage() float64 {
	var m runtime.MemStats
	runtime.ReadMemStats(&m)
	return float64(m.Alloc) / 1024 / 1024
}

// generateWithinLoop generates customer data using a loop approach
func (e *Ex3) generateWithinLoop(numRecords int, filename string) map[string]float64 {
	startTime := time.Now()
	startMemory := getMemoryUsage()

	numCorporate := int(float64(numRecords) * 0.05)

	file, err := os.Create(filename)
	if err != nil {
		panic(err)
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	// Write header
	writer.Write([]string{
		"TransactionDate", "BRID", "CUSTID", "CUST_TYPE", "FULL_NAME",
		"BIRTHDAY", "ADDRESS", "PROVINCE_CODE", "TOTAL_ASSETS",
	})

	for i := 0; i < numRecords; i++ {
		isCorporate := i < numCorporate
		transDate := time.Now().Format("2006-01-02")

		var brid, custType, birthday string
		if isCorporate {
			brid = "80001"
			custType = "P01"
			birthday = ""
		} else {
			brid = "90001"
			custType = "P02"
			birthday = e.generateDateOfBirth()
		}

		custID := fmt.Sprintf("%s%d",
			map[bool]string{true: "C", false: "I"}[isCorporate],
			rand.Intn(9000000)+1000000,
		)

		fullName := fmt.Sprintf("%s %s",
			map[bool]string{true: "Corporation", false: "Individual"}[isCorporate],
			custID,
		)

		address := fmt.Sprintf("%s Address %d",
			map[bool]string{true: "Corporate", false: "Home"}[isCorporate],
			rand.Intn(1000)+1,
		)

		provinces := []string{"HN", "HCM", "DN", "HP", "CT"}
		province := provinces[rand.Intn(len(provinces))]

		var totalAssets int
		if isCorporate {
			totalAssets = rand.Intn(9000000000) + 1000000000
		} else {
			totalAssets = rand.Intn(900000000) + 100000000
		}

		writer.Write([]string{
			transDate,
			brid,
			custID,
			custType,
			fullName,
			birthday,
			address,
			province,
			fmt.Sprintf("%d", totalAssets),
		})
	}

	endTime := time.Now()
	endMemory := getMemoryUsage()

	return map[string]float64{
		"execution_time": endTime.Sub(startTime).Seconds(),
		"memory_used":    endMemory - startMemory,
	}
}

// generateWithPredefined generates customer data using predefined values
func (e *Ex3) generateWithPredefined(numRecords int, filename string) map[string]float64 {
	startTime := time.Now()
	startMemory := getMemoryUsage()

	transDate := e.currentDate.Format("2006-01-02")
	numCorporate := int(float64(numRecords) * 0.05)

	customerTypes := map[string]string{
		"corporate":  "P01",
		"individual": "P02",
	}

	addressPrefixes := map[string]string{
		"corporate":  "Corporate Address",
		"individual": "Home Address",
	}

	file, err := os.Create(filename)
	if err != nil {
		panic(err)
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	// Write header
	writer.Write([]string{
		"TransactionDate", "BRID", "CUSTID", "CUST_TYPE", "FULL_NAME",
		"BIRTHDAY", "ADDRESS", "PROVINCE_CODE", "TOTAL_ASSETS",
	})

	for i := 0; i < numRecords; i++ {
		isCorporate := i < numCorporate
		custType := map[bool]string{true: "corporate", false: "individual"}[isCorporate]

		custID := fmt.Sprintf("%s%d",
			map[bool]string{true: "C", false: "I"}[isCorporate],
			rand.Intn(9000000)+1000000,
		)

		var totalAssets int
		if isCorporate {
			totalAssets = rand.Intn(9000000000) + 1000000000
		} else {
			totalAssets = rand.Intn(900000000) + 100000000
		}

		writer.Write([]string{
			transDate,
			e.branches[custType][0],
			custID,
			customerTypes[custType],
			fmt.Sprintf("%s Customer %d", capitalize(custType), i+1),
			map[bool]string{true: "", false: e.generateDateOfBirth()}[isCorporate],
			fmt.Sprintf("%s %d", addressPrefixes[custType], rand.Intn(1000)+1),
			e.provinceCodes[rand.Intn(len(e.provinceCodes))],
			fmt.Sprintf("%d", totalAssets),
		})
	}

	endTime := time.Now()
	endMemory := getMemoryUsage()

	return map[string]float64{
		"execution_time": endTime.Sub(startTime).Seconds(),
		"memory_used":    endMemory - startMemory,
	}
}

// capitalize capitalizes the first letter of a string
func capitalize(s string) string {
	if len(s) == 0 {
		return s
	}
	return string(s[0]-32) + s[1:]
}

func main() {
	rand.Seed(time.Now().UnixNano())
	generator := NewEx3()
	recordCounts := []int{10000, 100000, 1000000}

	fmt.Println("\nPerformance Comparison Results:")
	fmt.Println(strings.Repeat("-", 80))
	fmt.Printf("%-10s %-15s %-12s %-12s %s\n", "Records", "Approach", "Time (sec)", "Memory (MB)", "Speed (rec/sec)")
	fmt.Println(strings.Repeat("-", 80))

	for _, count := range recordCounts {
		result1 := generator.generateWithinLoop(
			count,
			filepath.Join(".", fmt.Sprintf("customer_data_Within_Loop_%d.csv", count)),
		)

		result2 := generator.generateWithPredefined(
			count,
			filepath.Join(".", fmt.Sprintf("customer_data_Predefined_%d.csv", count)),
		)

		fmt.Printf("%-10d %-15s %-12.2f %-12.2f %.2f\n",
			count, "Within Loop", result1["execution_time"], result1["memory_used"],
			float64(count)/result1["execution_time"])
		fmt.Printf("%-10s %-15s %-12.2f %-12.2f %.2f\n",
			"", "Pre-defined", result2["execution_time"], result2["memory_used"],
			float64(count)/result2["execution_time"])
		fmt.Println(strings.Repeat("-", 80))
	}
}
