package main

import (
	"encoding/csv"
	"fmt"
	"math/rand"
	"os"
	"runtime"
	"strings"
	"time"
)

type Ex2 struct {
	currentDate time.Time
}

func NewEx2() *Ex2 {
	return &Ex2{
		currentDate: time.Now(),
	}
}

func (e *Ex2) generateRandomCustomer() []string {
	return []string{
		e.currentDate.Format("2006-01-02"),                // TransactionDate
		fmt.Sprintf("C%07d", rand.Intn(9000000)+1000000),  // CUSTID
		fmt.Sprintf("%d", rand.Intn(900000000)+100000000), // INCOME
	}
}

func (e *Ex2) getHeaders() []string {
	return []string{"TransactionDate", "CUSTID", "INCOME"}
}

func (e *Ex2) generateFile(numRecords int, filename string) (float64, uint64, error) {
	startTime := time.Now()

	var memStart, memEnd runtime.MemStats
	runtime.ReadMemStats(&memStart)

	file, err := os.Create(filename)
	if err != nil {
		return 0, 0, fmt.Errorf("error creating file: %v", err)
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	if err := writer.Write(e.getHeaders()); err != nil {
		return 0, 0, fmt.Errorf("error writing header: %v", err)
	}

	for i := 0; i < numRecords; i++ {
		if err := writer.Write(e.generateRandomCustomer()); err != nil {
			return 0, 0, fmt.Errorf("error writing customer: %v", err)
		}
	}

	runtime.ReadMemStats(&memEnd)
	memUsed := memEnd.Alloc - memStart.Alloc
	executionTime := time.Since(startTime).Seconds()
	return executionTime, memUsed, nil
}

func main() {
	rand.Seed(time.Now().UnixNano())
	generator := NewEx2()
	recordCounts := []int{10000, 100000, 1000000}

	fmt.Println("Starting generate test data...")
	fmt.Printf("%-10s %-15s %-15s %-15s\n", "Records", "Time (s)", "Speed (rec/s)", "RAM (MB)")
	fmt.Println(strings.Repeat("-", 80))

	for _, count := range recordCounts {
		filename := fmt.Sprintf("Customer_extract_%s_%d.csv", time.Now().Format("20060102"), count)
		executionTime, memUsed, err := generator.generateFile(count, filename)
		if err != nil {
			fmt.Printf("Error generating file for %d records: %v\n", count, err)
			continue
		}
		speed := float64(count) / executionTime
		ramUsageMB := float64(memUsed) / (1024 * 1024)
		fmt.Printf("%-10d %-15.2f %-15.2f %-15.2f\n", count, executionTime, speed, ramUsageMB)
	}

	fmt.Println(strings.Repeat("-", 80))
}
