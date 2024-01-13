package org.pcl.structure.automaton;

public enum TokenType {
    IDENTIFIER,
    OPERATOR, // = < > <= >= + - *
    NUMBER,
    INTEGER,
    KEYWORD,
    SEPARATOR,  // ; ( , ) { } [ ] .  : ::
    STRING,
    CHARACTER
}
