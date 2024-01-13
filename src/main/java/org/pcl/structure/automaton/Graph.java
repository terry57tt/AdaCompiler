package org.pcl.structure.automaton;
import java.util.Arrays;
import java.util.List;

public class Graph {
    //On cree tous les etats de l'automate avec new automatonState
    //On cree les liens entre les etats avec addAdjacent
    //M

    public static Automaton create() {

        AutomatonState init_state = new AutomatonState('?', false);
        
        //Tous les mots clés

        //access
        AutomatonState state0 = new AutomatonState('a', true, TokenType.IDENTIFIER);
        AutomatonState state1 = new AutomatonState('c', true, TokenType.IDENTIFIER);
        AutomatonState state2 = new AutomatonState('c', true, TokenType.IDENTIFIER);
        AutomatonState state3 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state4 = new AutomatonState('s', true, TokenType.IDENTIFIER);
        AutomatonState state5 = new AutomatonState('s', true, TokenType.KEYWORD);
        init_state.addAdjacent(state0);
        state0.addAdjacent(state1);
        state1.addAdjacent(state2);
        state2.addAdjacent(state3);
        state3.addAdjacent(state4);
        state4.addAdjacent(state5);

        //and
        AutomatonState state6 = new AutomatonState('n', true, TokenType.IDENTIFIER);
        AutomatonState state7 = new AutomatonState('d', true, TokenType.KEYWORD);
        state0.addAdjacent(state6);
        state6.addAdjacent(state7);

        //begin
        AutomatonState state8 = new AutomatonState('b', true, TokenType.IDENTIFIER);
        AutomatonState state9 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state10 = new AutomatonState('g', true, TokenType.IDENTIFIER);
        AutomatonState state11 = new AutomatonState('i', true, TokenType.IDENTIFIER);
        AutomatonState state12 = new AutomatonState('n', true, TokenType.KEYWORD);
        init_state.addAdjacent(state8);
        state8.addAdjacent(state9);
        state9.addAdjacent(state10);
        state10.addAdjacent(state11);
        state11.addAdjacent(state12);

        //else
        AutomatonState state13 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state14 = new AutomatonState('l', true, TokenType.IDENTIFIER);
        AutomatonState state15 = new AutomatonState('s', true, TokenType.IDENTIFIER);
        AutomatonState state16 = new AutomatonState('e', true, TokenType.KEYWORD);
        init_state.addAdjacent(state13);
        state13.addAdjacent(state14);
        state14.addAdjacent(state15);
        state15.addAdjacent(state16);

        //elsif
        AutomatonState state20 = new AutomatonState('i', true, TokenType.IDENTIFIER);
        AutomatonState state21 = new AutomatonState('f', true, TokenType.KEYWORD);
        state15.addAdjacent(state20);
        state20.addAdjacent(state21);

        //end
        AutomatonState state22 = new AutomatonState('n', true, TokenType.IDENTIFIER);
        AutomatonState state23 = new AutomatonState('d', true, TokenType.KEYWORD);
        state13.addAdjacent(state22);
        state22.addAdjacent(state23);

        //false
        AutomatonState state24 = new AutomatonState('f', true, TokenType.IDENTIFIER);
        AutomatonState state25 = new AutomatonState('a', true, TokenType.IDENTIFIER);
        AutomatonState state26 = new AutomatonState('l', true, TokenType.IDENTIFIER);
        AutomatonState state27 = new AutomatonState('s', true, TokenType.IDENTIFIER);
        AutomatonState state28 = new AutomatonState('e', true, TokenType.KEYWORD);
        init_state.addAdjacent(state24);
        state24.addAdjacent(state25);
        state25.addAdjacent(state26);
        state26.addAdjacent(state27);
        state27.addAdjacent(state28);

        //for
        AutomatonState state29 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state30 = new AutomatonState('r', true, TokenType.KEYWORD);
        state24.addAdjacent(state29);
        state29.addAdjacent(state30);

        //function
        AutomatonState state31 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state32 = new AutomatonState('n', true, TokenType.IDENTIFIER);
        AutomatonState state33 = new AutomatonState('c', true, TokenType.IDENTIFIER);
        AutomatonState state34 = new AutomatonState('t', true, TokenType.IDENTIFIER);
        AutomatonState state35 = new AutomatonState('i', true, TokenType.IDENTIFIER);
        AutomatonState state36 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state37 = new AutomatonState('n', true, TokenType.KEYWORD);
        state24.addAdjacent(state31);
        state31.addAdjacent(state32);
        state32.addAdjacent(state33);
        state33.addAdjacent(state34);
        state34.addAdjacent(state35);
        state35.addAdjacent(state36);
        state36.addAdjacent(state37);

        //if
        AutomatonState state38 = new AutomatonState('i', true, TokenType.IDENTIFIER);
        AutomatonState state39 = new AutomatonState('f', true, TokenType.KEYWORD);
        init_state.addAdjacent(state38);
        state38.addAdjacent(state39);

        //in 
        AutomatonState state40 = new AutomatonState('n', true, TokenType.KEYWORD);
        state38.addAdjacent(state40);

        //is
        AutomatonState state41 = new AutomatonState('s', true, TokenType.KEYWORD);
        state38.addAdjacent(state41);

        //loop
        AutomatonState state42 = new AutomatonState('l', true, TokenType.IDENTIFIER);
        AutomatonState state43 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state44 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state45 = new AutomatonState('p', true, TokenType.KEYWORD);
        init_state.addAdjacent(state42);
        state42.addAdjacent(state43);
        state43.addAdjacent(state44);
        state44.addAdjacent(state45);

        //new 
        AutomatonState state46 = new AutomatonState('n', true, TokenType.IDENTIFIER);
        AutomatonState state47 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state48 = new AutomatonState('w', true, TokenType.KEYWORD);
        init_state.addAdjacent(state46);
        state46.addAdjacent(state47);
        state47.addAdjacent(state48);

        //not
        AutomatonState state49 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state50 = new AutomatonState('t', true, TokenType.KEYWORD);
        state46.addAdjacent(state49);
        state49.addAdjacent(state50);

        //null
        AutomatonState state51 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state52 = new AutomatonState('l', true, TokenType.IDENTIFIER);
        AutomatonState state53 = new AutomatonState('l', true, TokenType.KEYWORD);
        state46.addAdjacent(state51);
        state51.addAdjacent(state52);
        state52.addAdjacent(state53);

        //or
        AutomatonState state54 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state55 = new AutomatonState('r', true, TokenType.KEYWORD);
        init_state.addAdjacent(state54);
        state54.addAdjacent(state55);

        //out
        AutomatonState state56 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state57 = new AutomatonState('t', true, TokenType.KEYWORD);
        state54.addAdjacent(state56);
        state56.addAdjacent(state57);

        //procedure
        AutomatonState state58 = new AutomatonState('p', true, TokenType.IDENTIFIER);
        AutomatonState state59 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state60 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state61 = new AutomatonState('c', true, TokenType.IDENTIFIER);
        AutomatonState state62 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state63 = new AutomatonState('d', true, TokenType.IDENTIFIER);
        AutomatonState state64 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state65 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state66 = new AutomatonState('e', true, TokenType.KEYWORD);
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
        AutomatonState state67 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state68 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state69 = new AutomatonState('c', true, TokenType.IDENTIFIER);
        AutomatonState state70 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state71 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state72 = new AutomatonState('d', true, TokenType.KEYWORD);
        init_state.addAdjacent(state67);
        state67.addAdjacent(state68);
        state68.addAdjacent(state69);
        state69.addAdjacent(state70);
        state70.addAdjacent(state71);
        state71.addAdjacent(state72);

        //rem
        AutomatonState state74 = new AutomatonState('m', true, TokenType.KEYWORD);
        state68.addAdjacent(state74);

        //return
        AutomatonState state76 = new AutomatonState('t', true, TokenType.IDENTIFIER);
        AutomatonState state77 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state78 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state79 = new AutomatonState('n', true, TokenType.KEYWORD);
        state68.addAdjacent(state76);
        state76.addAdjacent(state77);
        state77.addAdjacent(state78);
        state78.addAdjacent(state79);


        //reverse
        AutomatonState state81 = new AutomatonState('v', true, TokenType.IDENTIFIER);
        AutomatonState state82 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state83 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state84 = new AutomatonState('s', true, TokenType.IDENTIFIER);
        AutomatonState state85 = new AutomatonState('e', true, TokenType.KEYWORD);
        state68.addAdjacent(state81);
        state81.addAdjacent(state82);
        state82.addAdjacent(state83);
        state83.addAdjacent(state84);
        state84.addAdjacent(state85);

        //then 
        AutomatonState state86 = new AutomatonState('t', true, TokenType.IDENTIFIER);
        AutomatonState state87 = new AutomatonState('h', true, TokenType.IDENTIFIER);
        AutomatonState state88 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state89 = new AutomatonState('n', true, TokenType.KEYWORD);
        init_state.addAdjacent(state86);
        state86.addAdjacent(state87);
        state87.addAdjacent(state88);
        state88.addAdjacent(state89);

        //true, TokenType.IDENTIFIER
        AutomatonState state90 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state91 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state92 = new AutomatonState('e', true, TokenType.KEYWORD);
        state86.addAdjacent(state90);
        state90.addAdjacent(state91);
        state91.addAdjacent(state92);

        //type
        AutomatonState state93 = new AutomatonState('y', true, TokenType.IDENTIFIER);
        AutomatonState state94 = new AutomatonState('p', true, TokenType.IDENTIFIER);
        AutomatonState state95 = new AutomatonState('e', true, TokenType.KEYWORD);
        state86.addAdjacent(state93);
        state93.addAdjacent(state94);
        state94.addAdjacent(state95);

        //use 
        AutomatonState state96 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state97 = new AutomatonState('s', true, TokenType.IDENTIFIER);
        AutomatonState state98 = new AutomatonState('e', true, TokenType.KEYWORD);
        init_state.addAdjacent(state96);
        state96.addAdjacent(state97);
        state97.addAdjacent(state98);

        //while
        AutomatonState state99 = new AutomatonState('w', true, TokenType.IDENTIFIER);
        AutomatonState state100 = new AutomatonState('h', true, TokenType.IDENTIFIER);
        AutomatonState state101 = new AutomatonState('i', true, TokenType.IDENTIFIER);
        AutomatonState state102 = new AutomatonState('l', true, TokenType.IDENTIFIER);
        AutomatonState state103 = new AutomatonState('e', true, TokenType.KEYWORD);
        init_state.addAdjacent(state99);
        state99.addAdjacent(state100);
        state100.addAdjacent(state101);
        state101.addAdjacent(state102);
        state102.addAdjacent(state103);

        //with
        AutomatonState state104 = new AutomatonState('i', true, TokenType.IDENTIFIER);
        AutomatonState state105 = new AutomatonState('t', true, TokenType.IDENTIFIER);
        AutomatonState state106 = new AutomatonState('h', true, TokenType.KEYWORD);
        state99.addAdjacent(state104);
        state104.addAdjacent(state105);
        state105.addAdjacent(state106);

        //Tous les opérateurs

        // +
        AutomatonState state107 = new AutomatonState('+', true, TokenType.OPERATOR);
        init_state.addAdjacent(state107);

        // -
        AutomatonState state108 = new AutomatonState('-', true, TokenType.OPERATOR);
        init_state.addAdjacent(state108);
        // ajout des nombres négatifs
        AutomatonState state108_0 = new AutomatonState('0', true, TokenType.NUMBER);
        AutomatonState state108_1 = new AutomatonState('1', true, TokenType.NUMBER);
        AutomatonState state108_2 = new AutomatonState('2', true, TokenType.NUMBER);
        AutomatonState state108_3 = new AutomatonState('3', true, TokenType.NUMBER);
        AutomatonState state108_4 = new AutomatonState('4', true, TokenType.NUMBER);
        AutomatonState state108_5 = new AutomatonState('5', true, TokenType.NUMBER);
        AutomatonState state108_6 = new AutomatonState('6', true, TokenType.NUMBER);
        AutomatonState state108_7 = new AutomatonState('7', true, TokenType.NUMBER);
        AutomatonState state108_8 = new AutomatonState('8', true, TokenType.NUMBER);
        AutomatonState state108_9 = new AutomatonState('9', true, TokenType.NUMBER);
        state108.addAdjacent(state108_0);
        state108.addAdjacent(state108_1);
        state108.addAdjacent(state108_2);
        state108.addAdjacent(state108_3);
        state108.addAdjacent(state108_4);
        state108.addAdjacent(state108_5);
        state108.addAdjacent(state108_6);
        state108.addAdjacent(state108_7);
        state108.addAdjacent(state108_8);
        state108.addAdjacent(state108_9);
        //On fait en sorte qu'il boucle pour qu'il puisse reconnaitre n'importe quel entier négatif
        state108_0.addRegexLoop("[0-9]*");
        state108_1.addRegexLoop("[0-9]*");
        state108_2.addRegexLoop("[0-9]*");
        state108_3.addRegexLoop("[0-9]*");
        state108_4.addRegexLoop("[0-9]*");
        state108_5.addRegexLoop("[0-9]*");
        state108_6.addRegexLoop("[0-9]*");
        state108_7.addRegexLoop("[0-9]*");
        state108_8.addRegexLoop("[0-9]*");
        state108_9.addRegexLoop("[0-9]*");


        // *
        AutomatonState state109 = new AutomatonState('*', true, TokenType.OPERATOR);
        init_state.addAdjacent(state109);

        // / (diviser) et /=
        AutomatonState state110 = new AutomatonState('/', true, TokenType.OPERATOR);
        AutomatonState state111 = new AutomatonState('=', true, TokenType.OPERATOR);
        init_state.addAdjacent(state110);
        state110.addAdjacent(state111);

        // < et <=
        AutomatonState state112 = new AutomatonState('<', true, TokenType.OPERATOR);
        AutomatonState state113 = new AutomatonState('=', true, TokenType.OPERATOR);
        init_state.addAdjacent(state112);
        state112.addAdjacent(state113);

        // > et >=
        AutomatonState state114 = new AutomatonState('>', true, TokenType.OPERATOR);
        AutomatonState state115 = new AutomatonState('=', true, TokenType.OPERATOR);
        init_state.addAdjacent(state114);
        state114.addAdjacent(state115);

        // =
        AutomatonState state116 = new AutomatonState('=', true, TokenType.OPERATOR);
        init_state.addAdjacent(state116);

        // (
        AutomatonState state117 = new AutomatonState('(', true, TokenType.SEPARATOR);
        init_state.addAdjacent(state117);

        // )
        AutomatonState state118 = new AutomatonState(')', true, TokenType.SEPARATOR);
        init_state.addAdjacent(state118);

        // ,
        AutomatonState state119 = new AutomatonState(',', true, TokenType.SEPARATOR);
        init_state.addAdjacent(state119);

        // ;
        AutomatonState state120 = new AutomatonState(';', true, TokenType.SEPARATOR);
        init_state.addAdjacent(state120);

        // : et := et ::
        AutomatonState state121 = new AutomatonState(':', true, TokenType.SEPARATOR);
        AutomatonState state122 = new AutomatonState('=', true, TokenType.SEPARATOR);
        AutomatonState state122_1 = new AutomatonState(':', true, TokenType.SEPARATOR);
        init_state.addAdjacent(state121);
        state121.addAdjacent(state122);
        state121.addAdjacent(state122_1);

        // .
        AutomatonState state123 = new AutomatonState('.', true, TokenType.SEPARATOR);
        init_state.addAdjacent(state123);

        // '
        AutomatonState state124 = new AutomatonState('\'', true, TokenType.SEPARATOR);
        init_state.addAdjacent(state124);

        // "
        AutomatonState state_quotes = new AutomatonState('\"', true, TokenType.CHARACTER);
        init_state.addAdjacent(state_quotes);
        state_quotes.addRegexLoop("[a-zA-Z0-9$-/:-?{-~\"^_`\\[\\] ]*");

        // Les entiers
        AutomatonState state125 = new AutomatonState('0', true, TokenType.NUMBER);
        AutomatonState state126 = new AutomatonState('1', true, TokenType.NUMBER);
        AutomatonState state127 = new AutomatonState('2', true, TokenType.NUMBER);
        AutomatonState state128 = new AutomatonState('3', true, TokenType.NUMBER);
        AutomatonState state129 = new AutomatonState('4', true, TokenType.NUMBER);
        AutomatonState state130 = new AutomatonState('5', true, TokenType.NUMBER);
        AutomatonState state131 = new AutomatonState('6', true, TokenType.NUMBER);
        AutomatonState state132 = new AutomatonState('7', true, TokenType.NUMBER);
        AutomatonState state133 = new AutomatonState('8', true, TokenType.NUMBER);
        AutomatonState state134 = new AutomatonState('9', true, TokenType.NUMBER);
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
        state125.addRegexLoop("[0-9]*");
        state126.addRegexLoop("[0-9]*");
        state127.addRegexLoop("[0-9]*");
        state128.addRegexLoop("[0-9]*");
        state129.addRegexLoop("[0-9]*");
        state130.addRegexLoop("[0-9]*");
        state131.addRegexLoop("[0-9]*");
        state132.addRegexLoop("[0-9]*");
        state133.addRegexLoop("[0-9]*");
        state134.addRegexLoop("[0-9]*");

        //Etats avec majuscule et minuscule pour les identifiants
        AutomatonState state135 = new AutomatonState('A', true, TokenType.IDENTIFIER);
        AutomatonState state136 = new AutomatonState('B', true, TokenType.IDENTIFIER);
        AutomatonState state137 = new AutomatonState('C', true, TokenType.IDENTIFIER);
        AutomatonState state138 = new AutomatonState('D', true, TokenType.IDENTIFIER);
        AutomatonState state139 = new AutomatonState('E', true, TokenType.IDENTIFIER);
        AutomatonState state140 = new AutomatonState('F', true, TokenType.IDENTIFIER);
        AutomatonState state141 = new AutomatonState('G', true, TokenType.IDENTIFIER);
        AutomatonState state142 = new AutomatonState('H', true, TokenType.IDENTIFIER);
        AutomatonState state143 = new AutomatonState('I', true, TokenType.IDENTIFIER);
        AutomatonState state144 = new AutomatonState('J', true, TokenType.IDENTIFIER);
        AutomatonState state145 = new AutomatonState('K', true, TokenType.IDENTIFIER);
        AutomatonState state147 = new AutomatonState('L', true, TokenType.IDENTIFIER);
        AutomatonState state148 = new AutomatonState('M', true, TokenType.IDENTIFIER);
        AutomatonState state149 = new AutomatonState('N', true, TokenType.IDENTIFIER);
        AutomatonState state150 = new AutomatonState('O', true, TokenType.IDENTIFIER);
        AutomatonState state151 = new AutomatonState('P', true, TokenType.IDENTIFIER);
        AutomatonState state152 = new AutomatonState('Q', true, TokenType.IDENTIFIER);
        AutomatonState state153 = new AutomatonState('R', true, TokenType.IDENTIFIER);
        AutomatonState state154 = new AutomatonState('S', true, TokenType.IDENTIFIER);
        AutomatonState state155 = new AutomatonState('T', true, TokenType.IDENTIFIER);
        AutomatonState state156 = new AutomatonState('U', true, TokenType.IDENTIFIER);
        AutomatonState state157 = new AutomatonState('V', true, TokenType.IDENTIFIER);
        AutomatonState state158 = new AutomatonState('W', true, TokenType.IDENTIFIER);
        AutomatonState state159 = new AutomatonState('X', true, TokenType.IDENTIFIER);
        AutomatonState state160 = new AutomatonState('Y', true, TokenType.IDENTIFIER);
        AutomatonState state161 = new AutomatonState('Z', true, TokenType.IDENTIFIER);
        AutomatonState state162 = new AutomatonState('a', true, TokenType.IDENTIFIER);
        AutomatonState state163 = new AutomatonState('b', true, TokenType.IDENTIFIER);
        AutomatonState state164 = new AutomatonState('c', true, TokenType.IDENTIFIER);
        AutomatonState state165 = new AutomatonState('d', true, TokenType.IDENTIFIER);
        AutomatonState state166 = new AutomatonState('e', true, TokenType.IDENTIFIER);
        AutomatonState state167 = new AutomatonState('f', true, TokenType.IDENTIFIER);
        AutomatonState state168 = new AutomatonState('g', true, TokenType.IDENTIFIER);
        AutomatonState state169 = new AutomatonState('h', true, TokenType.IDENTIFIER);
        AutomatonState state170 = new AutomatonState('i', true, TokenType.IDENTIFIER);
        AutomatonState state171 = new AutomatonState('j', true, TokenType.IDENTIFIER);
        AutomatonState state172 = new AutomatonState('k', true, TokenType.IDENTIFIER);
        AutomatonState state173 = new AutomatonState('l', true, TokenType.IDENTIFIER);
        AutomatonState state174 = new AutomatonState('m', true, TokenType.IDENTIFIER);
        AutomatonState state175 = new AutomatonState('n', true, TokenType.IDENTIFIER);
        AutomatonState state176 = new AutomatonState('o', true, TokenType.IDENTIFIER);
        AutomatonState state177 = new AutomatonState('p', true, TokenType.IDENTIFIER);
        AutomatonState state178 = new AutomatonState('q', true, TokenType.IDENTIFIER);
        AutomatonState state179 = new AutomatonState('r', true, TokenType.IDENTIFIER);
        AutomatonState state180 = new AutomatonState('s', true, TokenType.IDENTIFIER);
        AutomatonState state181 = new AutomatonState('t', true, TokenType.IDENTIFIER);
        AutomatonState state182 = new AutomatonState('u', true, TokenType.IDENTIFIER);
        AutomatonState state183 = new AutomatonState('v', true, TokenType.IDENTIFIER);
        AutomatonState state184 = new AutomatonState('w', true, TokenType.IDENTIFIER);
        AutomatonState state185 = new AutomatonState('x', true, TokenType.IDENTIFIER);
        AutomatonState state186 = new AutomatonState('y', true, TokenType.IDENTIFIER);
        AutomatonState state187 = new AutomatonState('z', true, TokenType.IDENTIFIER);
        AutomatonState state188 = new AutomatonState('_', true, TokenType.IDENTIFIER);
        //On rajoute les numeros mais pour les identificateurs cette fois 
        AutomatonState state189 = new AutomatonState('0', true, TokenType.IDENTIFIER);
        AutomatonState state190 = new AutomatonState('1', true, TokenType.IDENTIFIER);
        AutomatonState state191 = new AutomatonState('2', true, TokenType.IDENTIFIER);
        AutomatonState state192 = new AutomatonState('3', true, TokenType.IDENTIFIER);
        AutomatonState state193 = new AutomatonState('4', true, TokenType.IDENTIFIER);
        AutomatonState state194 = new AutomatonState('5', true, TokenType.IDENTIFIER);
        AutomatonState state195 = new AutomatonState('6', true, TokenType.IDENTIFIER);
        AutomatonState state196 = new AutomatonState('7', true, TokenType.IDENTIFIER);
        AutomatonState state197 = new AutomatonState('8', true, TokenType.IDENTIFIER);
        AutomatonState state198 = new AutomatonState('9', true, TokenType.IDENTIFIER);

        init_state.addAdjacent(state135);
        init_state.addAdjacent(state136);
        init_state.addAdjacent(state137);
        init_state.addAdjacent(state138);
        init_state.addAdjacent(state139);
        init_state.addAdjacent(state140);
        init_state.addAdjacent(state141);
        init_state.addAdjacent(state142);
        init_state.addAdjacent(state143);
        init_state.addAdjacent(state144);
        init_state.addAdjacent(state145);
        init_state.addAdjacent(state147);
        init_state.addAdjacent(state148);
        init_state.addAdjacent(state149);
        init_state.addAdjacent(state150);
        init_state.addAdjacent(state151);
        init_state.addAdjacent(state152);
        init_state.addAdjacent(state153);
        init_state.addAdjacent(state154);
        init_state.addAdjacent(state155);
        init_state.addAdjacent(state156);
        init_state.addAdjacent(state157);
        init_state.addAdjacent(state158);
        init_state.addAdjacent(state159);
        init_state.addAdjacent(state160);
        init_state.addAdjacent(state161);
        //Pour les minuscules, on relie à l'état initial ceux qui sont des mots clés qui commence pas par cette lettre
        init_state.addAdjacent(state164); //c
        init_state.addAdjacent(state165); //d
        init_state.addAdjacent(state168); //g
        init_state.addAdjacent(state169); //h
        init_state.addAdjacent(state171); //j
        init_state.addAdjacent(state172); //k
        init_state.addAdjacent(state174); //m
        init_state.addAdjacent(state178); //q
        init_state.addAdjacent(state180); //s
        init_state.addAdjacent(state183); //v
        init_state.addAdjacent(state185); //x
        init_state.addAdjacent(state186); //y
        init_state.addAdjacent(state187); //z
        //chaque etat boucle pour reconnaitre n'importe quel identifiant
        state135.addRegexLoop("[a-zA-Z0-9_]*");
        state136.addRegexLoop("[a-zA-Z0-9_]*");
        state137.addRegexLoop("[a-zA-Z0-9_]*");
        state138.addRegexLoop("[a-zA-Z0-9_]*");
        state139.addRegexLoop("[a-zA-Z0-9_]*");
        state140.addRegexLoop("[a-zA-Z0-9_]*");
        state141.addRegexLoop("[a-zA-Z0-9_]*");
        state142.addRegexLoop("[a-zA-Z0-9_]*");
        state143.addRegexLoop("[a-zA-Z0-9_]*");
        state144.addRegexLoop("[a-zA-Z0-9_]*");
        state145.addRegexLoop("[a-zA-Z0-9_]*");
        state147.addRegexLoop("[a-zA-Z0-9_]*");
        state148.addRegexLoop("[a-zA-Z0-9_]*");
        state149.addRegexLoop("[a-zA-Z0-9_]*");
        state150.addRegexLoop("[a-zA-Z0-9_]*");
        state151.addRegexLoop("[a-zA-Z0-9_]*");
        state152.addRegexLoop("[a-zA-Z0-9_]*");
        state153.addRegexLoop("[a-zA-Z0-9_]*");
        state154.addRegexLoop("[a-zA-Z0-9_]*");
        state155.addRegexLoop("[a-zA-Z0-9_]*");
        state156.addRegexLoop("[a-zA-Z0-9_]*");
        state157.addRegexLoop("[a-zA-Z0-9_]*");
        state158.addRegexLoop("[a-zA-Z0-9_]*");
        state159.addRegexLoop("[a-zA-Z0-9_]*");
        state160.addRegexLoop("[a-zA-Z0-9_]*");
        state161.addRegexLoop("[a-zA-Z0-9_]*");
        state162.addRegexLoop("[a-zA-Z0-9_]*");
        state163.addRegexLoop("[a-zA-Z0-9_]*");
        state164.addRegexLoop("[a-zA-Z0-9_]*");
        state165.addRegexLoop("[a-zA-Z0-9_]*");
        state166.addRegexLoop("[a-zA-Z0-9_]*");
        state167.addRegexLoop("[a-zA-Z0-9_]*");
        state168.addRegexLoop("[a-zA-Z0-9_]*");
        state169.addRegexLoop("[a-zA-Z0-9_]*");
        state170.addRegexLoop("[a-zA-Z0-9_]*");
        state171.addRegexLoop("[a-zA-Z0-9_]*");
        state172.addRegexLoop("[a-zA-Z0-9_]*");
        state173.addRegexLoop("[a-zA-Z0-9_]*");
        state174.addRegexLoop("[a-zA-Z0-9_]*");
        state175.addRegexLoop("[a-zA-Z0-9_]*");
        state176.addRegexLoop("[a-zA-Z0-9_]*");
        state177.addRegexLoop("[a-zA-Z0-9_]*");
        state178.addRegexLoop("[a-zA-Z0-9_]*");
        state179.addRegexLoop("[a-zA-Z0-9_]*");
        state180.addRegexLoop("[a-zA-Z0-9_]*");
        state181.addRegexLoop("[a-zA-Z0-9_]*");
        state182.addRegexLoop("[a-zA-Z0-9_]*");
        state183.addRegexLoop("[a-zA-Z0-9_]*");
        state184.addRegexLoop("[a-zA-Z0-9_]*");
        state185.addRegexLoop("[a-zA-Z0-9_]*");
        state186.addRegexLoop("[a-zA-Z0-9_]*");
        state187.addRegexLoop("[a-zA-Z0-9_]*");
        state188.addRegexLoop("[a-zA-Z0-9_]*");
        state189.addRegexLoop("[a-zA-Z0-9_]*");
        state190.addRegexLoop("[a-zA-Z0-9_]*");
        state191.addRegexLoop("[a-zA-Z0-9_]*");
        state192.addRegexLoop("[a-zA-Z0-9_]*");
        state193.addRegexLoop("[a-zA-Z0-9_]*");
        state194.addRegexLoop("[a-zA-Z0-9_]*");
        state195.addRegexLoop("[a-zA-Z0-9_]*");
        state196.addRegexLoop("[a-zA-Z0-9_]*");
        state197.addRegexLoop("[a-zA-Z0-9_]*");
        state198.addRegexLoop("[a-zA-Z0-9_]*");


        //On ajoute à chaque etats la liste des etats de state125 à state187 en utilisant addadjacents
        List<AutomatonState> statesList = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_a = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_b = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_c = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_d = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_e = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_f = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_g = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_h = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_i = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_j = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_k = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_l = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_m = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_n = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_o = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_p = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_q = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_r = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_s = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_t = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_u = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_v = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_w = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_x = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_y = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_z = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);

