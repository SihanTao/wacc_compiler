# Sample Makefile for the WACC Compiler lab: edit this to build your own comiler

# Useful locations

ANTLR_DIR	 := antlr_config
SOURCE_DIR	 := src
ANTLR_SOURCE_DIR := $(SOURCE_DIR)/antlr
OUTPUT_DIR	 := bin
LOG_DIR		 := log

# Project tools

ANTLR	:= antlrBuild
MKDIR	:= mkdir -p
JAVAC	:= javac
KOTLINC	:= kotlinc
RM	:= rm -rf
COMPILER := $(SOURCE_DIR)/Main.kt

# Configure project Java flags

FLAGS	:= -d $(OUTPUT_DIR) -cp bin:lib/antlr-4.9.3-complete.jar
JFLAGS	:= -sourcepath $(SOURCE_DIR) $(FLAGS)
FIND	:= find

# The make rules:

# run the antlr build script then attempts to compile all .java files within src/antlr
all: class compiler

class:
	cd $(ANTLR_DIR) && ./$(ANTLR)
	$(FIND) $(SOURCE_DIR) -name '*.kt' > $@
	$(MKDIR) $(OUTPUT_DIR)
	$(JAVAC) $(JFLAGS) $(ANTLR_SOURCE_DIR)/*.java
	$(KOTLINC) $(FLAGS) @$@

compiler:
	$(KOTLINC) $(FLAGS) $(COMPILER)

# clean up all of the compiled files
clean:
	$(RM) $(OUTPUT_DIR) $(SOURCE_DIR)/antlr $(LOG_DIR) class

.PHONY: all clean
