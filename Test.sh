SRC_DIR="$(pwd)/src/test"

VALID_TEST=$(find "${SRC_DIR}/valid" -iname '*.wacc')
INVALID_SYNTAX_TEST=$(find "${SRC_DIR}/invalid/syntaxErr" -iname '*.wacc')
INVALID_SEMANTIC_TEST=$(find "${SRC_DIR}/invalid/semanticErr" -iname '*.wacc')

valid_cases=0
invalid_cases=0
total_cases=0

mkdir -p "${SRC_DIR}/out"
> "${SRC_DIR}/out/valid.txt"
> "${SRC_DIR}/out/invalidSyntax.txt"
> "${SRC_DIR}/out/invalidSemantic.txt"

echo "-------------------------------------------------------------------"
echo "VALID TEST START"
echo "-------------------------------------------------------------------"

for file in ${VALID_TEST}; do
    echo $file >> "${SRC_DIR}/out/valid.txt" 
    ./compile $file >> "${SRC_DIR}/out/valid.txt"
    let total_cases+=1

    if [ $? -ne 0 ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error message: " $?
      echo "-------------------------------------------------------"
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

for file in ${INVALID_SYNTAX_TEST}; do
    echo $file >>  "${SRC_DIR}/out/invalidSyntax.txt"
    ./compile $file >> "${SRC_DIR}/out/invalidSyntax.txt"
    let total_cases_for_invalid+=1

    if [ $? -ne 100 ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error message: " $?
      echo "-------------------------------------------------------"
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
echo "INVALID SEMANTIC ERROR TEST START"
echo "-------------------------------------------------------------------"

valid_cases_for_invalid=0
invalid_cases_for_invalid=0
total_cases_for_invalid=0

for file in ${INVALID_SEMANTIC_TEST}; do
    echo $file >> "${SRC_DIR}/out/invalidSemantic.txt"
    ./compile $file >> "${SRC_DIR}/out/invalidSemantic.txt"
    let total_cases_for_invalid+=1

    if [ $? -ne 100 ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error message: " $?
      echo "-------------------------------------------------------"
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