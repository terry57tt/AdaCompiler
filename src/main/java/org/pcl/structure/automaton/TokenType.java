package org.pcl.structure.automaton;

public enum TokenType {
    IDENTIFIER,
    OPERATOR, // = < > <= >= + - *
    NUMBER,
    INTEGER,
    FLOAT,
    KEYWORD,
    SEPARATOR,  // ; ( , ) { } [ ] .  : ::
    STRING,
    CHARACTER,

}
