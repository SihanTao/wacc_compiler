parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

// program
program: BEGIN func* stat END EOF;

// function
func : type ident OPEN_PARENTHESES paramlist? CLOSE_PARENTHESES IS stat END;

// parameter list
paramlist : param (COMMA param)*;

// parameter
param: type ident;

// statement
stat: SKP
    | type ident ASSIGN assignrhs
    | assignlhs ASSIGN assignrhs
    | READ assignlhs
    | FREE expr
    | RETURN expr
    | EXIT expr
    | PRINT expr
    | PRINTLN expr
    | IF expr THEN stat ELSE stat FI
    | WHILE expr DO stat DONE
    | BEGIN stat END
    | stat SEMICOLON stat;