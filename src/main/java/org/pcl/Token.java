package org.pcl;

import org.pcl.structure.automaton.TokenType;

public class Token {
    TokenType type;
    String value;
    long lineNumber;
    public Token(String value){
        this.type = null;
        this.value = "";
        this.lineNumber = -1;
    }
    private Token(TokenType type, String value, long lineNumber){
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    private TokenType getType(){
        return this.type;
    }

    private String getValue(){
        return this.value;
    }

    private long getLineNumber(){
        return this.lineNumber;
    }
}
