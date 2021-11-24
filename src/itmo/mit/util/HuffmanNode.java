package itmo.mit.util;

public class HuffmanNode implements Comparable<HuffmanNode> {
    private int frequency;
    private HuffmanNode leftNode;
    private HuffmanNode rightNode;

    public HuffmanNode(int frequency) {
        this.frequency = frequency;
        this.leftNode = null;
        this.rightNode = null;
    }

    public HuffmanNode(HuffmanNode poll, HuffmanNode poll1) {
        this.frequency = poll.frequency + poll1.frequency;
        this.leftNode = poll;
        this.rightNode = poll1;
    }

    public HuffmanNode getLeftNode() {
        return leftNode;
    }

    public HuffmanNode getRightNode() {
        return rightNode;
    }

    public void setLeftNode(HuffmanNode leftNode) {
        this.leftNode = leftNode;
    }

    public void setRightNode(HuffmanNode rightNode) {
        this.rightNode = rightNode;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        return Integer.compare(this.frequency, node.frequency);
    }
}
