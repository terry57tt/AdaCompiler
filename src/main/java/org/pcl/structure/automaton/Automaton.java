package org.pcl.structure.automaton;

/** Create the automaton that will be used to recognize the tokens. */
public class Automaton {
    private final AutomatonState initialState;
    private AutomatonState currentState;

    /** The Character of the initialstate doesn't matter even recommended to put null */
    public Automaton(AutomatonState initialState) {
        this.initialState = initialState;
        this.currentState = initialState;
    }

    /** Reset the automaton to the initial state. */
    public void reset() {
        this.currentState = this.initialState;
    }

    /** Return if the automaton is in a final state. */
    public boolean isFinal() {
        return this.currentState.isFinal();
    }

    /** Advance the automaton to the next state. */
    public void advance(char transition) throws InvalidStateException {
        this.currentState = this.currentState.naviguate(transition);
    }

    /** Return the current state of the automaton. */
    public AutomatonState getCurrentState() {
        return this.currentState;
    }

    /** Return the initial state of the automaton. */
    public AutomatonState getInitialState() {
        return this.initialState;
    }

    /** Return the automaton as a string. */
    @Override
    public String toString() {
        return "Automaton{" +
                "initialState=" + initialState +
                ", currentState=" + currentState +
                '}';
    }
}
