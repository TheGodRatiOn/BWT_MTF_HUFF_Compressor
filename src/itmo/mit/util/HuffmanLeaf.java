package itmo.mit.util;

public class HuffmanLeaf extends HuffmanNode {
    private final int number;

    public HuffmanLeaf(int number, int frequency) {
        super(frequency);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
