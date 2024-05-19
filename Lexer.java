import java.io.IOException;
import java.util.LinkedList;
import java.util.HashMap;
public class Lexer {
    private StringHandler stringhandler;
    private int LineNumber=1;
    private int CharacterPosition=0;
    private LinkedList<Token> tokens = new LinkedList<Token>();
    private HashMap<String, TokenType> keywordHash = new HashMap<String, TokenType>();
    private HashMap<String, TokenType> twoCharacterHash = new HashMap<String, TokenType>();
    private HashMap<String, TokenType> oneCharacterHash = new HashMap<String, TokenType>();
    /**
     * Constructor for the Lexer class that initializes the StringHandler
     * @param inputs
     * @throws IOException
     */
    public Lexer(String inputs) throws IOException {
        stringhandler = new StringHandler(inputs);
        KeyWordHashmap();
        TwoCharacterHashmap();
        OneCharacterHashmap();
    }
    public void KeyWordHashmap(){
        keywordHash.put("return", TokenType.RETURN);
        keywordHash.put("math", TokenType.MATH);
        keywordHash.put("add", TokenType.ADD);
        keywordHash.put("subtract", TokenType.SUBTRACT);
        keywordHash.put("multiply", TokenType.MULTIPLY);
        keywordHash.put("and", TokenType.AND);
        keywordHash.put("or", TokenType.OR);
        keywordHash.put("not", TokenType.NOT);
        keywordHash.put("xor", TokenType.XOR);
        keywordHash.put("copy", TokenType.COPY);
        keywordHash.put("halt", TokenType.HALT);
        keywordHash.put("branch", TokenType.BRANCH);
        keywordHash.put("jump", TokenType.JUMP);
        keywordHash.put("call", TokenType.CALL);
        keywordHash.put("push", TokenType.PUSH);
        keywordHash.put("load", TokenType.LOAD);
        keywordHash.put("store", TokenType.STORE);
        keywordHash.put("peek", TokenType.PEEK);
        keywordHash.put("pop", TokenType.POP);
        keywordHash.put("interrupt", TokenType.INTERRUPT);
        keywordHash.put("shift", TokenType.SHIFT);
        keywordHash.put("left", TokenType.LEFT);
        keywordHash.put("right", TokenType.RIGHT);
        keywordHash.put("R", TokenType.REGISTER);
        keywordHash.put("\n", TokenType.NEWLINE);
        keywordHash.put("greaterOrEqual", TokenType.GREATEROREQUAL);
        keywordHash.put("lessOrEqual", TokenType.LESSOREQUAL);
        keywordHash.put("unequal", TokenType.UNEQUAL);
        keywordHash.put("equal",TokenType.EQUAL);
        keywordHash.put("greater",TokenType.GREATER);
        keywordHash.put("less",TokenType.LESS);
    }
    public void TwoCharacterHashmap(){
    }
    public void OneCharacterHashmap(){

    }
    /**
     * Collects the inputs as the Peek() method from StringHandler goes through each letter. It checks for spaces, tabs, empty
     * lines(\n), carriage returns(\r), letters and finally, digits. If there is a character that is not one of those things or
     * if thereis a second decimal point where there shouldn't be, it will throw an IllegalArgumentException and break.
     * @return
     */
    public LinkedList<Token> Lex(){
        while(!stringhandler.IsDone()) {
            char currentCharacter = stringhandler.Peek(0);
            if (currentCharacter == ' ' || currentCharacter == '\t') {
                stringhandler.GetChar();
                CharacterPosition++;
            } else if (currentCharacter == '\n' || currentCharacter == ';') {
                stringhandler.GetChar();
                LineNumber++;
                //CharacterPosition=0;
                //CharacterPosition=1;
                CharacterPosition++;
                tokens.add(new Token(TokenType.NEWLINE, LineNumber, CharacterPosition));
            } else if (currentCharacter == '\r') {
                stringhandler.GetChar();
                //CharacterPosition=0;
                CharacterPosition++;
            } else if (Character.isLetter(currentCharacter)) {
            Token wordProcessor = ProcessWord();
            tokens.add(wordProcessor);
            } else if (Character.isDigit(currentCharacter)) {
                Token numberProcessor = ProcessNumber();
                tokens.add(numberProcessor);
            } else if (currentCharacter == '#') {
                while (stringhandler.Peek(0) != '\n') {
                    stringhandler.GetChar();
                    CharacterPosition++;
                }
            } else {
                Token OneTwoSymbols = ProcessSymbols();
                if (OneTwoSymbols != null) {
                    tokens.add(OneTwoSymbols);
                } else {
                    throw new IllegalArgumentException("UNRECOGNIZED CHARACTER: " + currentCharacter);
                }
            }
        }
        tokens.add(new Token(TokenType.NEWLINE, LineNumber, CharacterPosition));
        return tokens;
    }
    /**
     * When a NUMBER is found in the above method, this method is called through that if statement where it checks for digits
     * and the possible use of a decimal. If a decimal is found, it sets the booolean value to true and lets it through the while
     * loop while increasing the position of the line by one. When the process is done, we calculate the length of the substring
     * and are able to get it to print properly by having the getSubstring helper method created in the StringHandler class
     * @return
     */
    private Token ProcessNumber() {
        int startPosition = CharacterPosition;
        boolean decimalFound = false;
        while(Character.isDigit(stringhandler.Peek(0)) || (!decimalFound && stringhandler.Peek(0) == '.')) {
            char currentCharacter  = stringhandler.GetChar();
            CharacterPosition++;
            if(currentCharacter == '.') {
                decimalFound = true;
            }
        }
        int length = CharacterPosition - startPosition;
        String value = stringhandler.getSubstring(startPosition, length);
        //String value = stringhandler.PeekString(CharacterPosition);
        return new Token(TokenType.NUMBER, value, LineNumber, startPosition);
    }
    /**
     * Similar to the previous method minus the inclusion of the decimalfound boolean. Also, when this method is called, we allow
     * for the use of digits when it is not the first character in the String. If it is found in the middle of the wrd, it
     * should still end up working and outputing the Token as a WORD
     * @return
     */
    private Token ProcessWord() {
        int startPosition = CharacterPosition;
        TokenType tokenType = null;
        while (Character.isLetterOrDigit(stringhandler.Peek(0)) || stringhandler.Peek(0) == '_') {
            stringhandler.GetChar();
            CharacterPosition++;
        }
        int length = CharacterPosition - startPosition;
        String value = stringhandler.getSubstring(startPosition, length);
        if (keywordHash.containsKey(value)) {
            tokenType = keywordHash.get(value);
            return new Token(tokenType, LineNumber, startPosition);
        }
        if (value.startsWith("R") && value.length() > 1 && Character.isDigit(value.charAt(1))) {
            String registerNumber = value.substring(1);
            return new Token(TokenType.REGISTER, registerNumber, LineNumber, startPosition + 1);
        }
        return null;
    }

    /**
     * The first if statement works by looking at the next two string elements found through PeekString. By looking through the two symbol
     * hashmap, we can then determine if there is a token that already exists for it and we can return that value as that set token. As
     * always, the CharacterPosition is added to by the number of characters we looked at. This is a similar process to the one
     * character if statement except we are only using PeekString for the singular symbol.
     * @return new Token depending on one or two character symbol, null if not an allowed character
     */
    public Token ProcessSymbols(){
        String twoCharacterSymbols = stringhandler.PeekString(2);
        String oneCharacterSymbols = stringhandler.PeekString(1);
        if(twoCharacterHash.containsKey(twoCharacterSymbols)){
            stringhandler.GetChar();
            stringhandler.GetChar();
            CharacterPosition+=2;
            return new Token(twoCharacterHash.get(twoCharacterSymbols), LineNumber, CharacterPosition);
        }
        if(oneCharacterHash.containsKey(oneCharacterSymbols)){
            stringhandler.GetChar();
            CharacterPosition++;
            return new Token(oneCharacterHash.get(oneCharacterSymbols), LineNumber, CharacterPosition);
        }
        return null;
    }
}
