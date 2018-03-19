#!/bin/bash
java -cp target/Spreadsheet-0.0.1-SNAPSHOT.jar com.windfall.testapp.Spreadsheet
tail -n 1000 log/Spreadsheet.log | less
