#!/bin/bash
cmd="java -cp target/Spreadsheet-0.0.1-SNAPSHOT.jar com.windfall.testapp.Spreadsheet"
if [[ "$1" == "" ]];
then
    ${cmd}
    exit
fi
$cmd $*
