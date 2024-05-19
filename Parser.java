import java.util.LinkedList;
import java.util.Optional;

public class Parser {
    private final TokenHandler tokenHandler;
    private String binaryRepresentation = "";

    private LinkedList<Token> tokens = new LinkedList<Token>();

    public Parser(LinkedList<Token> tokens) {
        this.tokens = tokens;
        tokenHandler = new TokenHandler(tokens);
    }

    /**
     * This method just looks for whenever there is a new line in the program and deletes the space so that it doesn't cause any sort of problems later.
     * @return
     */
    boolean AcceptSeperators() {
        while (tokenHandler.MoreTokens()) {
            Optional<Token> currentToken = tokenHandler.Peek(0);
            if (currentToken.get().getType() == TokenType.NEWLINE) {
                tokenHandler.MatchAndRemove(TokenType.NEWLINE);
            } else {
                break;
            }
        }
        return true;
    }
    /**
     * This method just checks that there is no more lines or tokens to take in from the input file.
     * @return
     */
    public String ParseFile() {
        return ParseStatement();
    }

    /**
     * Parses each statement in the file, it first looks for the particular token needed, once the token has been found, we match
     * and remove that token based on that. It then calls the neccessary methods to be able to properly parse through the tokens
     * If a newline token was discovered, we accept and delete the seperators, if no more tokens have been found we return the binary
     * representation.
     * @return
     */
    public String ParseStatement() {
        while (tokenHandler.MoreTokens()) {
            Optional<Token> currentToken = tokenHandler.Peek(0);
            TokenType tokenType = currentToken.get().getType();
            switch (tokenType) {
                case MATH:
                    tokenHandler.MatchAndRemove(TokenType.MATH);
                    return ParseMath();
                case BRANCH:
                    tokenHandler.MatchAndRemove(TokenType.BRANCH);
                    return ParseBranch();
                case HALT:
                    tokenHandler.MatchAndRemove(TokenType.HALT);
                    return ParseHalt();
                case COPY:
                    tokenHandler.MatchAndRemove(TokenType.COPY);
                    return ParseDestOnly();
                case JUMP:
                    tokenHandler.MatchAndRemove(TokenType.JUMP);
                    return ParseJump();
                case CALL:
                    tokenHandler.MatchAndRemove(TokenType.CALL);
                    return ParseCall();
                case PUSH:
                    tokenHandler.MatchAndRemove(TokenType.PUSH);
                    return ParsePush();
                case POP:
                    tokenHandler.MatchAndRemove(TokenType.POP);
                    return ParsePop();
                case LOAD:
                    tokenHandler.MatchAndRemove(TokenType.LOAD);
                    return ParseLoad();
                case STORE:
                    tokenHandler.MatchAndRemove(TokenType.STORE);
                    return ParseStore();
                case RETURN:
                    tokenHandler.MatchAndRemove(TokenType.RETURN);
                    return ParseReturn();
                case PEEK:
                    tokenHandler.MatchAndRemove(TokenType.PEEK);
                    return ParsePeek();
                case INTERRUPT:
                    tokenHandler.MatchAndRemove(TokenType.INTERRUPT);
                    return ParseInterrupt();
            }
            if (currentToken.get().getType() == TokenType.NEWLINE) {
                AcceptSeperators();
            }
        }
        return binaryRepresentation;
    }

