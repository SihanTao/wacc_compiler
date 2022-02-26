FILENAME="comment"

make clean
make

echo "Compiling: src/wacc_examples/valid/basic/skip/${FILENAME}.wacc"
./compile "src/wacc_examples/valid/basic/skip/${FILENAME}.wacc"

echo "Creating exec file ${FILENAME}.s ......"
arm-linux-gnueabi-gcc -o "$FILENAME" -mcpu=arm1176jzf-s -mtune=arm1176jzf-s "${FILENAME}.s"

echo "Executing the exec file ${FILENAME}.s ......"
qemu-arm -L /usr/arm-linux-gnueabi/ $FILENAME