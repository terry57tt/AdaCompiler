import org.junit.jupiter.api.Test;
import org.pcl.FileHandler;
import org.pcl.Lexeur;
import org.pcl.Token;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.automaton.TokenType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class LexeurTest {

    @Test
    public void testGetCharacters() throws IOException {
        String testFilePath = "data/keywords.ada";

        Stream<Character> characterStream = FileHandler.getCharacters(testFilePath);
        assertNotNull(characterStream);
        
        String result = characterStream.map(String::valueOf).collect(Collectors.joining());
        // check that the content is correct
        String expectedContent = "access and begin else elsif end if false true\n";

        // check that the file is read correctly
        assertEquals(expectedContent.length(), result.length(), "expected " + expectedContent.length() + " got " + result.length());

        assertEquals(expectedContent, result, "expected " + expectedContent + " got " + result);

        // check that spaces are not ignored
        assertEquals(' ', result.charAt(6), "expected ' ' got " + result.charAt(6));

        // check that new lines are not ignored
        assertEquals('\n', result.charAt(45), "expected '\\n' got " + result.charAt(45));
    }

    @Test
    public void testGetCharactersWithNonExistentFile() {
        String nonExistentFilePath = "non_existent_file.ada";

        assertThrows(IOException.class, () -> FileHandler.getCharacters(nonExistentFilePath));
    }

    @Test
    public void testAdaProgram0() throws Exception {
        String file = "data/keywords.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);

        Lexeur lexeur = new Lexeur(automaton, stream, file);
        
        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 9;

        for (Token token : tokens) {
            assert token.getType() == TokenType.KEYWORD: "expected TokenType.KEYWORD got " + token.getType() + " instead for " + token.getValue();
            assert token.getLineNumber() == 1: "expected line number 1 got " + token.getLineNumber() + " instead for " + token.getValue();
        }
    }

    @Test
    public void testAdaProgram1() throws Exception {
        String file = "data/program1.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);

        Lexeur lexeur = new Lexeur(automaton, stream, file);
        
        ArrayList<Token> tokens = lexeur.tokenize();
        
        assert tokens.size() == 25: "expected 25 tokens got " + tokens.size();
        
        assert tokens.get(0).getType() == TokenType.KEYWORD: "expected TokenType.KEYWORD got " + tokens.get(0).getType() + " instead for " + tokens.get(0).getValue();
        assert tokens.get(0).getValue().equals("with"): "expected 'with' got " + tokens.get(0).getValue();
        assert tokens.get(0).getLineNumber() == 1: "expected line number 1 got " + tokens.get(0).getLineNumber() + " instead for " + tokens.get(0).getValue();

        assert tokens.get(1).getType() == TokenType.IDENTIFIER: "expected TokenType.IDENTIFIER got " + tokens.get(1).getType() + " instead for " + tokens.get(1).getValue();
        assert tokens.get(1).getValue().equals("Ada"): "expected 'Ada' got " + tokens.get(1).getValue();
        assert tokens.get(1).getLineNumber() == 1: "expected line number 1 got " + tokens.get(1).getLineNumber() + " instead for " + tokens.get(1).getValue();

        assert tokens.get(2).getType() == TokenType.SEPARATOR: "expected TokenType.SEPARATOR got " + tokens.get(2).getType() + " instead for " + tokens.get(2).getValue();
        assert tokens.get(2).getValue().equals("."): "expected '.' got " + tokens.get(2).getValue();
        assert tokens.get(2).getLineNumber() == 1: "expected line number 1 got " + tokens.get(2).getLineNumber() + " instead for " + tokens.get(2).getValue();

        assert tokens.get(6).getType() == TokenType.IDENTIFIER: "expected TokenType.IDENTIFIER got " + tokens.get(6).getType() + " instead for " + tokens.get(6).getValue();
        assert tokens.get(6).getValue().equals("Hello_World"): "expected 'Hello_World' got " + tokens.get(6).getValue();
        assert tokens.get(6).getLineNumber() == 3: "expected line number 3 got " + tokens.get(6).getLineNumber() + " instead for " + tokens.get(6).getValue();

        assert tokens.get(14).getType() == TokenType.SEPARATOR: "expected TokenType.SEPARATOR got " + tokens.get(14).getType() + " instead for " + tokens.get(14).getValue();
        assert tokens.get(14).getValue().equals("("): "expected '(' got " + tokens.get(14).getValue();
        assert tokens.get(14).getLineNumber() == 5: "expected line number 5 got " + tokens.get(14).getLineNumber() + " instead for " + tokens.get(14).getValue();
    }

    @Test
    public void testInvalidCharacter() throws Exception {
        String file = "data/invalid_character.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);
        ArrayList<Token> tokens = lexeur.tokenize();

        for (Token token: tokens) {
            assert !token.getValue().equals("#"): "expected not '#' got " + token.getValue();
            assert !token.getValue().equals("!"): "expected not '!' got " + token.getValue();
        }
    }

    @Test
    public void testString() throws FileNotFoundException {
        String file = "data/strings.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);

        ArrayList<Token> tokens = lexeur.tokenize();
        assert tokens.size() == 4: "expected 4 tokens got " + tokens.size();
        assert tokens.get(0).getValue().equals("aaa"): "expected 'aaa' got " + tokens.get(0).getValue();
        assert tokens.get(1).getValue().equals("b"): "expected 'b' got " + tokens.get(1).getValue();
        assert tokens.get(2).getValue().equals("aa\"aa"): "expected 'aa\"aa' got " + tokens.get(2).getValue();
        assert tokens.get(3).getValue().equals("'"): "expected ''' got " + tokens.get(3).getValue();
    }



    @Test
    public void testSpecificCharacters() throws Exception {
        String file = "data/specific_characters.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);

        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 34;

        assert tokens.get(1).getValue().equals(":="): "expected ':=' got " + tokens.get(1).getValue();
        assert tokens.get(10).getValue().equals("<="): "expected '<=' got " + tokens.get(10).getValue();
        assert tokens.get(15).getValue().equals(">="): "expected '>=' got " + tokens.get(15).getValue();
        assert tokens.get(20).getValue().equals("/="): "expected '/=' got " + tokens.get(20).getValue();
    }

    @Test
    public void testRemoveComments() throws Exception {
        String file = "data/remove_comments.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);

        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.size() == 8;

        assert tokens.get(0).getValue().equals("access"): "expected 'access' got " + tokens.get(0).getValue();
        assert tokens.get(0).getLineNumber() == 2: "expected line number 2 got " + tokens.get(0).getLineNumber();

        assert tokens.get(1).getValue().equals("program"): "expected 'program' got " + tokens.get(1).getValue();
        assert tokens.get(1).getLineNumber() == 2: "expected line number 2 got " + tokens.get(1).getLineNumber();

        assert tokens.get(2).getValue().equals("with"): "expected 'with' got " + tokens.get(2).getValue();
        assert tokens.get(2).getLineNumber() == 2: "expected line number 2 got " + tokens.get(2).getLineNumber();

        assert tokens.get(3).getValue().equals("hello_Word"): "expected 'hello_Word' got " + tokens.get(3).getValue();
        assert tokens.get(3).getLineNumber() == 2: "expected line number 2 got " + tokens.get(3).getLineNumber();

        assert tokens.get(4).getValue().equals(";"): "expected ';' got " + tokens.get(4).getValue();
        assert tokens.get(4).getLineNumber() == 2: "expected line number 2 got " + tokens.get(4).getLineNumber();

        assert tokens.get(5).getValue().equals("procedure"): "expected 'procedure' got " + tokens.get(5).getValue();
        assert tokens.get(5).getLineNumber() == 4: "expected line number 4 got " + tokens.get(5).getLineNumber();

        assert tokens.get(6).getValue().equals("hello_Word"): "expected 'hello_Word' got " + tokens.get(6).getValue();
        assert tokens.get(6).getLineNumber() == 4: "expected line number 4 got " + tokens.get(6).getLineNumber();

        assert tokens.get(7).getValue().equals("is"): "expected 'is' got " + tokens.get(7).getValue();
        assert tokens.get(7).getLineNumber() == 4: "expected line number 4 got " + tokens.get(7).getLineNumber();
    }

    @Test
    public void testProgram4Parsed() throws FileNotFoundException {
        String file = "data/program4.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);
        Lexeur lexeur = new Lexeur(automaton, stream, file);

        ArrayList<Token> tokens = lexeur.tokenize();

        assert tokens.get(0).getValue().equals("with"): "expected 'with' got " + tokens.get(0).getValue();
        assert tokens.get(1).getValue().equals("Ada"): "expected 'Ada' got " + tokens.get(1).getValue();
        assert tokens.get(2).getValue().equals("."): "expected '.' got " + tokens.get(2).getValue();
        assert tokens.get(3).getValue().equals("Text_IO"): "expected 'Text_IO' got " + tokens.get(3).getValue();
        assert tokens.get(4).getValue().equals(";"): "expected ';' got " + tokens.get(4).getValue();
        assert tokens.get(5).getValue().equals("use"): "expected 'use' got " + tokens.get(5).getValue();
        assert tokens.get(6).getValue().equals("Ada"): "expected 'Ada' got " + tokens.get(6).getValue();
        assert tokens.get(7).getValue().equals("."): "expected '.' got " + tokens.get(7).getValue();
        assert tokens.get(8).getValue().equals("Text_IO"): "expected 'Text_IO' got " + tokens.get(8).getValue();
        assert tokens.get(9).getValue().equals(";"): "expected ';' got " + tokens.get(9).getValue();
        assert tokens.get(10).getValue().equals("procedure"): "expected ';' got " + tokens.get(10).getValue();
        assert tokens.get(11).getValue().equals("unDebut"): "expected 'unDebut' got " + tokens.get(11).getValue();
        assert tokens.get(12).getValue().equals("is"): "expected 'is' got " + tokens.get(12).getValue();
        assert tokens.get(13).getValue().equals("function"): "expected 'function' got " + tokens.get(13).getValue();
        assert tokens.get(14).getValue().equals("aireRectangle"): "expected 'aireRectangle' got " + tokens.get(14).getValue();
        assert tokens.get(15).getValue().equals("("): "expected '(' got " + tokens.get(15).getValue();
        assert tokens.get(16).getValue().equals("larg"): "expected 'larg' got " + tokens.get(16).getValue();
        assert tokens.get(17).getValue().equals(":"): "expected ':' got " + tokens.get(17).getValue();
        assert tokens.get(18).getValue().equals("integer"): "expected 'integer' got " + tokens.get(18).getValue();
        assert tokens.get(19).getValue().equals(";"): "expected ';' got " + tokens.get(19).getValue();
        assert tokens.get(20).getValue().equals("long"): "expected 'long' got " + tokens.get(20).getValue();
        assert tokens.get(21).getValue().equals(":"): "expected ':' got " + tokens.get(21).getValue();
        assert tokens.get(22).getValue().equals("integer"): "expected 'integer' got " + tokens.get(22).getValue();
        assert tokens.get(23).getValue().equals(")"): "expected ')' got " + tokens.get(23).getValue();
        assert tokens.get(24).getValue().equals("return"): "expected 'return' got " + tokens.get(24).getValue();
        assert tokens.get(25).getValue().equals("integer"): "expected 'integer' got " + tokens.get(25).getValue();
        assert tokens.get(26).getValue().equals("is"): "expected 'is' got " + tokens.get(26).getValue();
        assert tokens.get(27).getValue().equals("aire"): "expected 'aire' got " + tokens.get(27).getValue();
        assert tokens.get(28).getValue().equals(":"): "expected ':' got " + tokens.get(28).getValue();
        assert tokens.get(29).getValue().equals("integer"): "expected 'integer' got " + tokens.get(29).getValue();
        assert tokens.get(30).getValue().equals(";"): "expected ';' got " + tokens.get(30).getValue();
        assert tokens.get(31).getValue().equals("begin"): "expected 'begin' got " + tokens.get(31).getValue();
        assert tokens.get(32).getValue().equals("aire"): "expected 'aire' got " + tokens.get(32).getValue();
        assert tokens.get(33).getValue().equals(":="): "expected ':=' got " + tokens.get(33).getValue();
        assert tokens.get(34).getValue().equals("larg"): "expected 'larg' got " + tokens.get(34).getValue();
        assert tokens.get(35).getValue().equals("*"): "expected '*' got " + tokens.get(35).getValue();
        assert tokens.get(36).getValue().equals("long"): "expected 'long' got " + tokens.get(36).getValue();
        assert tokens.get(37).getValue().equals(";"): "expected ';' got " + tokens.get(37).getValue();
        assert tokens.get(38).getValue().equals("return"): "expected 'return' got " + tokens.get(38).getValue();
        assert tokens.get(39).getValue().equals("aire"): "expected 'aire' got " + tokens.get(39).getValue();
        assert tokens.get(40).getValue().equals("end"): "expected 'end' got " + tokens.get(40).getValue();
        assert tokens.get(41).getValue().equals("aireRectangle"): "expected 'aireRectangle' got " + tokens.get(41).getValue();
        assert tokens.get(42).getValue().equals(";"): "expected ';' got " + tokens.get(42).getValue();
        assert tokens.get(43).getValue().equals("function"): "expected 'function' got " + tokens.get(43).getValue();
        assert tokens.get(44).getValue().equals("perimetreRectangle"): "expected 'perimetreRectangle' got " + tokens.get(44).getValue();
        assert tokens.get(45).getValue().equals("("): "expected '(' got " + tokens.get(45).getValue();
        assert tokens.get(46).getValue().equals("larg"): "expected 'larg' got " + tokens.get(46).getValue();
        assert tokens.get(47).getValue().equals(":"): "expected ':' got " + tokens.get(47).getValue();
        assert tokens.get(48).getValue().equals("integer"): "expected 'integer' got " + tokens.get(48).getValue();
        assert tokens.get(49).getValue().equals(";"): "expected ';' got " + tokens.get(49).getValue();
        assert tokens.get(50).getValue().equals("long"): "expected 'long' got " + tokens.get(50).getValue();
        assert tokens.get(51).getValue().equals(":"): "expected ':' got " + tokens.get(51).getValue();
        assert tokens.get(52).getValue().equals("integer"): "expected 'integer' got " + tokens.get(52).getValue();
        assert tokens.get(53).getValue().equals(")"): "expected ')' got " + tokens.get(53).getValue();
        assert tokens.get(54).getValue().equals("return"): "expected 'return' got " + tokens.get(54).getValue();
        assert tokens.get(55).getValue().equals("integer"): "expected 'integer' got " + tokens.get(55).getValue();
        assert tokens.get(56).getValue().equals("is"): "expected 'is' got " + tokens.get(56).getValue();

    }
}
