package org.pcl.structure.automaton;


/** Create the automaton that will be used to recognize the tokens. */
public class Automaton {
    private final AutomatonState initialState;
    private AutomatonState currentState;

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

    //TODO Missing function like advance probably other don't know
}
