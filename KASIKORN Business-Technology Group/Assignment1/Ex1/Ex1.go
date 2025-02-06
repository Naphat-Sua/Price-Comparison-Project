package main

import (
	"fmt"
	"math/rand"
	"os"
	"runtime"
	"strings"
	"time"
)

type Ex1 struct {
	currentDate  time.Time
	businessDate string
	systemDate   string
}

func NewEx1() *Ex1 {
	now := time.Now()
	return &Ex1{
		currentDate:  now,
		businessDate: now.Format("2006-01-02"),
		systemDate:   now.Format("2006-01-02T15:04:05"),
	}
}

func (e *Ex1) generateHeader() string {
	return fmt.Sprintf("%-3s%-33s%-10s%-5s%-8s%-6s%-485s",
		"H01", e.systemDate, e.businessDate, "494", "AcctInf", "000001", "",
	)
}

func (e *Ex1) generateTransaction(operationType string, trnAmt, feeAmt float64) string {
	srcUid := fmt.Sprintf("500022223455123_5hu12e2%04d", rand.Intn(9000)+1000)
	rqUid := fmt.Sprintf("494_%s_%06d",
		e.currentDate.Format("20060102"),
		rand.Intn(900000)+100000)

	return fmt.Sprintf("%-40s%-47s%-2s%-2s%-4s%-8s%-8s%-10s%-45s%-55s%010d%018d%018d%-4s%-15s%-3s%-10s%-1s%-3s%-247s",
		srcUid, rqUid, operationType, "01", "0001", "K0999999", "A04CIS01", e.businessDate,
		"Transaction for testing", fmt.Sprintf("TEST%d", rand.Intn(9000)+1000),
		rand.Intn(9000000)+1000000000, int(trnAmt*100), int(feeAmt*100),
		"9180", fmt.Sprintf("KB%d", rand.Intn(900000)+100000), "001", e.businessDate, "N", "001", "",
	)
}

func (e *Ex1) generateTrailer(totalRecords int, totalDebitSum, totalCreditSum float64) string {
	totalSum := totalDebitSum + totalCreditSum
	return fmt.Sprintf("%-3s%015d%018d%018d%018d%-478s",
		"T01", totalRecords, int(totalSum*100), int(totalDebitSum*100), int(totalCreditSum*100), "",
	)
}

func (e *Ex1) generateFile(numRecords int, debitRatio, baseAmount float64) (float64, float64, error) {
	startTime := time.Now()

	// Memory tracking before execution
	var memBefore runtime.MemStats
	runtime.ReadMemStats(&memBefore)

	var totalDebitSum, totalCreditSum float64

	filename := fmt.Sprintf("SHARC.EDCMP.FCS3D01.RETAIL.DEBIT.TCB_%d", numRecords)
	file, err := os.Create(filename)
	if err != nil {
		return 0, 0, fmt.Errorf("error creating file: %v", err)
	}
	defer file.Close()

	// Write header
	if _, err := fmt.Fprintln(file, e.generateHeader()); err != nil {
		return 0, 0, fmt.Errorf("error writing header: %v", err)
	}

	// Write transactions
	for i := 0; i < numRecords; i++ {
		isDebit := rand.Float64() < debitRatio
		operationType := "CR"
		if isDebit {
			operationType = "DR"
		}

		trnAmt := baseAmount * (0.5 + rand.Float64())
		feeAmt := 10 + rand.Float64()*40

		if isDebit {
			totalDebitSum += trnAmt
		} else {
			totalCreditSum += trnAmt
		}

		transaction := e.generateTransaction(operationType, trnAmt, feeAmt)
		if _, err := fmt.Fprintln(file, transaction); err != nil {
			return 0, 0, fmt.Errorf("error writing transaction: %v", err)
		}
	}

	// Write trailer
	trailer := e.generateTrailer(numRecords, totalDebitSum, totalCreditSum)
	if _, err := fmt.Fprint(file, trailer); err != nil {
		return 0, 0, fmt.Errorf("error writing trailer: %v", err)
	}

	// Memory tracking after execution
	var memAfter runtime.MemStats
	runtime.ReadMemStats(&memAfter)

	executionTime := time.Since(startTime).Seconds()
	memoryUsed := float64(memAfter.Alloc-memBefore.Alloc) / (1024 * 1024) // Convert bytes to MB

	return executionTime, memoryUsed, nil
}

func main() {
	rand.Seed(time.Now().UnixNano())
	generator := NewEx1()
	recordCounts := []int{10000, 100000, 1000000}

	fmt.Println("Starting generate test data...")
	fmt.Printf("%-10s %-15s %-15s %-15s\n", "Records", "Time (seconds)", "RAM Used (MB)", "Speed (records/sec)")
	fmt.Println(strings.Repeat("-", 80))

	for _, count := range recordCounts {
		executionTime, memoryUsed, err := generator.generateFile(count, 0.6, 1000)
		if err != nil {
			fmt.Printf("Error generating file for %d records: %v\n", count, err)
			continue
		}
		speed := float64(count) / executionTime
		fmt.Printf("%-10d %-15.2f %-15.2f %.2f\n", count, executionTime, memoryUsed, speed)
	}

	fmt.Println(strings.Repeat("-", 80))
}
