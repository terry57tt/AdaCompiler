package org.pcl.structure.automaton;

public class IncorrectAutomatonException extends RuntimeException {

    public IncorrectAutomatonException(Character transition) {
        super("The automaton is not deterministic with the transition: " + transition);
    }
}
