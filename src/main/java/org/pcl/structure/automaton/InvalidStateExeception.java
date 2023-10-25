package org.pcl.structure.automaton;

/** Exception thrown when the state is invalid because of a transition who's not recognize. */
public class InvalidStateExeception extends RuntimeException {

    public InvalidStateExeception(Character transition) {
        super("Invalid transition : '" + transition + "'");
    }
}
