package org.pcl.structure.automaton;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Represent a state of the automaton. */
public class AutomatonState {

    /** The adjacent states are the states that can be reached from this state. */
    private final ArrayList<AutomatonState> adjacent;

    /** The transition is the character that will be used to go to this state. */
    private final Character transition;

    /** If the state is final, it means that it is a final state in the automaton. */
    private final boolean isFinal;

    /** The token type is the type of the token that will be recognized if the state is final. */
    private final TokenType tokenType;

    /** Represents all transitions who loop */
    private final ArrayList<Character> loop;

    public AutomatonState() {
        this(null, true, null);
    }

    /** Create automatonstate final with tokenType null*/
    public AutomatonState(Character transition) {
        this(transition, true, null);
    }

    /** Create automatonstate final */
    public AutomatonState(Character transition, TokenType tokenType) {
        this(transition, true, tokenType);
    }

    public AutomatonState(Character transition, boolean isFinal) {
        this(transition, isFinal, null);
    }

    public AutomatonState(Character transition, boolean isFinal, TokenType tokenType) {
        this.transition = transition;
        this.isFinal = isFinal;
        this.tokenType = tokenType;
        this.adjacent = new ArrayList<>();
        this.loop = new ArrayList<>();
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
        for (Character transition_loop: this.loop) {
            if (transitions.contains(transition_loop)) {
                throw new IncorrectAutomatonException(transition_loop);
            }
            transitions.add(transition_loop);
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


    public void addAdjacents(List<AutomatonState> Liststates) throws IncorrectAutomatonException {
        for (AutomatonState state : Liststates) {
            this.addAdjacent(state);
        }
    }

    /** Add a regex to the loop transitions. */    
    public void addRegexLoop(String regex) {
        loop.addAll(findMatchingCharacters(regex));
    }

    /** Find all characters that match the regex. */
    private ArrayList<Character> findMatchingCharacters(String regex) {
        ArrayList<Character> matchingCharacters = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);

        for (char c = 0; c < 128; c++) { // Consider ASCII characters
            String characterString = String.valueOf(c);
            Matcher matcher = pattern.matcher(characterString);

            if (matcher.matches()) {
                matchingCharacters.add(c);
            }
        }

        return matchingCharacters;
    }

    public void addLoop(Character transition) throws IncorrectAutomatonException {
        isDeterministic(transition);
        this.loop.add(transition);
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

    public TokenType getTokenType() {
        if (isFinal) {
            return tokenType;
        }
        return null;
    }

    /** Allow to get the state that can be reached with the transition.
     * Throws runTime Exception*/
    public AutomatonState naviguate(Character transition) throws InvalidStateException {
        for (AutomatonState state: this.adjacent) {
            if (state.getTransition().equals(transition)) {
                return state;
            }
        }
        for (Character transition_loop: this.loop) {
            if (transition_loop.equals(transition)) {
                return this;
            }
        }
        throw new InvalidStateException(transition);
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
