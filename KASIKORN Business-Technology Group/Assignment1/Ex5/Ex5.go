package main

import (
	"encoding/csv"
	"fmt"
	"math/rand"
	"os"
	"strings"
	"time"
)

type Generator interface {
	generateDataFile(numRecords int, outputFile string) (int, error)
	name() string
}

type DataInLoop struct {
	currentID     int
	provinceCodes []string
	countryCodes  []string
	bankCodes     []string
	positions     []string
	companySizes  []string
	firstNames    []string
	lastNames     []string
	companyTypes  []string
}

func NewDataInLoop() *DataInLoop {
	d := &DataInLoop{}
	d.setupMasterData()
	return d
}

func (d *DataInLoop) setupMasterData() {
	d.provinceCodes = []string{"BKK", "CNX", "NTB", "PTT", "NKP"}
	d.countryCodes = []string{"TH", "US", "GB", "JP", "SG"}
	d.bankCodes = []string{"79601001", "79601002", "79601003"}
	d.positions = []string{"Manager", "Staff", "Director", "Supervisor"}
	d.companySizes = []string{"S", "M", "L"}
	d.firstNames = []string{"Somchai", "Arthit", "Kittisak", "Naphat", "Mali"}
	d.lastNames = []string{"Kasikorn", "KBTG", "K+", "KhunThong", "MeowJot"}
	d.companyTypes = []string{"Trading", "Technology", "Services"}
}

func (d *DataInLoop) generateThaiID() string {
	d.currentID++
	prefix := rand.Intn(9) + 1 // 1-9
	middle := fmt.Sprintf("%09d", d.currentID)
	checkDigit := rand.Intn(10)
	return fmt.Sprintf("%d%s%d", prefix, middle, checkDigit)
}

func (d *DataInLoop) generatePersonName() string {
	firstName := d.firstNames[rand.Intn(len(d.firstNames))]
	lastName := d.lastNames[rand.Intn(len(d.lastNames))]
	return fmt.Sprintf("%s %s", firstName, lastName)
}

func (d *DataInLoop) generateCompanyName() string {
	companyType := d.companyTypes[rand.Intn(len(d.companyTypes))]
	suffixes := []string{"Group", "Corp", "Co", "PLC"}
	suffix := suffixes[rand.Intn(len(suffixes))]
	return fmt.Sprintf("%s %s %d", companyType, suffix, d.currentID)
}

