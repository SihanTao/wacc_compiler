SRC_DIR="$(pwd)/test/wacc_examples"

VALID_TEST=$(find "${SRC_DIR}/valid" -iname '*.wacc')
INVALID_SYNTAX_TEST=$(find "${SRC_DIR}/invalid/syntaxErr" -iname '*.wacc')
INVALID_SEMANTIC_TEST=$(find "${SRC_DIR}/invalid/semanticErr" -iname '*.wacc')

SYNTAX_ERROR_CODE=100
SEMANTIC_ERROR_CODE=200

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

    let total_cases+=1

    echo $file >> "${SRC_DIR}/out/valid.txt" 
    ./compile $file &>> "${SRC_DIR}/out/valid.txt"
	let error_code=$?
	echo "" >> "${SRC_DIR}/out/valid.txt"

    if [ $error_code -ne 0 ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error code: " $error_code
      echo "-------------------------------------------------------"
      let invalid_cases+=1
    else
      echo $file " is valid"
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

    let total_cases_for_invalid+=1

    echo $file >>  "${SRC_DIR}/out/invalidSyntax.txt"
    ./compile $file &>> "${SRC_DIR}/out/invalidSyntax.txt"
	let error_code=$?
	echo "" >> "${SRC_DIR}/out/invalidSyntax.txt"

    if [ $error_code -ne $SYNTAX_ERROR_CODE ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error code: " $error_code
      echo "-------------------------------------------------------"
      let invalid_cases_for_invalid+=1
    else
	  echo $file " fails correctly with error code " $SYNTAX_ERROR_CODE
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

    let total_cases_for_invalid+=1

    echo $file >> "${SRC_DIR}/out/invalidSemantic.txt"
    ./compile $file &>> "${SRC_DIR}/out/invalidSemantic.txt"
	let error_code=$?
	echo "" >> "${SRC_DIR}/out/invalidSemantic.txt"

    if [ $error_code -ne $SEMANTIC_ERROR_CODE ]; then
      echo "-------------------------------------------------------"
      echo "fail at" $file
      echo "error code: " $error_code
      echo "-------------------------------------------------------"
      let invalid_cases_for_invalid+=1
    else
	  echo $file " fails correctly with error code " $SEMANTIC_ERROR_CODE
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
