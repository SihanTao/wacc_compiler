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

echo "=============================Valid Case Test============================================"
echo "Correct Program must have expected output matches the execute output"
echo "========================================================================================"

for file in $VALID_TEST; do

    NAME=$(basename -s .wacc "${file}")

    echo "testing $NAME"

    ./compile $file > "$OUT_DIR/$NAME.s"
    arm-linux-gnueabi-gcc -o "$NAME_11.s" -mcpu=arm1176jzf-s -mtune=arm1176jzf-s "$OUT_DIR/$NAME.s" > "$OUT_DIR/$NAME_11.s"
    qemu-arm -L /usr/arm-linux-gnueabi/ "$OUT_DIR/$NAME_11.s" > "$OUT_DIR/$NAME.out"

    $REFERENCE_COMPILER $file > "$OUT_DIR/${NAME}_REF.s"
    $REFERENCE_EMULATOR "$OUT_DIR/${NAME}_REF.s" > "$OUT_DIR/${NAME}_REF.out"

    let COUNTER+=1
    
    if cmp -s "$OUT_DIR/$NAME.out" "$OUT_DIR/${NAME}_REF.out"; then
      echo "PASSED"
      let PASSED+=1
    else
      echo "FAILED"
      diff "$OUT_DIR/$NAME.out" "$OUT_DIR/${NAME}_REF.out" > "$FAILS_DIR/$NAME.txt"
    fi
done


echo "================================TEST RESULT============================================="
echo "Total Test: $TOTAL_COUNT"
echo "Compiled Test: $PASSED"
echo "========================================================================================"

if [[ $PASSED -ne $TOTAL_COUNT ]]; then
  exit -1
fi
