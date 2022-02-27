#!/bin/bash
SRC_DIR="$(pwd)/test"

VALID_EXAMPLES="${SRC_DIR}/wacc_examples/valid"

mkdir -p "${SRC_DIR}/log"
mkdir -p "${SRC_DIR}/log/out"
mkdir -p "${SRC_DIR}/log/fails"

REFERENCE_COMPILER="${SRC_DIR}/wacc_examples/refCompile"
REFERENCE_EMULATOR="${SRC_DIR}/wacc_examples/refEmulate"

OUT_DIR="${SRC_DIR}/log/out"
FAILS_DIR="${SRC_DIR}/log/fails"

rm -r $OUT_DIR/*
rm -r $FAILS_DIR/*

VALID_TEST=$(find ${VALID_EXAMPLES} -name "*.wacc")

TOTAL_COUNT=0
PASSED=0

INPUT="c"
SINGLE_FILE=""
DIRECTORY=""

# optional flags:
# -i {input}, sets input for the programs
# -f {file}, test only file supplied
# -d {directory}, tests all files in directory supplied
# automatically searches for file/directory name, no need to put whole path
while getopts 'i:f:d:' flag; do
  case "${flag}" in
    i) INPUT="${OPTARG}" ;;
    f) SINGLE_FILE="${OPTARG}" ;;
    d) DIRECTORY="${OPTARG}" ;;
  esac
done

echo "=============================Valid Case Test============================================"
echo "Correct Program must have expected output matches the execute output"
echo "========================================================================================"

if [[ $SINGLE_FILE -ne "" ]]; then
  testFile($(find ${VALID_EXAMPLES} -name $SINGLE_FILE))
elif [[ $DIRECTORY -ne "" ]]; then
  for file in $(find "${VALID_EXAMPLES}/$DIRECTORY" -name "*.wacc"); do
    testFile(file)
  done
else
  for file in $VALID_TEST; do
    testFile(file)
  done
fi

testFile() {

    file=$1
    NAME=$(basename -s .wacc "${file}")

    echo "testing $NAME"

    # generate our output
    ./compile $file > "$OUT_DIR/$NAME.s"
    arm-linux-gnueabi-gcc -o "$OUT_DIR/$NAME_11.s" -mcpu=arm1176jzf-s -mtune=arm1176jzf-s "$OUT_DIR/$NAME.s"
    echo $INPUT | qemu-arm -L /usr/arm-linux-gnueabi/ "$OUT_DIR/$NAME_11.s" > "$OUT_DIR/$NAME.out"

    # generate reference file
    echo $INPUT | $REFERENCE_COMPILER $file -a -x > "$OUT_DIR/${NAME}_REF.txt"

    # get the asm and output from the generated file
    awk '/===========================================================/{n++}
      {
        if ( n == 1 )
          print >"out" tmp ".s";
        else if ( n == 3)
          print > "out" tmp ".out";
      }' "$OUT_DIR/${NAME}_REF.txt"
    sed -i '1d' tmp.s && mv tmp.s "$OUT_DIR/${NAME}_REF.s"
    sed -i '1d' tmp.out && mv tmp.out "$OUT_DIR/${NAME}_REF.out"

    let COUNTER+=1
    
    # compare our output and reference output
    if cmp -s "$OUT_DIR/$NAME.out" "$OUT_DIR/${NAME}_REF.out"; then
      echo "PASSED"
      let PASSED+=1
    else
      echo "FAILED"
      diff "$OUT_DIR/$NAME.out" "$OUT_DIR/${NAME}_REF.out" > "$FAILS_DIR/$NAME.txt"
    fi

}

echo "================================TEST RESULT============================================="
echo "Total Test: $TOTAL_COUNT"
echo "Compiled Test: $PASSED"
echo "========================================================================================"

if [[ $PASSED -ne $TOTAL_COUNT ]]; then
  exit -1
fi
