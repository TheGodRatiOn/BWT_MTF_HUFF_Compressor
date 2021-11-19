package itmo.mit.util;

import java.util.*;

public class Huffman {
    private final List<Boolean> structure;
    private final List<Integer> order;
    private HuffmanNode root;
    private int curOrder;
    private int curStruct;
    private final Map<Integer, String> huffmanCodes;

    public Huffman() {
        this.structure = new ArrayList<>();
        this.order = new ArrayList<>();
        this.huffmanCodes = new HashMap<>();
    }

    public Huffman(boolean[] structure, int[] order) {
        this.huffmanCodes = new HashMap<>();
        List<Integer> integers = new ArrayList<>();
        for (int value : order) {
            integers.add(value);
        }
        this.order = integers;
        List<Boolean> booleans = new ArrayList<>();
        for (boolean b : structure) {
            booleans.add(b);
        }
        this.structure = booleans;

    }

    public Map<Integer, String> getHuffmanCodes(int[] appearances) {
        Queue<HuffmanNode> queue = new PriorityQueue<>();

        for (int i = 0; i < appearances.length; i++) {
            queue.add(new HuffmanLeaf(i, appearances[i]));
        }
        while (queue.size() > 1) {
            queue.add(new HuffmanNode(queue.poll(), Objects.requireNonNull(queue.poll())));
        }
        this.root = queue.poll();
        generateHuffmanCode(this.root, "");
        return this.huffmanCodes;
    }

    private void generateHuffmanCode(HuffmanNode huffmanNode, String code) {
        if (huffmanNode instanceof HuffmanLeaf) {
            int number = ((HuffmanLeaf) huffmanNode).getNumber();
            this.huffmanCodes.put(number, code);
            this.structure.add(false);
            this.order.add(number);
            return;
        }
        this.structure.add(true);
        generateHuffmanCode(huffmanNode.getLeftNode(), code.concat("0"));
        generateHuffmanCode(huffmanNode.getRightNode(), code.concat("1"));
    }

    public void generateAnotherHuffmanCode(HuffmanNode huffmanNode, String code) {
        if (huffmanNode.getFrequency() > -1) {
            int number = huffmanNode.getFrequency();
            this.huffmanCodes.put(number, code);
            return;
        }
        generateAnotherHuffmanCode(huffmanNode.getLeftNode(), code.concat("0"));
        generateAnotherHuffmanCode(huffmanNode.getRightNode(), code.concat("1"));
    }

    public void generateHuffmanTree(HuffmanNode huffmanNode) {
        if (!this.structure.get(this.curStruct)) {
            huffmanNode.setFrequency(this.order.get(this.curOrder));
            this.curOrder++;
            this.curStruct++;
        } else {
            this.curStruct++;
            huffmanNode.setLeftNode(new HuffmanNode(-1));
            huffmanNode.setRightNode(new HuffmanNode(-1));

            generateHuffmanTree(huffmanNode.getLeftNode());
            generateHuffmanTree(huffmanNode.getRightNode());
        }
    }

    public void setRoot(HuffmanNode root) {
        this.root = root;
    }

    public List<Boolean> getStructure() {
        return structure;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public Map<Integer, String> getHuffmanCodes() {
        return this.huffmanCodes;
    }
}
