grammar id;

@header {
package compiladores;
}


INT: ('-')? [0-9]+;

FLOAT: INT | INT '.' [0-9]+ | INT ('.' [0-9]+)? 'e' INT;

CHAR: '"'[a-zA-Z]*'"' | '\''[a-zA-Z]*'\'';
//va en la cabecera de todos los paquetes generados defino los tokens
PARABRE: '(';
PARCIER: ')';
LLAVEA: '{';
LLAVEC: '}';
CORA: '[';
CORC: ']';

COMA: ',';
PYC: ';';
EQ: '=';

TD_INT: 'int';
TD_CHAR: 'char';
TD_DOUBLE: 'double';
TD_LONG: 'long';
TD_SHORT: 'short';
TD_VOID: 'void';
TD_FLOAT: 'float';


FOR: 'for';
WHILE: 'while';
IF: 'if';
ELSE: 'else';
DO: 'do';
ELSEIF: 'else if';

MAYOR: '>';
MENOR: '<';
MAYORIGUAL: '>=';
MENORIGUAL: '<=';
IGUAL: '==';
DISTINTO: '!=';
YLOGICO: '&&';
OLOGICO: '||';
NOT: '!';

SUMA: '+';
RESTA: '-';
DIVISION: '/';
RESTO: '%';
MULTIPLICACION: '*';

INC: '++';
DEC: '--';



/*OPERADORES DE ASIGNACIÓN */
OAPOS: '+=';
OANEG: '-=';
OAMUL: '*=';
OADIV: '/=';
OARES: '%=';

RETURN: 'return';

// // handle characters which failed to match any other token ErrorCharacter : . ; // si lo uso no


ID: [a-zA-Z_][a-zA-Z0-9_]*; // IDENTIFICADORES : + 1 o mas concidencias 
// * 0 o mas concidencias


//REGLAS. 

prog: instrucciones; //raiz, todo lo otro debe ir abajo para que no lo tome como raiz              

instrucciones: declaraciones instrucciones
  | funcion instrucciones
  | asignacion PYC instrucciones
  | condicional_if instrucciones
  | bucle_while instrucciones
  | ciclo_for instrucciones
  |
  ;

asignacion: ID EQ expresion;

bloque: LLAVEA instrucciones LLAVEC ;

tipo_dato: TD_CHAR | TD_INT | TD_FLOAT | TD_LONG | TD_SHORT | TD_DOUBLE;

tipo_dato_funcion: tipo_dato | TD_VOID;

operador_aritmetico: SUMA | RESTA | MULTIPLICACION | DIVISION | RESTO;

operador_logico:   MENOR | MENORIGUAL | MAYOR | MAYORIGUAL | IGUAL | DISTINTO | YLOGICO | OLOGICO ;

expresion: expresion_aritmetica
  | expresion_simbolica
  ;

tipo_dato_int: INT;

tipo_dato_float: FLOAT;

tipo_dato_char: CHAR;

id_utilizado: ID;

expresion_simbolica: id_utilizado | tipo_dato_int | tipo_dato_float | tipo_dato_char;

expresion_logica: expresion_simbolica
  | NOT expresion_simbolica
  ;

operacion_logica: expresion_logica operador_logico operacion_logica
  | PARABRE operacion_logica PARCIER operador_logico operacion_logica
  | PARABRE operacion_logica PARCIER
  | expresion_logica
  ;

if_data: IF PARABRE operacion_logica PARCIER;

condicional_if: if_data bloque;

expresion_aritmetica: operacion_aritmetica;

operacion_aritmetica: expresion_simbolica operador_aritmetico operacion_aritmetica
  | PARABRE operacion_aritmetica PARCIER operador_aritmetico operacion_aritmetica
  | PARABRE operacion_aritmetica PARCIER
  | expresion_simbolica
  ;


declaracion_variable: ID EQ expresion
  | ID
  ;

lista_variables: declaracion_variable COMA lista_variables
  | declaracion_variable
  ;

declaraciones: tipo_dato lista_variables PYC ;

lista_parametros_implementacion_funcion: tipo_dato ID COMA lista_parametros_implementacion_funcion
  | tipo_dato ID
  |
  ;

lista_parametros_declaracion_funcion: tipo_dato ID COMA lista_parametros_declaracion_funcion
  | tipo_dato ID
  |
  ;

lista_parametros_funcion_usada: id_utilizado COMA lista_parametros_funcion_usada
  | id_utilizado
  |
  ;

declaracion_funcion: tipo_dato_funcion ID PARABRE lista_parametros_declaracion_funcion PARCIER PYC
  ;

implementacion_funcion_encabezado: tipo_dato_funcion ID PARABRE lista_parametros_implementacion_funcion PARCIER;

implementacion_funcion: implementacion_funcion_encabezado bloque;

uso_funcion: ID PARABRE lista_parametros_funcion_usada PARCIER PYC
  ;

funcion: declaracion_funcion
  | implementacion_funcion
  | uso_funcion
  ;

while_data: WHILE PARABRE operacion_logica PARCIER;

bucle_while: while_data bloque ;

for_data: declaraciones
  |
  ;

for_expresion: ID SUMA SUMA 
  | 
  ;

for_tipo: FOR PARABRE for_data operacion_logica PYC for_expresion PARCIER;

ciclo_for: for_tipo bloque;

COMMENT : '//'.*?'\n' -> skip;
WS : [ \n\t] -> skip;
TRASH: . -> skip;