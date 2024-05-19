import java.io.IOException;
public class StringHandler {
    private Integer index = 0;
    private String AssemblerFile;
    public StringHandler(String input) throws IOException {
        AssemblerFile = input;
    }
    /**
     * First determine the location of the index by adding the passed value to the starting index of 0. Then we check if it is a valid index value
     * by checking if it is not negative and is less than the total length of the entire file. Then we just return character at that specific index
     * if it passed and return an empty char if it doesn't exist.
     * @param i
     * @return
     */
    public char Peek(int i) {
        int peekIndex = index+i;
        if(peekIndex>=0 && peekIndex<AssemblerFile.length()) {
            return AssemblerFile.charAt(peekIndex);
        }
        return '\0';
    }
    /**
     * Like the previous method, we determine the location of the index and then check if it is a valid index. If it is, we return a substring that
     * starts from the starting index up until the ending index of peekIndex. Instead of returning an empty char though, we return an empty String
     * value.
     * @param i
     * @return
     */
    public String PeekString(int i) {
        int peekIndex = index+i;
        if(peekIndex>=0 && peekIndex<=AssemblerFile.length()) {
            return AssemblerFile.substring(index, peekIndex);
        }
        return "";
    }
    /**
     * Returns the next character in the file after increasing the index by one.
     * @return
     */
    public char GetChar() {
        char current = AssemblerFile.charAt(index);
        index++;
        return current;
    }
    /**
     * Increases the index by the passed value i
     * @param i
     */
    public void Swallow(int i) {
        index+=i;
    }
    /**
     * Checks if the index has reached the end of the file.
     * @return
     */
    public boolean IsDone() {
        if(index>=AssemblerFile.length()) {
            return true;
        }else
            return false;
    }
    /**
     * Returns a substring of the file that starts at the current index up until the end of the file.
     * @return
     */
    public String Remainder() {
        return AssemblerFile.substring(index);
    }
    /**
     * I created this helper method to be able to print out the proper String values passed by the Lexer methods ProcessWord and
     * ProcessNumber. When those two methods are used in the Lexer class, this method helps print out the String by collecting
     * the starting position of the String up until the end point of the String.
     * @param startPosition
     * @param length
     * @return substring OR ""(nothing)
     */
    public String getSubstring(int startPosition, int length) {
        String substring = AssemblerFile.substring(startPosition, startPosition+length);
        return substring;
    }
}
