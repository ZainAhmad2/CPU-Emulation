public class ALU {
    public Word op1 = new Word();
    public Word op2 = new Word();
    public Word result = new Word();
    public ALU(Word op1, Word op2){
        this.op1 = op1;
        this.op2 = op2;
    }
    /**
     * This method takes in an array of Bit objects, iterates through the bits, converts it to a string of four characters, and then matches it with the specific operation
     * we need to do, setting the result equal to the result of the operands op1 and op2 doing that specific operation. For the left and right shift methods, we iterate through
     * only the last five bits shifting op1 by the amount op2 asks for u too.
     * @param operation
     */
    public void doOperation(Bit[] operation) {
        String StringOperation = "";
        for (Bit bit : operation) {
            if (bit.getValue()) {
                StringOperation += '1';
            } else {
                StringOperation += '0';
            }
        }
        switch (StringOperation) {
            case "1000":
                result = op1.and(op2);
                break;
            case "1001":
                result = op1.or(op2);
                break;
            case "1010":
                result = op1.xor(op2);
                break;
            case "1011":
                result = op1.not();
                break;
            case "1100":
                int shiftAmount = 0;
                for (int i = 0; i < 5; i++) {
                    if (op2.getBit(i).getValue()) {
                        shiftAmount += (1 << i);
                    }
                }
                result = op1.leftShift(shiftAmount);
                break;
            case "1101":
                shiftAmount = 0;
                for (int i = 0; i < 5; i++) {
                    if (op2.getBit(i).getValue()) {
                        shiftAmount += (1 << i);
                    }
                }
                result = op1.rightShift(shiftAmount);
                break;
            case "1110":
                result = add(op1, op2);
                break;
            case "1111":
                result = subtract(op1, op2);
                break;
            case "0111":
                result = multiply(op1, op2);
                break;
        }
    }
    public boolean doBranchOperation(Bit[] operation) {
        String StringOperation = "";
        for (Bit bit : operation) {
            if (bit.getValue()) {
                StringOperation += '1';
            } else {
                StringOperation += '0';
            }
        }
        switch (StringOperation) {

        }
        return false;
    }
    /**
     * This method for add2 just follows the pseudocode described by Phipps in the word document. It takes in two words as parameters and computes their sum using bitwise
     * XOR and then calculates the carryOut with bitwise AND and OR operations. The result is then returned as a new Word object.
     * @param a
     * @param b
     * @return sum
     */
    public Word add2(Word a, Word b) {
        for (int i = 0; i < 32; i++) {
            Word bit1 = new Word(a.getBit(i).getValue());
            Word bit2 = new Word(b.getBit(i).getValue());

            Word sum = bit1.xor(bit2).xor(new Word());
            result.setBit(i, sum.getBit(0));
            if(i == 32) {
                Word carryOut = bit1.and(bit2).or(bit1.xor(bit2).and(new Word()));
            }
        }
        return result;
    }
    /**
     * The add4 method computes the sum of four Word objects passed in through the parameters by first using a chain of the XOR operand. To then handle the first carryOut
     * value I considered all possible pairs of AND for the inputs a,b,c,d followed by the OR operand for eahc of these pairs. The second sum is then calculated by using the
     * bitwise XOR with the last word. The second carryout then uses a similar pattern to the add2 carryout, combining XOR, AND, and the OR operand to be able to get our final
     * carryOut value. Finally, the carryOut bit values are combined with the second sum to determine our final result.
     * @param a
     * @param b
     * @param c
     * @param d
     * @return sum2
     */
    public Word add4(Word a, Word b, Word c, Word d) {
        Word carryIn1 = new Word();
        Word carryIn2 = new Word();
        for (int i = 0; i < 32; i++) {
            Word bit1 = new Word(a.getBit(i).getValue());
            Word bit2 = new Word(b.getBit(i).getValue());
            Word bit3 = new Word(c.getBit(i).getValue());
            Word bit4 = new Word(d.getBit(i).getValue());

            Word sum1 = bit1.xor(bit2).xor(bit3).xor(bit4);
            Word carryOut1 = (bit1.and(bit2)).or(bit1.and(bit3)).or(bit2.and(bit3)).or(bit1.and(bit4)).or(bit2.and(bit4)).or(bit3.and(bit4));
            Word sum2 = sum1.xor(carryIn1);
            Word carryOut2 = (sum1.and(carryIn1)).or(sum1.and(carryIn2)).or(carryIn1.and(carryIn2));
            Word sum3 = sum2.xor(carryIn2);

            result.setBit(i, sum3.getBit(0));
            carryIn1 = carryOut1;
            carryIn2 = carryOut2;
        }
        return result;
    }

    /**
     * The add method calls the add2 method to perform the necessary operation.
     * @param op1
     * @param op2
     * @return result
     */
    public Word add(Word op1, Word op2) {
        return add2(op1,op2);
    }

    /**
     * This method follows pseudocode provided by Phipps in the word document. It first creates a negative representation of op2 using the NOT operand, adding it to the op1
     *along with a new Word to to account with possible carryIn associated with the method.
     * @param op1
     * @param op2
     * @return add(op1, add(negative, new Word()))
     */
    public Word subtract(Word op1, Word op2) {
        Word negative = new Word();
        for (int i = 0; i < 32; i++) {
            negative.setBit(i, op2.getBit(i).not());
        }
        return add(op1, add(negative, new Word()));
    }

    /**
     * As described in the word document provided by Phipps, this method follows a multi-round approach to solve for the multipliction of bits. In the first round, we shift
     * op2 left by multiples of four and adding by the number of shifted values. In the second round, we combine the elements of roundone, alongside with new Words to be able
     * to account for carryIn. Finally, in the thirdround we solve for the final result by summing up the first two elements of roundtwo and storing the final result in a
     * wordresult Word object, where each of the bits we solved for is set and returned back to the original doOperation method.
     * @param op1
     * @param op2
     * @return wordResult
     */
    public Word multiply(Word op1, Word op2) {
        Word[] roundOne = new Word[8];
        for(int i=0; i<8; i++) {
            roundOne[i] = add4(op1, op2.leftShift(4*i), op2.leftShift((4*i)+1), op2.leftShift((4*i)+2));
        }
        Word[] roundTwo = new Word[4];
        for (int i = 0; i < 4; i++) {
            roundTwo[i] = add4(roundOne[i*2], roundOne[(i*2)+1], new Word(true), new Word(true));
        }
        Word[] roundThree = new Word[2];
        roundThree[0] = add2(roundTwo[0], roundTwo[1]);
        roundThree[1] = add2(roundTwo[2], roundTwo[3]);

        Word wordResult = new Word();
        for (int i = 0; i < 32; i++) {
            if(i<16){
                wordResult.setBit(i, roundThree[0].getBit(i));
            }else{
                wordResult.setBit(i, roundThree[1].getBit(i-16));
            }
        }
        return wordResult;
    }
}
/*
//Before change made by Phipps
private Bit[] add4(Bit a, Bit b, Bit c, Bit d, Bit carryIn){
        Bit sum1 = a.xor(b).xor(carryIn);
        Bit carryOut1 = a.and(b).or(a.xor(b).and(carryIn));

        Bit sum2 = sum1.xor(c).xor(d);
        Bit carryOut = sum1.and(c).or(sum1.xor(c).and(d));
        return new Bit[]{sum2, carryOut};
    }
    public Word multiply(Word op1, Word op2){
        Bit result = new Bit();
        Word[] round1 = new Word[8];
        for(int i =0; i<8; i++){
            round1[i] = new Word(false);
        }

        for(int i =0; i<8; i++){
            round1[i] = add4(op1.getBit(i), op2.leftShift(4*i).getBit(i), op2.leftShift((4*i)+1).getBit(i), op2.leftShift((4*i)+2).getBit(i), op2.leftShift((4*i)+3).getBit(i));
        }
    }
    Bit[] roundThree = add2(roundTwo[0], roundTwo[1], new Bit(false));
        result[0] = roundThree[0];
        for(int i =1; i< roundTwo.length; i++){
            result[i] = roundThree[i];
        }
        Word wordResult = new Word();
        for(int i =0; i< result.length; i++){
            wordResult.setBit(i, result[i]);
        }
        return wordResult;
    }
     public Bit[] add2(Bit a, Bit b, Bit carryIn) {
        Bit sum = a.xor(b).xor(carryIn);
        Bit carryOut = a.and(b).or(a.xor(b).and(carryIn));
        return new Bit[]{sum, carryOut};
    }

    public Bit[] add4(Bit a, Bit b, Bit c, Bit d, Bit carryIn) {
        Bit sum1 = a.xor(b).xor(carryIn);
        Bit carryOut1 = (a.and(b)).or(a.and(c)).or(b.and(c));
        Bit sum2 = sum1.xor(c).xor(d);
        Bit carryOut2 = (sum1.and(d)).or(c.and(d)).or(sum1.and(d));

        return new Bit[]{sum2, carryOut1.xor(carryOut2)};
    }
    public Word add(Word op1, Word op2) {
        Word carryIn = new Word(false);
        for (int i = 0; i < 32; i++) {
            Word sum = add2(op1, op2);
            result.setBit(i, sum.getBit(i));
            carryIn = sum.getBit(32);
        }
        return result;
    }  Bit[] result = new Bit[8];

        Bit[] roundOne = new Bit[8];
        for (int i = 0; i < 8; i++) {
            Bit[] roundOneAddition = add4(op1.getBit(i), op2.leftShift(4 * i).getBit(i), op2.leftShift((4 * i + 1)).getBit(i), op2.leftShift((4 * i + 2)).getBit(i), op2.leftShift((4 * i + 3)).getBit(i));
            roundOne[i] = roundOneAddition[0];
        }

        Bit[] roundTwo = new Bit[4];
        for (int i = 0; i < 4; i++) {
            Bit[] roundTwoAddition = add4(roundOne[i * 2], roundOne[i * 2 + 1], new Bit(false), new Bit(false), new Bit(false));
            roundTwo[i] = roundTwoAddition[0];
        }
        result[0] = roundTwo[0];
        result[1] = roundTwo[1];

        Bit[] roundThree = add2(roundTwo[0], roundTwo[1], new Bit(false));
        result[2] = roundThree[0];
        result[3] = roundThree[1];

        Word wordResult = new Word();
        for (int i = 0; i < 4; i++) {
            wordResult.setBit(i, result[i]);
        }
        return wordResult;
    }
    public Word add4(Word a, Word b, Word c, Word d) {
        Word sum1 = a.xor(b).xor(c).xor(d);
        Word carryOut1 = (a.and(b)).or(a.and(c)).or(b.and(c));
        Word sum2 = sum1.xor(c).xor(d);
        Word carryOut2 = (sum2.and(d)).or(c.and(d)).or(sum2.and(d));
        return sum2;
    }
 */