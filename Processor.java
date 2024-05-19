public class Processor {
    private Word PC;
    private Word SP;
    private Bit halted;
    private Word currentInstruction;
    private ALU alu;
    private Word[] registers = new Word[32];
    private Word function;
    private Word opCode;
    private Word immediate;
    private Word rd1R;
    private Word rd2R;
    private Word rd3R;
    private Word rs2R;
    private Word rs1;
    private Word rs23R;
    private Word result;

    public Processor() {
        PC = new Word();
        SP = new Word();
        halted = new Bit(false);
        currentInstruction = new Word();
        function = new Word();
        immediate = new Word();
        rd1R = new Word();
        rd2R = new Word();
        rd3R = new Word();
        rs2R = new Word();
        rs1 = new Word();
        rs23R = new Word();
        opCode = new Word();
        result = new Word();
        alu = new ALU(new Word(), new Word());
        for (int i = 0; i < 32; i++) {
            registers[i] = new Word();
        }
    }

    public Word[] getRegisters() {
        return registers;
    }

    /**
     * Follows the loop found in the document
     */
    public void run() {
        while (!halted.getValue()) {
            fetch();
            decode();
            execute();
            store();
//            System.out.println("Ran");
        }
    }

    /**
     * Gets the next instruction from the memory and then increments the PC
     */
    public void fetch() {
        currentInstruction = MainMemory.read(PC);
        PC.increment();
    }

    /**
     * In this method, I first mask to get the opCode, search for the first two bits and depending on its value, go into each if-else-if statement corresponding to either the
     * destination only, the second register, or the third register. Once I am there, i mask to look for the bits that I need and extract the value that way. Finally, I shift
     * the values back to their original place and move on to the execute method. I declared the neccessary values as globals in order to be able to use it later in the
     * following methods.
     */
    public void decode() {
        Word mask = new Word("11111000000000000000000000000000");
        opCode = currentInstruction.and(mask);
        //opCode.leftShift(27);
        if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
            mask = new Word("00000000000000111111111111111111");
            immediate = currentInstruction.and(mask);
            immediate = immediate.leftShift(14);

            mask = new Word("00000000001111000000000000000000");
            function = currentInstruction.and(mask);
            function = function.leftShift(10);

            mask = new Word("00000111110000000000000000000000");
            rd1R = currentInstruction.and(mask);
            rd1R = rd1R.leftShift(5);

        } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
            mask = new Word("00000000000000000001111111111111");
            immediate = currentInstruction.and(mask);
            immediate = immediate.leftShift(19);

            mask = new Word("00000000000000111110000000000000");
            rs2R = currentInstruction.and(mask);
            rs2R = rs2R.leftShift(14);

            mask = new Word("00000000001111000000000000000000");
            function = currentInstruction.and(mask);
            function = function.leftShift(10);

            mask = new Word("00000111110000000000000000000000");
            rd2R = currentInstruction.and(mask);
            rd2R = rd2R.leftShift(5);

        } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
            mask = new Word("00000000000000000000000011111111");
            immediate = currentInstruction.and(mask);
            immediate = immediate.leftShift(24);

            mask = new Word("00000000000000000001111100000000");
            rs1 = currentInstruction.and(mask);
            rs1 = rs1.leftShift(19);
            // registers[(int) rs1.getUnsigned()].copy(rs1);

            mask = new Word("00000000000000111110000000000000");
            rs23R = currentInstruction.and(mask);
            rs23R = rs23R.leftShift(14);

            mask = new Word("00000000001111000000000000000000");
            function = currentInstruction.and(mask);
            function = function.leftShift(10);

            mask = new Word("00000111110000000000000000000000");
            rd3R = currentInstruction.and(mask);
            rd3R = rd3R.leftShift(5);

        } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == false) {
            mask = new Word("00000111111111111111111111111111");
            immediate = currentInstruction.and(mask);
            immediate = immediate.leftShift(5);
        } else {
            System.out.println("Error with opcode in decode");
        }
    }

    /**
     * Like the last method, I look for the values of the bits in order to determine what operation needs to be done. For the execute method, I first checked for the 2nd, 3rd,
     * and 4th bit to determine what part of the execute method. For the next part, I checked the bit value of the first two bits and completed the operation based on that value.
     * It then executes the 1R, 2R, and then the 3R instruction shown in the chart, utilizing the ALU we created in project 2 to perform the operation and store the results we
     * need. We collect the function bits from the decode section. Finally, if the bits are both false, we stop the program.
     */
    public void execute() {
//        Word mask = new Word("11111000000000000000000000000000");
//        opCode = currentInstruction.and(mask);
        //opCode.leftShift(27);
        if (opCode.getBit(2).getValue() == false && opCode.getBit(3).getValue() == false && opCode.getBit(4).getValue() == false) {
            if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
                //System.out.println("got into 1R");
                rd1R.copy(immediate);
            } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
                //System.out.println("got into 2R");
                alu = new ALU(rs1, rs2R);
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                alu.doOperation(functionArray);
                result = alu.result;
                // registers[(int) rd2R.getUnsigned()].copy(result);
            } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
                //System.out.println("got into 3R");
                alu = new ALU(rs23R, rd3R);
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                alu.doOperation(functionArray);
                result = alu.result;
                //registers[(int) rd3R.getUnsigned()].copy(result);
            } else {
                halted.set(true);
                //System.out.println("Error with opcode in execute");
            }
            /**
             * For the branch operation, I checked for the three values of the opcode, then once in the branch operation, I checked for the first two bits of the opcode to get
             * what register we are in. Once this was done, I called the doBranchOperation method which collected the type of boolean operation we need through the bit Function
             * array I created along with the two words I needed to use as described in the chart. For the dest only and no R, I just jumped from PC to the immediate through
             * addition or by copying it directly.
             */
            //THE BRANCH OP
        } else if (opCode.getBit(2).getValue() == false && opCode.getBit(3).getValue() == false && opCode.getBit(4).getValue() == true) {
            if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                doBranchOperation(functionArray, rs2R, rd2R);
            } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                doBranchOperation(functionArray, rs1, rs23R);
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
                PC.copy(alu.add(PC, immediate));
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == false) {
                PC.copy(immediate);
            } else {
                System.out.println("error in branch");
            }
            /**
             * For the Call part of the chart, we follow a similar situation to the branch operation except, after we calculated the value of the branch operation, we call the
             * push method, which pushes the old PC back onto the stack to be used again at a later point. It also differs in the dest only section of the chart, where
             * now we do a branchoperation there as well calling push just as well. Again for the noR box of the chart we just copy PC into the immediate and again call the push
             * method.
             */
            //THE CALL OP
        } else if (opCode.getBit(2).getValue() == false && opCode.getBit(3).getValue() == true && opCode.getBit(4).getValue() == false) {
            if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                doBranchOperation(functionArray, rs2R, rd2R);
                push();
            } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                doBranchOperation(functionArray, rs1, rs23R);
                push();
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
                Bit[] functionArray = new Bit[4];
                for (int i = 0; i < 4; i++) {
                    functionArray[i] = function.getBit(i);
                }
                doBranchOperation(functionArray, rd1R, immediate);
                push();
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == false) {
                push();
                PC.copy(immediate);
            }
            /**
             * After finding the needed bit value, I called the push method which I implemented below.
             */
            //PUSH OP
        } else if (opCode.getBit(2).getValue() == false && opCode.getBit(3).getValue() == true && opCode.getBit(4).getValue() == true) {
            push();
            /**
             * For the load section of the code, I access the memory and copy the value into the register as needed. Before I do this though, I check to add the two values
             * listed in the chart. For the last part of the chart, I just call the pop method that I created below.
             */
            //LOAD OP
        } else if (opCode.getBit(2).getValue() == true && opCode.getBit(3).getValue() == false && opCode.getBit(4).getValue() == false) {
            if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
                MainMemory.write(rd2R, alu.add(rs2R, immediate));
            } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
                MainMemory.write(rd3R, alu.add(rs1, rs23R));
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
                MainMemory.write(rd1R, alu.add(rd1R, immediate));
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == false) {
                pop(PC);
            }
            /**
             * For the store method, I did something similar to the load method except, instead of saving the addition of two words into the rd register value, I store them
             * into the RS value as needed. However, unlike the previous method, the chart leaves the one with noR completely unused and is therefore not to be considered. The
             * same thing occurs in the push part of the code.
             */
            //STORE OP
        } else if (opCode.getBit(2).getValue() == true && opCode.getBit(3).getValue() == false && opCode.getBit(4).getValue() == true) {
            if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
                MainMemory.write(alu.add(rd2R, immediate), rs2R);
            } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
                MainMemory.write(alu.add(rd3R, rs1), rs23R);
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
                MainMemory.write(rd1R, immediate);
            }
            /**
             * Pop first looks ahead in the stack, then saves the value into the register as needed, and then increments the SP as told by the document. Again, like load and store
             * this is just a memory access method.
             */
            //POP OP
        } else if (opCode.getBit(2).getValue() == true && opCode.getBit(3).getValue() == false && opCode.getBit(4).getValue() == false) {
            if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
                peek(PC);
                MainMemory.write(rd2R, alu.add(rs2R, immediate));
                SP.decrement();
            } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
                peek(PC);
                MainMemory.write(rd3R, alu.add(rs1, rs23R));
                SP.decrement();
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
                pop(PC);
                SP.increment();
            } else if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == false) {
                //INTERRUPT
            }
        } else {
            halted.set(true);
        }
    }

    /**
     * This method stores the result into the register and updates the current instruction based on that. It follows the same if-else-if format as the previous two methods.
     */
    public void store() {
        Word mask = new Word("11111000000000000000000000000000");
        opCode = currentInstruction.and(mask);
        //opCode.leftShift(27);
        if (opCode.getBit(0).getValue() == false && opCode.getBit(1).getValue() == true) {
            registers[(int) rd1R.getUnsigned()] = result;
            currentInstruction.copy(result);
        } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == false) {
            registers[(int) rd2R.getUnsigned()] = result;
            currentInstruction.copy(result);
        } else if (opCode.getBit(0).getValue() == true && opCode.getBit(1).getValue() == true) {
            registers[(int) rd3R.getUnsigned()] = result;
            currentInstruction.copy(result);
        }
        if (!registers[0].equals(0)) {
            currentInstruction.copy(new Word(false));
        }
    }

    /**
     * The push method first listed in the execute method works by calling the ALU, doing an operation, saving the result into the SP, and then decrementing the stack pointer
     * once we are done using it. It does the same thing for every case of the method however, the words and regusters it uses to do the operation is different for each one
     */
    public void push() {
        if (opCode.getBit(2).getValue() == true && opCode.getBit(3).getValue() == true) {
            alu = new ALU(rd2R, rs1);
            Bit[] functionArray = new Bit[4];
            for (int i = 0; i < 4; i++) {
                functionArray[i] = function.getBit(i);
            }
            alu.doOperation(functionArray);
            result = alu.result;
            MainMemory.write(SP, result);
            SP.decrement();
        } else if (opCode.getBit(2).getValue() == true && opCode.getBit(3).getValue() == false) {
            alu = new ALU(rs1, rs2R);
            Bit[] functionArray = new Bit[4];
            for (int i = 0; i < 4; i++) {
                functionArray[i] = function.getBit(i);
            }
            alu.doOperation(functionArray);
            result = alu.result;
            MainMemory.write(SP, result);
            SP.decrement();
        } else if (opCode.getBit(2).getValue() == false && opCode.getBit(3).getValue() == true) {
            alu = new ALU(rd1R, immediate);
            Bit[] functionArray = new Bit[4];
            for (int i = 0; i < 4; i++) {
                functionArray[i] = function.getBit(i);
            }
            alu.doOperation(functionArray);
            result = alu.result;
            MainMemory.write(SP, result);
            SP.decrement();
        }else{
            System.out.println("Error in push");
        }
    }

    /**
     * For the Pop method, we increment the stack pointer and then just take what we read from the SP and copy it into the word. In the usual case, the value being saved would
     * be put into the PC.
     * @param word
     */
    public void pop(Word word){
        SP.increment();
        MainMemory.read(SP).copy(word);
    }

    /**
     * Instead of incrementing the stack, we just look at what is currently in the word and check for it's existance later.
     * @param word
     */
    public void peek(Word word){
        MainMemory.read(SP).copy(word);
    }

    /**
     * In the branchOperation, we do it similar to the way the regular math operation is done in the ALU. First we collect the value of the bits that are passed through, then
     * we go through each case that matches. Once a case has been selected we subtract the two words passed above, check if it passes the condition for that particular operation
     * and then if it does, adds the immediate value to the PC as told to do in the document.
     * @param operation
     * @param op1
     * @param op2
     */
    public void doBranchOperation(Bit[] operation, Word op1, Word op2) {
        String StringOperation = "";
        for (Bit bit : operation) {
            if (bit.getValue()) {
                StringOperation += '1';
            } else {
                StringOperation += '0';
            }
        }
        switch (StringOperation) {
            //EQ
            case "0000":
                result = alu.subtract(op1, op2);
                if (result.getUnsigned() == 0) {
                    PC = alu.add(PC, immediate);
                }
                break;
            case "0001":
                //NEQ
                result = alu.subtract(op1, op2);
                if (result.getUnsigned() != 0) {
                    PC = alu.add(PC, immediate);
                }
                break;
            case "0010":
                result = alu.subtract(op1, op2);
                if (result.getBit(31).getValue()) {
                    PC = alu.add(PC, immediate);
                }
                //LT
                break;
            case "0011":
                result = alu.subtract(op1, op2);
                if (!result.getBit(31).getValue()) {
                    PC = alu.add(PC, immediate);
                }
                //GTE
                break;
            case "0100":
                result = alu.subtract(op1, op2);
                if (result.getBit(31).getValue() && result.getUnsigned() != 0) {
                    PC = alu.add(PC, immediate);
                }
                //GT
                break;
            case "0101":
                result = alu.subtract(op1, op2);
                if (result.getBit(31).getValue() && result.getUnsigned() == 0) {
                    PC = alu.add(PC, immediate);
                }
                //LE
                break;
            default:
                System.out.println("Error w branch OP");
        }
    }
}
/**
 * UnWorking code
 * Word opCode = currentInstruction.leftShift(4);
 * switch (opCode.toString()){
 * case "00000":
 * Word immediate0R = currentInstruction.and(new Word(true));
 * break;
 * case "00001":
 * Word immediate1R = currentInstruction.rightShift(18).and(new Word());
 * Word function1R = currentInstruction.rightShift(4).and(new Word(true));
 * Word rd1R = currentInstruction.rightShift(5).and(new Word(true));
 * break;
 * case "00010":
 * Word immediate2R = currentInstruction.rightShift(13).and(new Word(true));
 * Word rs2R = currentInstruction.rightShift(5).and(new Word(true));
 * Word function2R = currentInstruction.rightShift(4).and(new Word(true));
 * Word rd2R = currentInstruction.rightShift(5).and(new Word(true));
 * break;
 * case "00011":
 * Word immediate3R = currentInstruction.rightShift(8).and(new Word(true));
 * Word rs1 = currentInstruction.rightShift(1).and(new Word(true));
 * Word rs23R = currentInstruction.rightShift(5).and(new Word(true));
 * Word function3R = currentInstruction.rightShift(4).and(new Word(true));
 * Word rd3R = currentInstruction.rightShift(5).and(new Word(true));
 * break;
 * default:
 * System.out.println("Error with opcode in decode");
 * break;
 * }
 * ALU alu = new ALU(new Word(), new Word());
 * Word opCode = currentInstruction.rightShift(25);
 * Bit[] operation = new Bit[4];
 * switch (opCode.toString()){
 * case "00":
 * <p>
 * break;
 * case "01":
 * Word immediate1R = currentInstruction.rightShift(18).and(new Word(true));
 * Word rd1R = currentInstruction.rightShift(23).and(new Word(true));
 * alu.op1 = immediate1R;
 * alu.op2 = new Word();
 * break;
 * case "10":
 * Word rs2R = currentInstruction.rightShift(5).and(new Word(true));
 * Word rd2R = currentInstruction.rightShift(10).and(new Word(true));
 * alu.op1 = rs2R;
 * alu.op2 = rd2R;
 * break;
 * case "11":
 * Word rs1 = currentInstruction.rightShift(1).and(new Word(true));
 * Word rs2 = currentInstruction.rightShift(5).and(new Word(true));
 * Word rd = currentInstruction.rightShift(10).and(new Word(true));
 * alu.op1 = rs1;
 * alu.op2 = rs2;
 * break;
 * default:
 * System.out.println("Error with opcode in execute");
 * break;
 * }
 * alu.doOperation(operation);
 * opCode = currentInstruction.rightShift(27);
 * switch (opCode.toString()) {
 * case "00000":
 * for (int i = 0; i < 32; i++) {
 * immediate.setBit(i, currentInstruction.getBit(i));
 * }
 * immediate = immediate.and(new Word(true));
 * immediate = immediate.leftShift(0);
 * break;
 * case "00001":
 * for (int i = 0; i < 32; i++) {
 * immediate.setBit(i, currentInstruction.getBit(i));
 * }
 * immediate = immediate.rightShift(18).and(new Word(true));
 * function = currentInstruction.rightShift(4).and(new Word(true));
 * registers[(int) rd1R.getUnsigned()] = rd1R;
 * rd1R = rd1R.leftShift(27);
 * break;
 * case "00010":
 * for (int i = 0; i < 32; i++) {
 * immediate.setBit(i, currentInstruction.getBit(i));
 * }
 * immediate = immediate.rightShift(13).and(new Word(true));
 * for (int i = 0; i < 5; i++) {
 * rs2R.setBit(i, currentInstruction.getBit(i + 27));
 * }
 * function = currentInstruction.rightShift(4).and(new Word(true));
 * rs2R = registers[(int) rd2R.getUnsigned()];
 * rd2R = rd2R.leftShift(27);
 * break;
 * case "00011":
 * for (int i = 0; i < 32; i++) {
 * immediate.setBit(i, currentInstruction.getBit(i));
 * }
 * immediate = immediate.rightShift(8).and(new Word(true));
 * for (int i = 0; i < 5; i++) {
 * rs1.setBit(i, currentInstruction.getBit(i + 27));
 * }
 * for (int i = 0; i < 5; i++) {
 * rs23R.setBit(i, currentInstruction.getBit(i + 22));
 * }
 * function = currentInstruction.rightShift(4).and(new Word(true));
 * rs1 = registers[(int) rd3R.getUnsigned()];
 * rs23R = registers[(int) rd3R.getUnsigned()];
 * rd3R = rd3R.leftShift(27);
 * break;
 * default:
 * System.out.println("Error with opcode in decode");
 * break;
 * }
 */
