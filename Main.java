import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) throws IOException {
        String filename = args[0];
        Path myPath = Paths.get(filename);
        String input = new String(Files.readAllBytes(myPath));
        Lexer lexer = new Lexer(input);
        LinkedList<Token> tokens = lexer.Lex();
        Parser parser = new Parser(tokens);
        for (Token token : tokens) {
            System.out.print(token.toString());
        }
    }
}
