package org.pcl;

import org.pcl.structure.automaton.TokenType;

public class Token {
    private TokenType type;
    private String value;
    private long lineNumber;

    public Token(String value){
        this(null, value, -1);
    }

    public Token(TokenType type, String value, long lineNumber){
        this.type = type;
        this.value = value;
        this.lineNumber = lineNumber;
    }

    public TokenType getType(){
        return this.type;
    }

    public void setType(TokenType type){
        this.type = type;
    }

    public String getValue(){
        return this.value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public long getLineNumber(){
        return this.lineNumber;
    }

    public void setLineNumber(long lineNumber){
        this.lineNumber = lineNumber;
    }
}
