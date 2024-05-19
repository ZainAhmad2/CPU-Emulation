public class Word {
    private Bit[] bits = new Bit[32];

    public Word(boolean value) {
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit(value);
        }
    }
    public Word(Word value) {
        for (int i = 0; i < 32; i++) {
            bits[i] = value.getBit(i);
        }
    }
    public Word(String value) {
        for (int i = 0; i < 32; i++) {
            char c = value.charAt(i);
            if(c == '0'){
                bits[i] = new Bit(false);
            }else{
                bits[i] = new Bit(true);
            }
        }
    }

    public Word() {
        for (int i = 0; i < 32; i++) {
            bits[i] = new Bit();
        }
    }

    public Bit getBit(int i) {
        return new Bit(bits[i].getValue());
    }

    public void setBit(int i, Bit value) {
        bits[i].set(value.getValue());
    }

    //Uses AND from Bit class to return a new Word here.
    public Word and(Word other) {
        Word result = new Word();
        for (int i = 0; i < 32; i++) {
            result.bits[i] = bits[i].and(other.bits[i]);
        }
        return result;
    }

    //Uses OR from Bit class to return a new Word here.
    public Word or(Word other) {
        Word result = new Word();
        for (int i = 0; i < 32; i++) {
            result.bits[i] = bits[i].or(other.bits[i]);
        }
        return result;
    }

    //Uses XOR from Bit class to return a new Word here.
    public Word xor(Word other) {
        Word result = new Word();
        for (int i = 0; i < 32; i++) {
            result.bits[i] = bits[i].xor(other.bits[i]);
        }
        return result;
    }

    //Uses NOT from Bit class to return a new Word here.
    public Word not() {
        Word result = new Word();
        for (int i = 0; i < 32; i++) {
            result.bits[i] = bits[i].not();
        }
        return result;
    }

    /*
    This method takes an amount in and shifts the bits of the word to the right by that amount, filling in the shifted bits with 0.
     */
    public Word rightShift(int amount) {
        Word result = new Word();
        for (int i = amount; i < 32; i++) {
            result.setBit(i, bits[i - amount]);
        }
        return result;
    }

    /*
    This method takes an amount in and shifts the bits of the word to the left by that amount, filling in the shifted bits with 0.
     */
    public Word leftShift(int amount) {
        Word result = new Word();
        for (int i = 0; i < 32 - amount; i++) {
            result.setBit(i, bits[i + amount]);
        }
        return result;
    }

    /**
     * Increments the bit array by 1. Starting from the first bit, we first use the XOR operand along with the carry value from the previous bit calculation, updating the value
     * of the carryIn accordingly.
     */
    public void increment() {
        Bit carryIn = new Bit(true);
        for (int i = 0; i <32; i++) {
            Bit currentBit = new Bit(bits[i].getValue());
            bits[i].set(currentBit.xor(carryIn).getValue());
            carryIn = carryIn.and(currentBit);

        }
    }

    /**
     * Decrements the bit array by 1, similar to increment except after, we use the not() operation for it to calculate successfully.
     */
    public void decrement() {
        Bit carryIn = new Bit(true);
        for (int i = 0; i <32; i++) {
            Bit currentBit = new Bit(bits[i].getValue());
            bits[i].set(currentBit.xor(carryIn).getValue());
            carryIn = carryIn.and(currentBit.not());
        }
    }

    public String toString() {
        String result = "";
        for (int i = 0; i < 31; i++) {
            result += bits[i].toString() + ",";
        }
        result += bits[31].toString();
        return result;
    }

    /**
     * For this method, I iterated through each bit in the word, doubling the value for each iteration and incrementing by one if the
     * bit ends up being true. The final calculated unsigned long value is then returned.
     *
     * @return value
     */
    public long getUnsigned() {
        long value = 0;
        for (int i = 0; i < 32; i++) {
            if(getBit(i).getValue()) {
                value += (long) Math.pow(2,i);
            }
        }
        return value;
    }

    /**
     * If the value of the bit turns out to be positive, we directly call the usigned method and convert it into an int. If it is negative,
     * we iterate through the bits like in the unsigned method, shift the bits to the left and mulitply it by two and calculate the final
     * value of the bit by subtracting the value from the largerst positive value stored by 32 bits and then multiplying it by -1 to get
     * the actual value.
     *
     * @return value
     */
    public int getSigned() {
        if (!bits[31].getValue()) {
            return (int) getUnsigned();
        } else {
            int value = 1;
            for (int i = 0; i < 32; i++) {
                value *= 2;
            }
            value = -1 * ((1 << 31) - value);
            return value;
        }
    }

    public void copy(Word other) {
        for (int i = 0; i < 32; i++) {
            bits[i].set(other.getBit(i).getValue());
        }
    }

    /**
     * This methods sets the value of the bit based on the provided value passed throiugh in the parameter. It iterates through each
     * position, creating the mask with the single bit set at the current position, then it uses the bitwise AND operation with the value
     * to determine whether the bit should be set into the word. Then we just update the satte of the bit based on that AND operation.
     *
     * @param value
     */
    public void set(int value) {
        for (int i = 0; i < 32; i++) {
            int mask = 1;
            for (int j = 0; j < i; j++) {
                mask *= 2;
            }
            bits[i].set((value & mask) != 0);
        }
    }
}
/*
public int getSigned(){
        int value = 0;
        for(int i = 0; i<31; i++){
            value*=2;
            if(bits[i].getValue() == true){
                value++;
            }
        }
        if(bits[31].getValue() == true){
            return value;
        }else{
           value -= (1<<31);
        }
        return value;
    }
      public Word(Bit[] bitArray) {
        for(int i = 0; i<32; i++){
            bits[i] = new Bit();
        }
    }
     public void increment(){
        for(int i =0; i<32; i++){
            bits[i] = bits[i].xor(bits[i].and(bits[i]));
        }
    }
 */