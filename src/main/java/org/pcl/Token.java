package org.pcl;

import org.pcl.structure.automaton.TokenType;

public class Token {
    TokenType type;
    String value;
    long lineNumber;

    private Token(TokenType type, String value, long lineNumber){
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    private TokenType getType(){
        return type;
    }

    private String getValue(){
        return value;
    }

    private long getLineNumber(){
        return lineNumber;
    }
}
