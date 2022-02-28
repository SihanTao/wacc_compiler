FILENAME="printBool"
TEST_FOLDER_PATH="test/wacc_examples/valid/IO/print"

#make clean
#make

echo "Compiling: ${TEST_FOLDER_PATH}/${FILENAME}.wacc"
./compile "${TEST_FOLDER_PATH}/${FILENAME}.wacc"

echo "Creating exec file ${FILENAME}.s ......"
arm-linux-gnueabi-gcc -o "$FILENAME" -mcpu=arm1176jzf-s -mtune=arm1176jzf-s "${FILENAME}.s"

echo "Executing the exec file ${FILENAME}.s ......"
qemu-arm -L /usr/arm-linux-gnueabi/ $FILENAME

echo "The exit code is $?"