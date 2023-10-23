package org.pcl.structure.automaton;

import java.util.ArrayList;
import java.util.HashSet;

/** Represent a state of the automaton. */
public class AutomatonState {

    /** The adjacent states are the states that can be reached from this state. */
    private final ArrayList<AutomatonState> adjacent;

    /** The transition is the character that will be used to go to this state. */
    private final  Character transition;

    /** If the state is final, it means that it is a final state in the automaton. */
    private final boolean isFinal;


    public AutomatonState(Character transition, boolean isFinal) {
        this.transition = transition;
        this.isFinal = isFinal;
        this.adjacent = new ArrayList<>();

    }

    /** Test that the automaton is deterministic with then new transition. */
    private void isDeterministic(Character transition) throws IncorrectAutomatonException{
        HashSet<Character> transitions = new HashSet<>();
        transitions.add(transition);
        for (AutomatonState state: this.adjacent) {
            if (transitions.contains(state.getTransition())) {
                throw new IncorrectAutomatonException(state.getTransition());
            }
            transitions.add(state.getTransition());
        }
    }


    /** Add a state to the adjacent states.
     * throws a RunTimeException instead of a boolean to make sur the graph is deterministic because
     * it will be too heavy on the programmer side to check to each call that the function return true, since the automaton
     * should be already deterministic it's a security with no impact on programmer side*/
    public void addAdjacent(AutomatonState state) throws IncorrectAutomatonException {
        isDeterministic(state.getTransition());
        this.adjacent.add(state);
    }

    /** Return if the states is final. */
    public boolean isFinal() {
        return isFinal;
    }

    public ArrayList<AutomatonState> getAdjacent() {
        return adjacent;
    }

    public Character getTransition() {
        return transition;
    }

    /** Allow to get the state that can be reached with the transition.
     * Throws runTime Exception*/
    public AutomatonState naviguate(Character transition) throws InvalidStateExeception {
        for (AutomatonState state: this.adjacent) {
            if (state.getTransition().equals(transition)) {
                return state;
            }
        }
        throw new InvalidStateExeception(transition);
    }

    /** Return the state as a string. */
    @Override
    public String toString() {
        return "AutomatonState{" +
                "transition=" + transition +
                ", isFinal=" + isFinal +
                '}';
    }
}
