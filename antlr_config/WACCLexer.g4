lexer grammar WACCLexer;

// Other Keywords
BEGIN: 'begin';
END: 'END';
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
fragment INTSIGN: MINUS | PLUS;

// character
COMMA: ',';
SEMICOLON: ';';
UNDERSCORE: '_';
APOSTROPHE: '\'';
QUOTATION: '"';
CHARACTER: ~['"\\]
         | '\\' ESCASPED_CHAR;

ESCASPED_CHAR: '0' | 'b' | 't' | 'n' | 'f' | 'r' | '"' | '\'' | '\\';
fragment LETTER: [a-zA-Z];
fragment DIGIT: [0-9];

// comment
SHARP   : '#' ;
EOL     : '\n' ;
COMMENT : SHARP ~[\n]* EOL -> skip;