func (d *DataInLoop) generateDataFile(numRecords int, outputFile string) (int, error) {
	numCorporate := int(float64(numRecords) * 0.05)
	numIndividual := numRecords - numCorporate

	file, err := os.Create(outputFile)
	if err != nil {
		return 0, err
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	header := []string{"CUSTID", "FULL_NAME", "CUST_TYPE", "IDENTITY_NUM"}
	if err := writer.Write(header); err != nil {
		return 0, err
	}

	recordsGenerated := 0

	for i := 0; i < numCorporate; i++ {
		companyName := d.generateCompanyName()
		custID := "C" + d.generateThaiID()
		identityNum := d.generateThaiID()

		record := []string{custID, companyName, "P01", identityNum}
		if err := writer.Write(record); err != nil {
			return recordsGenerated, err
		}
		recordsGenerated++
	}

	for i := 0; i < numIndividual; i++ {
		custID := "I" + d.generateThaiID()
		fullName := d.generatePersonName()
		identityNum := d.generateThaiID()

		record := []string{custID, fullName, "P02", identityNum}
		if err := writer.Write(record); err != nil {
			return recordsGenerated, err
		}
		recordsGenerated++
	}

	return recordsGenerated, nil
}

func (d *DataInLoop) name() string {
	return "DataInLoop"
}

type DataOutLoop struct {
	currentID       int
	provinceCodes   []string
	countryCodes    []string
	bankCodes       []string
	positions       []string
	companySizes    []string
	personNames     []string
	companyBaseNames []string
	idPrefixes      []int
	idCheckDigits   []int
}

func NewDataOutLoop() *DataOutLoop {
	d := &DataOutLoop{}
	d.setupMasterData()
	return d
}

func (d *DataOutLoop) setupMasterData() {
	d.provinceCodes = []string{"BKK", "CNX", "NTB", "PTT", "NKP"}
	d.countryCodes = []string{"TH", "US", "GB", "JP", "SG"}
	d.bankCodes = []string{"79601001", "79601002", "79601003"}
	d.positions = []string{"Manager", "Staff", "Director", "Supervisor"}
	d.companySizes = []string{"S", "M", "L"}

	firstNames := []string{"Somchai", "Arthit", "Kittisak", "Naphat", "Mali"}
	lastNames := []string{"Kasikorn", "KBTG", "K+", "KhunThong", "MeowJot"}
	d.personNames = make([]string, 0, len(firstNames)*len(lastNames))
	for _, f := range firstNames {
		for _, l := range lastNames {
			d.personNames = append(d.personNames, fmt.Sprintf("%s %s", f, l))
		}
	}

	companyTypes := []string{"Trading", "Technology", "Services"}
	companySuffixes := []string{"Group", "Corp", "Co", "PLC"}
	d.companyBaseNames = make([]string, 0, len(companyTypes)*len(companySuffixes))
	for _, t := range companyTypes {
		for _, s := range companySuffixes {
			d.companyBaseNames = append(d.companyBaseNames, fmt.Sprintf("%s %s", t, s))
		}
	}

	d.idPrefixes = make([]int, 9)
	for i := 0; i < 9; i++ {
		d.idPrefixes[i] = i + 1
	}
	d.idCheckDigits = make([]int, 10)
	for i := 0; i < 10; i++ {
		d.idCheckDigits[i] = i
	}
}

func (d *DataOutLoop) generateBatchIDs(batchSize int) []string {
	ids := make([]string, batchSize)
	for i := 0; i < batchSize; i++ {
		d.currentID++
		prefix := d.idPrefixes[rand.Intn(len(d.idPrefixes))]
		check := d.idCheckDigits[rand.Intn(len(d.idCheckDigits))]
		ids[i] = fmt.Sprintf("%d%09d%d", prefix, d.currentID, check)
	}
	return ids
}

func (d *DataOutLoop) generateDataFile(numRecords int, outputFile string) (int, error) {
	numCorporate := int(float64(numRecords) * 0.05)
	numIndividual := numRecords - numCorporate

	batchSize := (numCorporate + numIndividual) * 2
	allIDs := d.generateBatchIDs(batchSize)
	idIndex := 0

	file, err := os.Create(outputFile)
	if err != nil {
		return 0, err
	}
	defer file.Close()

	writer := csv.NewWriter(file)
	defer writer.Flush()

	header := []string{"CUSTID", "FULL_NAME", "CUST_TYPE", "IDENTITY_NUM"}
	if err := writer.Write(header); err != nil {
		return 0, err
	}

	recordsGenerated := 0

	for i := 0; i < numCorporate; i++ {
		if idIndex+1 >= len(allIDs) {
			break
		}
		companyName := fmt.Sprintf("%s %d", d.companyBaseNames[rand.Intn(len(d.companyBaseNames))], i+1)
		record := []string{
			"C" + allIDs[idIndex],
			companyName,
			"P01",
			allIDs[idIndex+1],
		}
		if err := writer.Write(record); err != nil {
			return recordsGenerated, err
		}
		recordsGenerated++
		idIndex += 2
	}

	for i := 0; i < numIndividual; i++ {
		if idIndex+1 >= len(allIDs) {
			break
		}
		record := []string{
			"I" + allIDs[idIndex],
			d.personNames[rand.Intn(len(d.personNames))],
			"P02",
			allIDs[idIndex+1],
		}
		if err := writer.Write(record); err != nil {
			return recordsGenerated, err
		}
		recordsGenerated++
		idIndex += 2
	}

	return recordsGenerated, nil
}

func (d *DataOutLoop) name() string {
	return "DataOutLoop"
}

type Result struct {
	size            int
	duration        time.Duration
	recordsPerSecond float64
	fileSizeMB      float64
}

func runPerformanceTest(generator Generator, sizes []int) []Result {
	var results []Result
	for _, size := range sizes {
		start := time.Now()
		outputFile := fmt.Sprintf("customer_data_%s_%drecords.csv", generator.name(), size)

		recordsGenerated, err := generator.generateDataFile(size, outputFile)
		if err != nil {
			fmt.Printf("Error generating data: %v\n", err)
			continue
		}

		duration := time.Since(start)
		fileInfo, err := os.Stat(outputFile)
		if err != nil {
			fmt.Printf("Error getting file info: %v\n", err)
			continue
		}

		fileSizeMB := float64(fileInfo.Size()) / (1024 * 1024)
		rps := float64(recordsGenerated) / duration.Seconds()

		results = append(results, Result{
			size:            size,
			duration:        duration,
			recordsPerSecond: rps,
			fileSizeMB:      fileSizeMB,
		})

		fmt.Printf("Generated %d records in %v (%.2f records/s)\n", recordsGenerated, duration, rps)
		fmt.Printf("File size: %.2f MB\n", fileSizeMB)
	}
	return results
}

func main() {
	rand.Seed(time.Now().UnixNano())

	sizes := []int{10000, 100000, 1000000}

	fmt.Println("Testing function in loop...")
	inLoopResults := runPerformanceTest(NewDataInLoop(), sizes)

	fmt.Println("\nTesting function out loop...")
	outLoopResults := runPerformanceTest(NewDataOutLoop(), sizes)

	fmt.Println("\nPerformance Comparison:")
	fmt.Printf("%-10s %-12s %-12s %-12s %-12s\n", "Size", "Method", "Duration", "Rec/s", "File Size")
	fmt.Println(strings.Repeat("-", 60))

	for i, size := range sizes {
		inLoop := inLoopResults[i]
		outLoop := outLoopResults[i]

		fmt.Printf("%-10d %-12s %-12v %-12.0f %-12.2f\n",
			size, "InLoop", inLoop.duration.Round(time.Millisecond), inLoop.recordsPerSecond, inLoop.fileSizeMB)
		fmt.Printf("%-10d %-12s %-12v %-12.0f %-12.2f\n",
			size, "OutLoop", outLoop.duration.Round(time.Millisecond), outLoop.recordsPerSecond, outLoop.fileSizeMB)
		fmt.Printf("Speedup: %.2fx\n\n", inLoop.duration.Seconds()/outLoop.duration.Seconds())
	}
}