# spreadsheet
Reads CSV, resolves references like a spreadsheet, evals expressions, writes output, exits on exceptions

mvn install
run.sh - will run a default file filled with iostat output should replace 3000 txt fields with 0.00

run.sh <path> will process file
## Exiting Error Checks
* non-csv - no commas first line
* field counts mismatch in rows
* circular reference 

run.sh <path> <path> <path> - will run in sequence

output to output/csv_output/<input-file-without-ext-output>.csv


