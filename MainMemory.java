public class MainMemory {
    private static Word[] memory = new Word[1024];
    public static void initialize() {
        for (int i = 0; i < memory.length; i++) {
            memory[i] = new Word();
        }
    }
    /**
     * This method just returns a new word object with the location in the memory address needed. We got the index from the unsigned method we made from the Word class.
     * Reads the word stored at the specified memory address.
     * @param address
     * @return new Word(memory[(int)address.getUnsigned()]);
     */
    public static Word read(Word address) {
        return new Word(memory[(int)address.getUnsigned()]);
    }

    /**
     * Writes the value of the bits to the word address passed in through the parameter.
     * @param address
     * @param value
     */
    public static void write(Word address, Word value) {
        for (int i=0; i<32; i++) {
            memory[(int) address.getUnsigned()].setBit(i, value.getBit(i));
        }
    }

    /**
     * Loads the data into the memory, converting the string of bits passed in through the data array to the Word object
     * @param data
     */
    public static void load(String[] data) {
        for (int i = 0; i < memory.length && i < data.length; i++) {
            Word word = new Word();
            for (int j = 0; j < data[i].length(); j++) {
                word.setBit(j, new Bit(data[i].charAt(j) == '1'));
            }
            memory[i] = word;
        }
    }
}
/*
public static void load(String[] data) {
        initialize();
        for (int i=0; i<memory.length && i<data.length; i++) {
            int value = Integer.parseInt(data[i],2);
            for(int j=0; j<32; j++){
                memory[i].setBit(j, memory[i].getBit(j));
            }
        }
 */