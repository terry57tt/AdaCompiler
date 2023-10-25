package org.pcl.structure.automaton;

public class Graph {
    //On cree tous les etats de l'automate avec new automatonState
    //On cree les liens entre les etats avec addAdjacent
    //M

    public static Automaton create() {

        AutomatonState init_state = new AutomatonState('?', false);
        
        //Tous les mots clé d'abord

        //access
        AutomatonState state0 = new AutomatonState('a', false);
        AutomatonState state1 = new AutomatonState('c', false);
        AutomatonState state2 = new AutomatonState('c', false);
        AutomatonState state3 = new AutomatonState('e', false);
        AutomatonState state4 = new AutomatonState('s', false);
        AutomatonState state5 = new AutomatonState('s', true);
        init_state.addAdjacent(state0);
        state0.addAdjacent(state1);
        state1.addAdjacent(state2);
        state2.addAdjacent(state3);
        state3.addAdjacent(state4);
        state4.addAdjacent(state5);

        //and
        AutomatonState state6 = new AutomatonState('n', false);
        AutomatonState state7 = new AutomatonState('d', true);
        state0.addAdjacent(state6);
        state6.addAdjacent(state7);

        //begin
        AutomatonState state8 = new AutomatonState('b', false);
        AutomatonState state9 = new AutomatonState('e', false);
        AutomatonState state10 = new AutomatonState('g', false);
        AutomatonState state11 = new AutomatonState('i', false);
        AutomatonState state12 = new AutomatonState('n', true);
        init_state.addAdjacent(state8);
        state8.addAdjacent(state9);
        state9.addAdjacent(state10);
        state10.addAdjacent(state11);
        state11.addAdjacent(state12);

        //else
        AutomatonState state13 = new AutomatonState('e', false);
        AutomatonState state14 = new AutomatonState('l', false);
        AutomatonState state15 = new AutomatonState('s', false);
        AutomatonState state16 = new AutomatonState('e', false);
        init_state.addAdjacent(state13);
        state13.addAdjacent(state14);
        state14.addAdjacent(state15);
        state15.addAdjacent(state16);

        //elsif
        AutomatonState state20 = new AutomatonState('i', false);
        AutomatonState state21 = new AutomatonState('f', false);
        state15.addAdjacent(state20);
        state20.addAdjacent(state21);

        //end
        AutomatonState state22 = new AutomatonState('n', false);
        AutomatonState state23 = new AutomatonState('d', false);
        state13.addAdjacent(state22);
        state22.addAdjacent(state23);

        //false
        AutomatonState state24 = new AutomatonState('f', false);
        AutomatonState state25 = new AutomatonState('a', false);
        AutomatonState state26 = new AutomatonState('l', false);
        AutomatonState state27 = new AutomatonState('s', false);
        AutomatonState state28 = new AutomatonState('e', false);
        init_state.addAdjacent(state24);
        state24.addAdjacent(state25);
        state25.addAdjacent(state26);
        state26.addAdjacent(state27);
        state27.addAdjacent(state28);

        //for
        AutomatonState state29 = new AutomatonState('o', false);
        AutomatonState state30 = new AutomatonState('r', false);
        state24.addAdjacent(state29);
        state29.addAdjacent(state30);

        //function
        AutomatonState state31 = new AutomatonState('u', false);
        AutomatonState state32 = new AutomatonState('n', false);
        AutomatonState state33 = new AutomatonState('c', false);
        AutomatonState state34 = new AutomatonState('t', false);
        AutomatonState state35 = new AutomatonState('i', false);
        AutomatonState state36 = new AutomatonState('o', false);
        AutomatonState state37 = new AutomatonState('n', false);
        state24.addAdjacent(state31);
        state31.addAdjacent(state32);
        state32.addAdjacent(state33);
        state33.addAdjacent(state34);
        state34.addAdjacent(state35);
        state35.addAdjacent(state36);
        state36.addAdjacent(state37);

        //if
        AutomatonState state38 = new AutomatonState('i', false);
        AutomatonState state39 = new AutomatonState('f', false);
        init_state.addAdjacent(state38);
        state38.addAdjacent(state39);

        //in 
        AutomatonState state40 = new AutomatonState('n', false);
        state38.addAdjacent(state40);

        //is
        AutomatonState state41 = new AutomatonState('s', false);
        state38.addAdjacent(state41);

        //loop
        AutomatonState state42 = new AutomatonState('l', false);
        AutomatonState state43 = new AutomatonState('o', false);
        AutomatonState state44 = new AutomatonState('o', false);
        AutomatonState state45 = new AutomatonState('p', false);
        init_state.addAdjacent(state42);
        state42.addAdjacent(state43);
        state43.addAdjacent(state44);
        state44.addAdjacent(state45);

        //new 
        AutomatonState state46 = new AutomatonState('n', false);
        AutomatonState state47 = new AutomatonState('e', false);
        AutomatonState state48 = new AutomatonState('w', false);
        init_state.addAdjacent(state46);
        state46.addAdjacent(state47);
        state47.addAdjacent(state48);

        //not
        AutomatonState state49 = new AutomatonState('o', false);
        AutomatonState state50 = new AutomatonState('t', false);
        state46.addAdjacent(state49);
        state49.addAdjacent(state50);

        //null
        AutomatonState state51 = new AutomatonState('u', false);
        AutomatonState state52 = new AutomatonState('l', false);
        AutomatonState state53 = new AutomatonState('l', false);
        state46.addAdjacent(state51);
        state51.addAdjacent(state52);
        state52.addAdjacent(state53);

        //or
        AutomatonState state54 = new AutomatonState('o', false);
        AutomatonState state55 = new AutomatonState('r', false);
        init_state.addAdjacent(state54);
        state54.addAdjacent(state55);

        //out
        AutomatonState state56 = new AutomatonState('u', false);
        AutomatonState state57 = new AutomatonState('t', false);
        state54.addAdjacent(state56);
        state56.addAdjacent(state57);

        //procedure
        AutomatonState state58 = new AutomatonState('p', false);
        AutomatonState state59 = new AutomatonState('r', false);
        AutomatonState state60 = new AutomatonState('o', false);
        AutomatonState state61 = new AutomatonState('c', false);
        AutomatonState state62 = new AutomatonState('e', false);
        AutomatonState state63 = new AutomatonState('d', false);
        AutomatonState state64 = new AutomatonState('u', false);
        AutomatonState state65 = new AutomatonState('r', false);
        AutomatonState state66 = new AutomatonState('e', false);
        init_state.addAdjacent(state58);
        state58.addAdjacent(state59);
        state59.addAdjacent(state60);
        state60.addAdjacent(state61);
        state61.addAdjacent(state62);
        state62.addAdjacent(state63);
        state63.addAdjacent(state64);
        state64.addAdjacent(state65);
        state65.addAdjacent(state66);

        //record 
        AutomatonState state67 = new AutomatonState('r', false);
        AutomatonState state68 = new AutomatonState('e', false);
        AutomatonState state69 = new AutomatonState('c', false);
        AutomatonState state70 = new AutomatonState('o', false);
        AutomatonState state71 = new AutomatonState('r', false);
        AutomatonState state72 = new AutomatonState('d', false);
        init_state.addAdjacent(state67);
        state67.addAdjacent(state68);
        state68.addAdjacent(state69);
        state69.addAdjacent(state70);
        state70.addAdjacent(state71);
        state71.addAdjacent(state72);

        //rem
        AutomatonState state74 = new AutomatonState('m', false);
        state68.addAdjacent(state74);

        //return
        AutomatonState state76 = new AutomatonState('t', false);
        AutomatonState state77 = new AutomatonState('u', false);
        AutomatonState state78 = new AutomatonState('r', false);
        AutomatonState state79 = new AutomatonState('n', false);
        state68.addAdjacent(state76);
        state76.addAdjacent(state77);
        state77.addAdjacent(state78);
        state78.addAdjacent(state79);


        //reverse
        AutomatonState state81 = new AutomatonState('v', false);
        AutomatonState state82 = new AutomatonState('e', false);
        AutomatonState state83 = new AutomatonState('r', false);
        AutomatonState state84 = new AutomatonState('s', false);
        AutomatonState state85 = new AutomatonState('e', false);
        state68.addAdjacent(state81);
        state81.addAdjacent(state82);
        state82.addAdjacent(state83);
        state83.addAdjacent(state84);
        state84.addAdjacent(state85);

        //then 
        AutomatonState state86 = new AutomatonState('t', false);
        AutomatonState state87 = new AutomatonState('h', false);
        AutomatonState state88 = new AutomatonState('e', false);
        AutomatonState state89 = new AutomatonState('n', false);
        init_state.addAdjacent(state86);
        state86.addAdjacent(state87);
        state87.addAdjacent(state88);
        state88.addAdjacent(state89);

        //true
        AutomatonState state90 = new AutomatonState('r', false);
        AutomatonState state91 = new AutomatonState('u', false);
        AutomatonState state92 = new AutomatonState('e', false);
        state86.addAdjacent(state90);
        state90.addAdjacent(state91);
        state91.addAdjacent(state92);

        //type
        AutomatonState state93 = new AutomatonState('y', false);
        AutomatonState state94 = new AutomatonState('p', false);
        AutomatonState state95 = new AutomatonState('e', false);
        state86.addAdjacent(state93);
        state93.addAdjacent(state94);
        state94.addAdjacent(state95);

        //use 
        AutomatonState state96 = new AutomatonState('u', false);
        AutomatonState state97 = new AutomatonState('s', false);
        AutomatonState state98 = new AutomatonState('e', false);
        init_state.addAdjacent(state96);
        state96.addAdjacent(state97);
        state97.addAdjacent(state98);

        //while
        AutomatonState state99 = new AutomatonState('w', false);
        AutomatonState state100 = new AutomatonState('h', false);
        AutomatonState state101 = new AutomatonState('i', false);
        AutomatonState state102 = new AutomatonState('l', false);
        AutomatonState state103 = new AutomatonState('e', false);
        init_state.addAdjacent(state99);
        state99.addAdjacent(state100);
        state100.addAdjacent(state101);
        state101.addAdjacent(state102);
        state102.addAdjacent(state103);

        //with
        AutomatonState state104 = new AutomatonState('i', false);
        AutomatonState state105 = new AutomatonState('t', false);
        AutomatonState state106 = new AutomatonState('h', false);
        state99.addAdjacent(state104);
        state104.addAdjacent(state105);
        state105.addAdjacent(state106);

        //Tous les opérateurs

        // +
        AutomatonState state107 = new AutomatonState('+', true);
        init_state.addAdjacent(state107);

        // -
        AutomatonState state108 = new AutomatonState('-', true);
        init_state.addAdjacent(state108);

        // *
        AutomatonState state109 = new AutomatonState('*', true);
        init_state.addAdjacent(state109);

        // / (diviser) et /=
        AutomatonState state110 = new AutomatonState('/', true);
        AutomatonState state111 = new AutomatonState('=', true);
        init_state.addAdjacent(state110);
        state110.addAdjacent(state111);

        // < et <=
        AutomatonState state112 = new AutomatonState('<', true);
        AutomatonState state113 = new AutomatonState('=', true);
        init_state.addAdjacent(state112);
        state111.addAdjacent(state113);

        // > et >=
        AutomatonState state114 = new AutomatonState('>', true);
        AutomatonState state115 = new AutomatonState('=', true);
        init_state.addAdjacent(state114);
        state112.addAdjacent(state115);

        // =
        AutomatonState state116 = new AutomatonState('=', true);
        init_state.addAdjacent(state116);

        // (
        AutomatonState state117 = new AutomatonState('(', true);
        init_state.addAdjacent(state117);

        // )
        AutomatonState state118 = new AutomatonState(')', true);
        init_state.addAdjacent(state118);

        // ,
        AutomatonState state119 = new AutomatonState(',', true);
        init_state.addAdjacent(state119);

        // ;
        AutomatonState state120 = new AutomatonState(';', true);
        init_state.addAdjacent(state120);

        // : et :=
        AutomatonState state121 = new AutomatonState(':', true);
        AutomatonState state122 = new AutomatonState('=', true);
        init_state.addAdjacent(state121);
        state121.addAdjacent(state122);

        // .
        AutomatonState state123 = new AutomatonState('.', true);
        init_state.addAdjacent(state123);

        // '
        AutomatonState state124 = new AutomatonState('\'', true);
        init_state.addAdjacent(state124);

        // Les entiers
        AutomatonState state125 = new AutomatonState('0', true);
        AutomatonState state126 = new AutomatonState('1', true);
        AutomatonState state127 = new AutomatonState('2', true);
        AutomatonState state128 = new AutomatonState('3', true);
        AutomatonState state129 = new AutomatonState('4', true);
        AutomatonState state130 = new AutomatonState('5', true);
        AutomatonState state131 = new AutomatonState('6', true);
        AutomatonState state132 = new AutomatonState('7', true);
        AutomatonState state133 = new AutomatonState('8', true);
        AutomatonState state134 = new AutomatonState('9', true);
        init_state.addAdjacent(state125);
        init_state.addAdjacent(state126);
        init_state.addAdjacent(state127);
        init_state.addAdjacent(state128);
        init_state.addAdjacent(state129);
        init_state.addAdjacent(state130);
        init_state.addAdjacent(state131);
        init_state.addAdjacent(state132);
        init_state.addAdjacent(state133);
        init_state.addAdjacent(state134);
        //On fait en sorte qu'il boucle pour qu'il puisse reconnaitre n'importe quelle entier
        state125.addLoop('0');
        state125.addLoop('1');
        state125.addLoop('2');
        state125.addLoop('3');
        state125.addLoop('4');
        state125.addLoop('5');
        state125.addLoop('6');
        state125.addLoop('7');
        state125.addLoop('8');
        state125.addLoop('9');
        state126.addLoop('0');
        state126.addLoop('1');
        state126.addLoop('2');
        state126.addLoop('3');
        state126.addLoop('4');
        state126.addLoop('5');
        state126.addLoop('6');
        state126.addLoop('7');
        state126.addLoop('8');
        state126.addLoop('9');
        state127.addLoop('0');
        state127.addLoop('1');
        state127.addLoop('2');
        state127.addLoop('3');
        state127.addLoop('4');
        state127.addLoop('5');
        state127.addLoop('6');
        state127.addLoop('7');
        state127.addLoop('8');
        state127.addLoop('9');
        state128.addLoop('0');
        state128.addLoop('1');
        state128.addLoop('2');
        state128.addLoop('3');
        state128.addLoop('4');
        state128.addLoop('5');
        state128.addLoop('6');
        state128.addLoop('7');
        state128.addLoop('8');
        state128.addLoop('9');

        return new Automaton(init_state);
    }
}
