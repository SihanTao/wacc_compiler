VALID_TEST=$(find ../valid -iname '*.wacc')
INVALID_TEST=$(find ../invalid/syntaxErr -iname '*.wacc')

valid_cases=0
invalid_cases=0
total_cases=0

echo "-------------------------------------------------------------------"
echo "VALID TEST START"
echo "-------------------------------------------------------------------"

for file in ${VALID_TEST}; do
    msg=$(./compile $file 2>> ../bin/valid)
    let total_cases+=1

    if [ $? -ne 0 ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error message: " $msg
      echo "-------------------------------------------------------"
      exit 1
      let invalid_cases+=1
    else
      let valid_cases+=1
    fi
    done

    echo "-------------------------------------------------------"
    echo "TOTAL" $total_cases "CASES. PASSED" $valid_cases ". FAILED" $invalid_cases "CASES"
    echo "-------------------------------------------------------"

echo "-------------------------------------------------------------------"
echo "INVALID SYNTAX ERROR TEST START"
echo "-------------------------------------------------------------------"

valid_cases_for_invalid=0
invalid_cases_for_invalid=0
total_cases_for_invalid=0

for file in ${INVALID_TEST}; do
    msg=$(./compile $file 2>> ../bin/invalidsyn)
    let total_cases_for_invalid+=1

    if [ $? -ne 100 ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
#      echo "error message: " $msg
      echo "error message: " $?
      echo "-------------------------------------------------------"
      exit 1
      let invalid_cases_for_invalid+=1
    else
      let valid_cases_for_invalid+=1
    fi
    done

    echo "-------------------------------------------------------"
    echo "TOTAL" $total_cases_for_invalid "CASES. PASSED" $valid_cases_for_invalid ".
    FAILED" $invalid_cases_for_invalid "CASES"
    echo "-------------------------------------------------------"


echo "-------------------------------------------------------------------"
echo "PARSER TEST FINISHED"
echo "-------------------------------------------------------------------"
