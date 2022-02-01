VALID_TEST=$(find ../valid -iname '*.wacc')
INVALID_TEST=$(find ../invalid -iname '*.wacc')

valid_cases=0
invalid_cases=0
total_cases=0
valid_cases_for_invalid=0
invalid_cases_for_invalid=0
total_cases_for_invalid=0

echo "-------------------------------------------------------------------"
echo " VALID TEST START"
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
echo " INVALID TEST START"
echo "-------------------------------------------------------------------"


for file in ${INVALID_TEST}; do
    msg=$(./compile $file 2>> ../bin/invalid)
    total_cases=$((total_cases_for_invalid+1))

    if [ $? -ne 0 ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error message: " $msg
      echo "-------------------------------------------------------"
      exit 1
      invalid_cases=$((invalid_cases_for_invalid+1))
    else
      valid_cases=$((valid_cases_for_invalid+1))
    fi
    done

    echo "-------------------------------------------------------"
    echo "TOTAL" $total_cases "CASES. PASSED" $valid_cases ". FAILED" $invalid_cases "CASES"

echo "-------------------------------------------------------------------"
echo " PARSER TEST FINISHED"
echo "-------------------------------------------------------------------"
