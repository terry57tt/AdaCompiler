import org.junit.jupiter.api.Test;
import org.pcl.structure.automaton.TokenType;
import org.pcl.Lexeur;
import org.pcl.Token;
import org.pcl.FileHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.pcl.structure.automaton.InvalidStateExeception;

public class LexeurTest {

    @Test
    public void testGetCharacters() throws IOException {
        String testFilePath = "keywords.ada";

        Stream<Character> characterStream = FileHandler.getCharacters(testFilePath);

        assertNotNull(characterStream);

        String result = characterStream.map(String::valueOf).collect(Collectors.joining());

        // check that the file is read correctly
        assertEquals(39, result.length());

        // check that the content is correct
        String expectedContent = "access and begin else elsif end\n    if false true\n";
        assertEquals(expectedContent, result);

        // check that spaces are not ignored  
        String space = characterStream.skip(6).findFirst().get().toString();
        assertEquals(" ", space);

        // check that new lines are not ignored
        String newLine = characterStream.skip(32).findFirst().get().toString();
        assertEquals("\n", newLine);

        // check that tabs are not ignored
        String tab = characterStream.skip(33).findFirst().get().toString();
        assertEquals("\t", tab);
    }

    @Test
    public void testGetCharactersWithNonExistentFile() {
        String nonExistentFilePath = "non_existent_file.ada";

        assertThrows(IOException.class, () -> FileHandler.getCharacters(nonExistentFilePath));
    }


    @Test
    public void testAdaProgram0() throws Exception {
        String pathAdaProgram = "data/keywords.ada";
        Lexeur lexer = new Lexeur(pathAdaProgram);
        
        ArrayList<Token> tokens = lexer.getTokens(pathAdaProgram);
        assert tokens.size() == 7;
        
        for (int i = 0; i < tokens.size(); i++) {
            assert tokens.get(i).getType() == TokenType.KEYWORD;
            assert tokens.get(i).getLineNumber() == 1;
        }
    }

    @Test
    public void testAdaProgram1() throws Exception {
        String pathAdaProgram = "data/program1.ada";
        Lexeur lexer = new Lexeur(pathAdaProgram);
        
        ArrayList<Token> tokens = lexer.getTokens(pathAdaProgram);
        assert tokens.size() == 26;
        
        assert tokens.get(0).getType() == TokenType.KEYWORD;
        assert tokens.get(0).getValue().equals("with");
        assert tokens.get(0).getLineNumber() == 1;

        assert tokens.get(1).getType() == TokenType.IDENTIFIER;
        assert tokens.get(1).getValue().equals("Ada");
        assert tokens.get(1).getLineNumber() == 1;

        assert tokens.get(2).getType() == TokenType.OPERATOR;
        assert tokens.get(2).getValue().equals(".");
        assert tokens.get(2).getLineNumber() == 1;

        assert tokens.get(6).getType() == TokenType.IDENTIFIER;
        assert tokens.get(6).getValue().equals("Hello_World");
        assert tokens.get(6).getLineNumber() == 2;

        assert tokens.get(14).getType() == TokenType.OPERATOR;
        assert tokens.get(14).getValue().equals("(");
        assert tokens.get(14).getLineNumber() == 4;
    }

    // TODO : check error message and line number
    @Test
    public void testAdaProgram2() throws Exception {
        String pathAdaProgram = "data/invalid_character.ada";
        Lexeur lexer = new Lexeur(pathAdaProgram);

        try {
            lexer.getTokens(pathAdaProgram);
            assert false;
        } catch (InvalidStateExeception e) {
            assert true;
        }
    }
}
