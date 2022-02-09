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
stat: SKP                               #SkipStat
    | type ident ASSIGN assignrhs       #DeclareStat
    | assignlhs ASSIGN assignrhs        #AssignStat
    | READ assignlhs                    #ReadStat
    | FREE expr                         #FreeStat
    | RETURN expr                       #ReturnStat
    | EXIT expr                         #ExitStat
    | PRINT expr                        #PrintStat
    | PRINTLN expr                      #PrintlnStat
    | IF expr THEN stat ELSE stat FI    #IfStat
    | WHILE expr DO stat DONE           #WhileStat
    | BEGIN stat END                    #ScopeStat
    | stat SEMICOLON stat               #SequenceStat
    ;

// assign-lhs
assignlhs : ident
    | arrayElem
    | pairElem
    ;

// assign rhs
assignrhs : expr
    | arrayLiter
    | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
    | pairElem
    | CALL ident OPEN_PARENTHESES arglist? CLOSE_PARENTHESES
    ;

// argument list
arglist : expr (COMMA expr)*;

// pair element
pairElem : FST expr
    | SND expr
    ;

// type
type : baseType
    | arrayType
    | pairType
    ;

baseType: INT
    | BOOL
    | CHAR
    | STRING
    ;

arrayType: baseType OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
    | arrayType OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
    | pairType OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
    ;

pairType: PAIR OPEN_PARENTHESES pairElemType COMMA pairElemType CLOSE_PARENTHESES;

pairElemType: baseType
    | arrayType
    | PAIR
    ;

// expression
expr : intLiter
    | boolLiter
    | charLiter
    | strLiter
    | pairLiter
    | ident
    | arrayElem
    | unaryOper expr
    | expr binaryOper=(MUL | DIV | MOD) expr
    | expr binaryOper=( PLUS | MINUS ) expr
    | expr binaryOper=(GT | GEQ | LT | LEQ) expr
    | expr binaryOper=( EQ | NEQ ) expr
    | expr AND expr
    | expr OR expr
    | OPEN_PARENTHESES expr CLOSE_PARENTHESES
    ;

// Operators
// unary ops
unaryOper: NOT
    | MINUS
    | LEN
    | ORD
    | CHR
    ;

ident: IDENT;

arrayElem: ident (OPEN_SQUARE_BRACKETS expr CLOSE_SQUARE_BRACKETS)+;

// literal
intLiter: (PLUS | MINUS)? NUMBER;
boolLiter: TRUE | FALSE;
charLiter: CHARLITER;
strLiter: STRLITER;
arrayLiter: OPEN_SQUARE_BRACKETS (expr (COMMA expr)*)? CLOSE_SQUARE_BRACKETS;
pairLiter: NULL;
