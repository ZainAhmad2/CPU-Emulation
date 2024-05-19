import java.util.LinkedList;
import java.util.Optional;

public class TokenHandler {
    private static LinkedList<Token> tokens = new LinkedList<>();;
    private int index;
    public TokenHandler(LinkedList<Token> tokens){
        this.tokens = tokens;
        this.index = 0;
    }

    /**
     * Similar to the method we used in the StringHandler, this method looks ahead a given number of tokens, j. If there is a token found
     * at that particular index, it returns an instance of the Optional class with the specified value found at j. If it is not found by the
     * program, it returns an empty instance of the optional class.
     * @param j
     * @return Optional.of(tokens.get(j)), Optional.empty()
     */
    public Optional<Token> Peek(int j){
        if(j>=0 && j<tokens.size()){
            return Optional.of(tokens.get(j));
        }
        return Optional.empty();
    }

    /**
     * This method just checks if there are still more tokens in the token LinkedList, if there is it returns true and if there isn't it
     * returns false.
     * @return true, false
     */
    public boolean MoreTokens(){
        if (tokens.size()>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * This method takes in a parameter t which is a specific tokenType created by the Lexer in the previous assignment. Since the program
     * goes in order, we can directly check if the particular token passed in matches the first token in the LinkedList. If it does end up
     * matching, we create another instance of the Optional class except where we end up removing the first value of the list. If the values
     * don't end up matching we return an empty instance of the Optional class like we did in the first method.
     * @param t
     * @return Optional.of(tokens.removeFirst()), Optional.empty()
     */
    public Optional<Token> MatchAndRemove(TokenType t){
        if(tokens.size()>0 && tokens.getFirst().getType() == t){
            return Optional.of(tokens.removeFirst());
        }
        return Optional.empty();
    }
}
