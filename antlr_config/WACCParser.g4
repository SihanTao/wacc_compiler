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
assignlhs : ident        #Identifier
          | array_elem   #ArrayElem
          | pair_elem    #LhsPairElem // will use the same visitor in rhs and lhs
          ;

// assign rhs
assignrhs : expr                                                        #ExprNode
          | arrayLiter                                                  #ArrayLiteral
          | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES  #NewPair
          | pair_elem                                                   #RhsPairElem // will use the same visitor in rhs and lhs
          | CALL ident OPEN_PARENTHESES arglist? CLOSE_PARENTHESES      #FunctionCall
          ;

// argument list
arglist : expr (COMMA expr)*;

// pair element
pair_elem: FST expr #FstExpr
         | SND expr #SndExpr
         ;

// type
type : base_type
     | array_type
     | pair_type
     ;

base_type: INT
        | BOOL
        | CHAR
        | STRING
        ;

array_type: base_type OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
         | array_type OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
         | pair_type OPEN_SQUARE_BRACKETS CLOSE_SQUARE_BRACKETS
         ;

pair_type: PAIR OPEN_PARENTHESES pairElemType COMMA pairElemType CLOSE_PARENTHESES;

pairElemType: base_type
            | array_type
            | PAIR
            ;

// expression
expr : intLiter         #IntExpr
     | boolLiter        #BoolExpr
     | charLiter        #CharExpr
     | strLiter         #StrExpr
     | pairLiter        #PairExpr
     | ident            #IdentExpr
     | array_elem       #ArrayExpr
     | unaryOper expr   #UnopExpr
     | expr binaryOper=(MUL | DIV | MOD) expr      #ArithmeticExpr
     | expr binaryOper=( PLUS | MINUS ) expr       #ArithmeticExpr
     | expr binaryOper=(GT | GEQ | LT | LEQ) expr  #CmpExpr
     | expr binaryOper=( EQ | NEQ ) expr           #EqExpr
     | expr (AND | OR) expr                        #AndOrExpr
     | OPEN_PARENTHESES expr CLOSE_PARENTHESES     #ParenExpr
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

array_elem: ident (OPEN_SQUARE_BRACKETS expr CLOSE_SQUARE_BRACKETS)+;

// literal
intLiter: (PLUS | MINUS)? NUMBER;
boolLiter: TRUE | FALSE;
charLiter: CHARLITER;
strLiter: STRLITER;
arrayLiter: OPEN_SQUARE_BRACKETS (expr (COMMA expr)*)? CLOSE_SQUARE_BRACKETS;
pairLiter: NULL;
