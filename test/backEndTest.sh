#!/bin/bash
SRC_DIR="$(pwd)/test"

VALID_EXAMPLES="${SRC_DIR}/wacc_examples/valid"

mkdir -p "${SRC_DIR}/log"
mkdir -p "${SRC_DIR}/log/out"
mkdir -p "${SRC_DIR}/log/fails"

OUT_DIR="${SRC_DIR}/log/out"
FAILS_DIR="${SRC_DIR}/log/fails"

find $OUT_DIR -type f ! -name "*.txt" -delete
rm -rf $FAILS_DIR/*

TOTAL_COUNT=0
PASSED=0

INPUT="g"
SINGLE_FILE=""
DIRECTORY=""
OPTIMISATION=""

# optional flags:
# -i {input}, sets input for the programs
# -f {file}, test only file supplied
# -d {directory}, tests all files in directory supplied
# automatically searches for file/directory name, no need to put whole path
while getopts 'i:f:d:o:' flag; do
  case "${flag}" in
    i) INPUT="${OPTARG}" ;;
    f) SINGLE_FILE="${OPTARG}" ;;
    d) DIRECTORY="${OPTARG}" ;;
    o) OPTIMISATION="${OPTARG}" ;;
    *) echo "Invalid option: -$flag" ;;
  esac
done

echo "=============================Valid Case Test============================================"
echo "Correct Program must have expected output matches the execute output"
echo "========================================================================================"

if [[ $SINGLE_FILE != "" ]]; then
  FILES_TO_TEST=$(find ${VALID_EXAMPLES} -name "$SINGLE_FILE*")
elif [[ $DIRECTORY != "" ]]; then
  FILES_TO_TEST=$(find "${VALID_EXAMPLES}/$DIRECTORY" -name "*.wacc")
else
  FILES_TO_TEST=$(find ${VALID_EXAMPLES} -name "*.wacc" ! -name "IOLoop.wacc" ! -name "ticTacToe.wacc")
fi

for file in $FILES_TO_TEST;do

    let TOTAL_COUNT+=1
    NAME=$(basename -s .wacc "${file}")

    echo "testing $NAME"

    # generate our output
    if [[ $OPTIMISATION == "" ]]; then
      ./compile $file
    else
      ./compile $file -$OPTIMISATION
    fi
    mv "$NAME.s" "$OUT_DIR/$NAME.s"
    arm-linux-gnueabi-gcc -o "$OUT_DIR/${NAME}" -mcpu=arm1176jzf-s -mtune=arm1176jzf-s "$OUT_DIR/$NAME.s"
    echo "$INPUT" | qemu-arm -L /usr/arm-linux-gnueabi/ "$OUT_DIR/${NAME}" > "$OUT_DIR/$NAME.out"
    our_code=$?

    their_code=$(cat "$OUT_DIR/${NAME}_exitCode.txt")

    failed=false

    # compare exit codes
    if [[ our_code -ne their_code ]]; then
      failed=true
      echo "our exit code is ${our_code} but the correct exit code is ${their_code}" > "$FAILS_DIR/$NAME.txt"
    fi

    # compare our output and reference output
    if ! diff -b -B "${OUT_DIR}/$NAME.out" "${OUT_DIR}/${NAME}.txt"; then
      echo "our output is different from expected output"
      failed=true
      diff -b -B "${OUT_DIR}/$NAME.out" "${OUT_DIR}/${NAME}.txt" > "${FAILS_DIR}/$NAME.txt"
    fi

    if [[ "$failed" = false ]]; then
      echo "PASSED"
      let PASSED+=1
      find $OUT_DIR -name "${NAME}.*" -delete
    else
      echo "FAILED"
    fi

done

echo "================================TEST RESULT============================================="
echo "Total Test: $TOTAL_COUNT"
echo "Passed Test: $PASSED"
echo "========================================================================================"

if [[ $PASSED -ne $TOTAL_COUNT ]]; then
  exit -1
fi
