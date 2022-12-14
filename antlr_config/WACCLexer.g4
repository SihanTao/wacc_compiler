lexer grammar WACCLexer;

// Skip white space
WS : [ \n\t\r]+ -> skip;

// Other Keywords
BEGIN: 'begin';
END: 'end';
SKP: 'skip';
READ: 'read';
FREE: 'free';
RETURN: 'return';
EXIT: 'exit';
PRINT: 'print';
PRINTLN: 'println';
CALL: 'call';
TRUE: 'true';
FALSE: 'false';
NULL: 'null';
IS: 'is';

// if-else keywords
IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';

// while keywords
WHILE: 'while';
DO: 'do';
DONE: 'done';

// Pair keyworks
PAIR: 'pair';
NEWPAIR: 'newpair';
FST: 'fst';
SND: 'snd';

// type
INT: 'int';
BOOL: 'bool';
CHAR: 'char';
STRING: 'string';

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;
OPEN_SQUARE_BRACKETS: '[';
CLOSE_SQUARE_BRACKETS: ']';

// Operaters
// unary ops
ASSIGN: '=';
NOT: '!';
LEN: 'len';
ORD: 'ord';
CHR: 'chr';

// binary ops
MUL: '*';
DIV: '/';
MOD: '%';
PLUS: '+';
MINUS: '-';
GT: '>';
GEQ: '>=';
LT: '<';
LEQ: '<=';
EQ: '==';
NEQ: '!=';
AND: '&&';
OR: '||';

// character
COMMA: ',';
SEMICOLON: ';';
APOSTROPHE: '\'';
QUOTATION: '"';
fragment
CHARACTER: ~['"\\]
         | '\\' ESCASPED_CHAR;

// some literals
CHARLITER: '\'' CHARACTER '\'';
STRLITER: '"' CHARACTER* '"';

fragment ESCASPED_CHAR: [0btnfr"'\\];

IDENT: ('_' | [a-zA-Z]) ('_' | [a-zA-Z] | DIGIT)*;
NUMBER: DIGIT+;
fragment DIGIT: [0-9];

// comment
SHARP   : '#' ;
EOL     : '\n' ;
COMMENT : SHARP ~[\n]* EOL -> skip;