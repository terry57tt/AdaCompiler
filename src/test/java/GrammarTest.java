import java.io.IOException;
import java.util.ArrayList;

import org.pcl.FileHandler;
import org.pcl.Lexeur;
import org.pcl.Token;

import org.pcl.grammaire.Grammar;
import org.pcl.grammaire.Grammar_ast;
import org.pcl.ig.PClWindows;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.junit.jupiter.api.Test;


import org.pcl.structure.automaton.TokenType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

public class GrammarTest {
    @Test
    public void createGrammar() throws IOException {
        String file = "demo/SyntaxError/syntaxError.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);

        Lexeur lexeur = new Lexeur(automaton, stream, file);

        ArrayList<Token> tokens = lexeur.tokenize();

        Grammar grammar = new Grammar(tokens);
        SyntaxTree tree = grammar.getSyntaxTree();
        System.out.println("Printrds");
        System.out.println(tree);
    }


    public void reduceGrammar() {
        Token token = new Token("2");
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(token);
        Node root = new Node("Fichier");
        Node node1 = new Node("node1");
        Node node2 = new Node("node2");
        Node leaf = new Node(token);
        root.addChild(node1);
        node1.setParent(root);
        node1.addChild(node2);
        node2.setParent(node1);
        node2.addChild(leaf);
        leaf.setParent(node2);

        Grammar_ast grammar = new Grammar_ast(tokens);
        grammar.syntaxTree = new SyntaxTree(grammar.reduceNodesChildren(root));

        assert grammar.syntaxTree.getRootNode().getValue().equals(root.getValue());
        assert grammar.syntaxTree.getRootNode().getChildren().get(0) == node2;
        assert grammar.syntaxTree.getRootNode().getChildren().get(0).getChildren().get(0).equals(leaf);

    }

    public static void main(String[] args) {
        Node root = new Node("Fichier");
        Token tokenEgal = new Token("=");
        Node node1 = new Node(tokenEgal);
        root.addChild(node1);
        node1.setParent(root);

        Node child1 = new Node("NT1");
        Node child3 = new Node("NT2");
        Token tokenFois = new Token(TokenType.SEPARATOR, "*", 4);
        Node child2 = new Node(tokenFois);
        node1.addChild(child1);
        node1.addChild(child2);
        node1.addChild(child3);
        child1.setParent(node1);
        child2.setParent(node1);
        child3.setParent(node1);

        Token tokenX = new Token(TokenType.IDENTIFIER, "x", 4);
        Token token3 = new Token(TokenType.NUMBER,"3", 4);
        Node cc1 = new Node(tokenX);
        cc1.setParent(child1);
        child1.addChild(cc1);
        Node cc2 = new Node(token3);
        cc2.setParent(child3);
        child3.addChild(cc2);

        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(tokenEgal);
        tokens.add(tokenFois);
        tokens.add(tokenX);
        tokens.add(token3);

        Grammar_ast grammar = new Grammar_ast(tokens);
        grammar.syntaxTree = new SyntaxTree(root);
        grammar.createAST();
        new PClWindows(tokens, grammar.ast,!grammar.error).start();


    }


    }