    /**
     * After checking if the current token is a JUMP token, we match and remove that particular token, since JUMP is only shown
     * once in the chart at the DESTONLY section of the chart, we can then immediately call that parse and then add on the
     * remaining bits for that section.
     * @return
     */
    private String ParseJump() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.JUMP) {
            binaryRepresentation += "001";
            tokenHandler.MatchAndRemove(TokenType.JUMP);
            if (currentToken.get().getType() == TokenType.REGISTER) {
                ParseDestOnly();
                return binaryRepresentation;
            } else {
                binaryRepresentation+="00";
                binaryRepresentation+= "000000000000000000000000000";
            return binaryRepresentation;
            }
        }
        return binaryRepresentation;
    }

    /**
     * The ParseBranch method works by first checking for the branch token above, then by checking for each case of the BOP section
     * of the chart, using an if-else-if statement we can then check for what type. Once the type has been found we correctly add
     * the bits for the operation and then calculate the register value and add it to the total number of bits. This gives us what
     * we need for the branch operation.
     * @return
     */
    private String ParseBranch() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        Optional<Token> nextToken = tokenHandler.Peek(1);
        binaryRepresentation += "001";
        if (currentToken.isPresent()) {
            if (currentToken.get().getType() == TokenType.EQUAL) {
                binaryRepresentation += "0000";
                tokenHandler.MatchAndRemove(TokenType.EQUAL);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.UNEQUAL) {
                binaryRepresentation += "0001";
                tokenHandler.MatchAndRemove(TokenType.UNEQUAL);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            }else if(currentToken.get().getType() == TokenType.LESS){
                binaryRepresentation += "0010";
                tokenHandler.MatchAndRemove(TokenType.LESS);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            }else if(currentToken.get().getType() == TokenType.GREATEROREQUAL){
                binaryRepresentation += "0011";
                tokenHandler.MatchAndRemove(TokenType.GREATEROREQUAL);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            }else if(currentToken.get().getType() == TokenType.GREATER){
                binaryRepresentation += "0100";
                tokenHandler.MatchAndRemove(TokenType.GREATER);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            }else if(currentToken.get().getType() == TokenType.LESSOREQUAL){
                binaryRepresentation += "0101";
                tokenHandler.MatchAndRemove(TokenType.LESSOREQUAL);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            }else{
                return binaryRepresentation;
            }
        }
        return binaryRepresentation;
    }

    /**
     * Since the peek operation is in multiple parts of the chart, we add the appropriate binary amount to the full sequence. Once this
     * part has been found we check for the register value, based on the number of registers, we add more bits each time and
     * understand the total needed value from that.
     * @return binaryRepresentation
     */
    private String ParsePeek() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        Optional<Token> nextToken = tokenHandler.Peek(1);
        if (currentToken.get().getType() == TokenType.PEEK) {
            binaryRepresentation += "110";
            tokenHandler.MatchAndRemove(TokenType.PEEK);
            if (currentToken.get().getType() == TokenType.REGISTER) {
                tokenHandler.MatchAndRemove(TokenType.REGISTER);
                binaryRepresentation += "01";
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    ParseTwoR();
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        return binaryRepresentation;
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseThreeR();
                        return binaryRepresentation;
                    } else {
                        return binaryRepresentation;
                    }
                }
            }
        }
        return binaryRepresentation;
    }

    /**
     * This just adds the needed bits that return has, giving us the bits associated with it.
     * @return
     */
    private String ParseReturn() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.RETURN) {
            binaryRepresentation += "10000";
            tokenHandler.MatchAndRemove(TokenType.RETURN);
        }
        return binaryRepresentation;
    }

    /**
     * Similar to the return method, we just added the bits associated with push
     * @return
     */
    private String ParsePush() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.PUSH) {
            binaryRepresentation += "011";
            tokenHandler.MatchAndRemove(TokenType.PUSH);
        }
        return binaryRepresentation;
    }
    /**
     * Similar to the push and return method, we just added the bits associated with pop
     * @return
     */
    private String ParsePop() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.POP) {
            binaryRepresentation += "110";
            tokenHandler.MatchAndRemove(TokenType.POP);
        }
        return binaryRepresentation;
    }
    /**
     * Similar to the previous methods, we just added the bits associated with load
     * @return
     */
    private String ParseLoad() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.LOAD) {
            binaryRepresentation += "100";
            tokenHandler.MatchAndRemove(TokenType.LOAD);
        }
        return binaryRepresentation;
    }
    /**
     * Similar to the previous methods, we just added the bits associated with store
     * @return
     */
    private String ParseStore() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.STORE) {
            binaryRepresentation += "101";
            tokenHandler.MatchAndRemove(TokenType.STORE);
        }
        return binaryRepresentation;
    }
    /**
     * Similar to the previous methods, we just added the bits associated with interrupt
     * @return
     */
    private String ParseInterrupt() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.HALT) {
            binaryRepresentation += "11000";
            tokenHandler.MatchAndRemove(TokenType.HALT);
        }
        return binaryRepresentation;
    }
    /**
     * Similar to the previous methods, we just added the bits associated with call
     * @return
     */
    private String ParseCall() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.CALL) {
            binaryRepresentation += "010";
            tokenHandler.MatchAndRemove(TokenType.CALL);
        }
        return binaryRepresentation;
    }
    /**
     * Similar to the previous methods, we just added the bits associated with halt
     * @return
     */
    public String ParseHalt() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.HALT) {
            binaryRepresentation += "00000";
            tokenHandler.MatchAndRemove(TokenType.HALT);
        }
        return binaryRepresentation;
    }
    /**
     * Similar to the branch method, except we look for and add the tokens and bits associated with the math operations. Based on the
     * number of registers, like last time we continuosly add the bits from first  the type of operation being done and then the
     * registers needed with it. Only for the shift section of the code do we instead of adding it directly here, we add it
     * through its own seperate method.
     * @return
     */
    public String ParseMath() {
        binaryRepresentation = "000";
        Optional<Token> currentToken = tokenHandler.Peek(0);
        Optional<Token> nextToken = tokenHandler.Peek(1);
        if (currentToken.isPresent()) {
            if (currentToken.get().getType() == TokenType.ADD) {
                binaryRepresentation += "1110";
                tokenHandler.MatchAndRemove(TokenType.ADD);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.SUBTRACT) {
                binaryRepresentation += "1111";
                tokenHandler.MatchAndRemove(TokenType.SUBTRACT);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.MULTIPLY) {
                binaryRepresentation += "0111";
                tokenHandler.MatchAndRemove(TokenType.MULTIPLY);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.AND) {
                binaryRepresentation += "1000";
                tokenHandler.MatchAndRemove(TokenType.AND);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.OR) {
                binaryRepresentation += "1001";
                tokenHandler.MatchAndRemove(TokenType.OR);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.XOR) {
                binaryRepresentation += "1010";
                tokenHandler.MatchAndRemove(TokenType.XOR);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.NOT) {
                binaryRepresentation += "1011";
                tokenHandler.MatchAndRemove(TokenType.NOT);
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    if (nextToken.get().getType() != TokenType.REGISTER) {
                        ParseDestOnly();
                        return binaryRepresentation;
                    } else {
                        tokenHandler.MatchAndRemove(TokenType.REGISTER);
                        binaryRepresentation += "01";
                    }
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseTwoR();
                        if (currentToken.get().getType() == TokenType.REGISTER) {
                            ParseThreeR();
                            return binaryRepresentation;
                        } else {
                            return binaryRepresentation;
                        }
                    } else {
                        return binaryRepresentation;
                    }
                }
            } else if (currentToken.get().getType() == TokenType.SHIFT) {
                tokenHandler.MatchAndRemove(TokenType.SHIFT);
                ParseShift();
            }
            return binaryRepresentation;
        }
        return binaryRepresentation;
    }

    /**
     * Like the short method earlier except, we also have to check if its left or right shift. Both do the same things for now except,
     * for the binary values that they represent. They are returned through the binary representation as normal.
     * @return
     */
    public String ParseShift() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.LEFT) {
            tokenHandler.MatchAndRemove(TokenType.LEFT);
            binaryRepresentation += "1100";
            if (currentToken.get().getType() == TokenType.REGISTER) {
                tokenHandler.MatchAndRemove(TokenType.REGISTER);
                binaryRepresentation += "01";
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    ParseTwoR();
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseThreeR();
                        return binaryRepresentation;
                    } else {
                        return binaryRepresentation;
                    }
                } else {
                    return binaryRepresentation;
                }
            }
        } else if (currentToken.get().getType() == TokenType.RIGHT) {
            binaryRepresentation += "1101";
            tokenHandler.MatchAndRemove(TokenType.RIGHT);
            if (currentToken.get().getType() == TokenType.REGISTER) {
                tokenHandler.MatchAndRemove(TokenType.REGISTER);
                binaryRepresentation += "00000";
                if (currentToken.get().getType() == TokenType.REGISTER) {
                    ParseTwoR();
                    if (currentToken.get().getType() == TokenType.REGISTER) {
                        ParseThreeR();
                        return binaryRepresentation;
                    } else {
                        return binaryRepresentation;
                    }
                } else {
                    return binaryRepresentation;
                }
            }
            return binaryRepresentation;
        }
        return binaryRepresentation;
    }

    /**
     * Adds the next two bits that represents twoR
     * @return
     */
    public String ParseTwoR() {
        binaryRepresentation += "10";
        tokenHandler.MatchAndRemove(TokenType.REGISTER);
        return binaryRepresentation;
    }

    /**
     * Adds the next two bits that represent 3R
     * @return
     */
    public String ParseThreeR() {
        binaryRepresentation += "11";
        tokenHandler.MatchAndRemove(TokenType.REGISTER);
        return binaryRepresentation;
    }

    /**
     * This method automatically adds the 32 bits of a number as shown in the document chart in the directions, adding the correct bits
     * for it
     * @return
     */
    public String ParseDestOnly() {
        binaryRepresentation += "01";
        tokenHandler.MatchAndRemove(TokenType.REGISTER);
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.NUMBER) {
            binaryRepresentation += "00000000000000000000000000000000";
            tokenHandler.MatchAndRemove(TokenType.NUMBER);
            return binaryRepresentation;
        }
        return binaryRepresentation;
    }

    /**
     * Since there is nothing to add for noR, the bits added are only 0's
     * @return
     */
    public String ParseNoR() {
        Optional<Token> currentToken = tokenHandler.Peek(0);
        if (currentToken.get().getType() == TokenType.NUMBER) {
            binaryRepresentation += "00";
            binaryRepresentation += " ";
            binaryRepresentation += "00000000000000000000000000000000";
            tokenHandler.MatchAndRemove(TokenType.NUMBER);
            return binaryRepresentation;
        }
        return binaryRepresentation;
    }
}