        List<AutomatonState> statesList_without_c_or_n = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_l_or_n = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state174, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_e_or_i = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state167, state168, state169, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_a_or_o_or_u = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state177, state178, state179, state180, state181, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_f_or_n_or_s = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state168, state169, state170, state171, state172, state173, state174, state176, state177, state178, state179, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_e_or_o_or_u = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state167, state168, state169, state170, state171, state172, state173, state174, state175, state177, state178, state179, state180, state181, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_r_or_u = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state169, state170, state171, state172, state173, state174, state175, state176, state177, state178, state180, state181, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_c_or_m_or_t_or_v = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state165, state166, state167, state168, state169, state170, state171, state172, state173, state175, state176, state177, state178, state179, state180, state182, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_h_or_r_or_y = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state170, state171, state172, state173, state174, state175, state176, state177, state178, state180, state181, state182, state183, state184, state185, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);
        List<AutomatonState> statesList_without_h_or_i = Arrays.asList(state135, state136, state137, state138, state139, state140, state141, state142, state143, state144, state145, state147, state148, state149, state150, state151, state152, state153, state154, state155, state156, state157, state158, state159, state160, state161, state162, state163, state164, state165, state166, state167, state168, state171, state172, state173, state174, state175, state176, state177, state178, state179, state180, state181, state182, state183, state184, state185, state186, state187, state188, state189, state190, state191, state192, state193, state194, state195, state196, state197, state198);

        //On ajoute à chaque etats la liste des etats de state135 à state187 en utilisant addadjacents
        //access
        state0.addAdjacents(statesList_without_c_or_n);
        state1.addAdjacents(statesList_without_c);
        state2.addAdjacents(statesList_without_e);
        state3.addAdjacents(statesList_without_s);
        state4.addAdjacents(statesList_without_s);
        state5.addAdjacents(statesList);

        //and
        state6.addAdjacents(statesList_without_d);
        state7.addAdjacents(statesList);

        //begin
        state8.addAdjacents(statesList_without_e);
        state9.addAdjacents(statesList_without_g);
        state10.addAdjacents(statesList_without_i);
        state11.addAdjacents(statesList_without_n);
        state12.addAdjacents(statesList);

        //else
        state13.addAdjacents(statesList_without_l_or_n);
        state14.addAdjacents(statesList_without_s);
        state15.addAdjacents(statesList_without_e_or_i);
        state16.addAdjacents(statesList);

        //elsif
        state20.addAdjacents(statesList_without_f);
        state21.addAdjacents(statesList);

        //end
        state22.addAdjacents(statesList_without_d);
        state23.addAdjacents(statesList);

        //false
        state24.addAdjacents(statesList_without_a_or_o_or_u);
        state25.addAdjacents(statesList_without_l);
        state26.addAdjacents(statesList_without_s);
        state27.addAdjacents(statesList_without_e);
        state28.addAdjacents(statesList);

        //for
        state29.addAdjacents(statesList_without_r);
        state30.addAdjacents(statesList);

        //function
        state31.addAdjacents(statesList_without_n);
        state32.addAdjacents(statesList_without_c);
        state33.addAdjacents(statesList_without_t);
        state34.addAdjacents(statesList_without_i);
        state35.addAdjacents(statesList_without_o);
        state36.addAdjacents(statesList_without_n);
        state37.addAdjacents(statesList);

        //if
        state38.addAdjacents(statesList_without_f_or_n_or_s);
        state39.addAdjacents(statesList);

        //in
        state40.addAdjacents(statesList);

        //is
        state41.addAdjacents(statesList);

        //loop
        state42.addAdjacents(statesList_without_o);
        state43.addAdjacents(statesList_without_o);
        state44.addAdjacents(statesList_without_p);
        state45.addAdjacents(statesList);

        //new
        state46.addAdjacents(statesList_without_e_or_o_or_u);
        state47.addAdjacents(statesList_without_w);
        state48.addAdjacents(statesList);

        //not
        state49.addAdjacents(statesList_without_t);
        state50.addAdjacents(statesList);

        //null
        state51.addAdjacents(statesList_without_l);
        state52.addAdjacents(statesList_without_l);
        state53.addAdjacents(statesList);

        //or
        state54.addAdjacents(statesList_without_r_or_u);
        state55.addAdjacents(statesList);

        //out
        state56.addAdjacents(statesList_without_t);
        state57.addAdjacents(statesList);

        //procedure
        state58.addAdjacents(statesList_without_r);
        state59.addAdjacents(statesList_without_o);
        state60.addAdjacents(statesList_without_c);
        state61.addAdjacents(statesList_without_e);
        state62.addAdjacents(statesList_without_d);
        state63.addAdjacents(statesList_without_u);
        state64.addAdjacents(statesList_without_r);
        state65.addAdjacents(statesList_without_e);
        state66.addAdjacents(statesList);

        //record
        state67.addAdjacents(statesList_without_e);
        state68.addAdjacents(statesList_without_c_or_m_or_t_or_v);
        state69.addAdjacents(statesList_without_o);
        state70.addAdjacents(statesList_without_r);
        state71.addAdjacents(statesList_without_d);
        state72.addAdjacents(statesList);

        //rem
        state74.addAdjacents(statesList);

        //return
        state76.addAdjacents(statesList_without_u);
        state77.addAdjacents(statesList_without_r);
        state78.addAdjacents(statesList_without_n);
        state79.addAdjacents(statesList);

        //reverse
        state81.addAdjacents(statesList_without_e);
        state82.addAdjacents(statesList_without_r);
        state83.addAdjacents(statesList_without_s);
        state84.addAdjacents(statesList_without_e);
        state85.addAdjacents(statesList);

        //then
        state86.addAdjacents(statesList_without_h_or_r_or_y);
        state87.addAdjacents(statesList_without_e);
        state88.addAdjacents(statesList_without_n);
        state89.addAdjacents(statesList);

        //true
        state90.addAdjacents(statesList_without_u);
        state91.addAdjacents(statesList_without_e);
        state92.addAdjacents(statesList);

        //type
        state93.addAdjacents(statesList_without_p);
        state94.addAdjacents(statesList_without_e);
        state95.addAdjacents(statesList);

        //use
        state96.addAdjacents(statesList_without_s);
        state97.addAdjacents(statesList_without_e);
        state98.addAdjacents(statesList);

        //while
        state99.addAdjacents(statesList_without_h_or_i);
        state100.addAdjacents(statesList_without_i);
        state101.addAdjacents(statesList_without_l);
        state102.addAdjacents(statesList_without_e);
        state103.addAdjacents(statesList);

        //with
        state104.addAdjacents(statesList_without_t);
        state105.addAdjacents(statesList_without_h);
        state106.addAdjacents(statesList);

        
        return new Automaton(init_state);
    }
}
