public class Bit {
    private boolean value;
    public Bit(boolean value) {
        this.value = value;
    }
    public Bit() {
        this.value = false;
    }
    //Sets the value of the bit
    public void set(boolean value) {
        this.value = value;
    }
    //Sets the value to true
    public void set() {
        this.value = true;
    }
    //Sets the bit to false
    public void clear() {
        this.value = false;
    }
    public boolean getValue() {
        return this.value;
    }
    //Toggles the value of the bit from true to false and vice versa
    public void toggle() {
        this.value = !this.value;
    }
    /*
    This method returns a new bit set to true only if both this bit and the other bit are true, else it returns false.
     */
    public Bit and(Bit other) {
        if (!this.value) {
            return new Bit(false);
        }
        return new Bit(other.getValue());
    }
    /*
    Returns a new bit set to true if either this bit or another is true, else it returns false.
     */
    public Bit or(Bit other) {
        if (this.value) {
            return new Bit(true);
        }
        return new Bit(other.getValue());
    }
    /*
    Returns a new bit that is true if this bit and another bit have different values, if not then it returns false.
    */
    public Bit xor(Bit other) {
        if (this.value != other.getValue()) {
            return new Bit(true);
        } else {
            return new Bit(false);
        }
    }

    public Bit not() {
        return new Bit(!value);
    }

    public String toString() {
        if (this.value) {
            return "t";
        } else {
            return "f";
        }
    }
}
