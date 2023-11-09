package org.pcl.structure.automaton;

/** Exception thrown when the state is invalid because of a transition who's not recognize. */
public class InvalidStateException extends RuntimeException {

    public InvalidStateException(Character transition) {
        super("Invalid transition : '" + transition + "'");
    }
}
