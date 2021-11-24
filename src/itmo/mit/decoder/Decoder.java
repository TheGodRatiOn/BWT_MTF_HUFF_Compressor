package itmo.mit.decoder;

import itmo.mit.util.Huffman;
import itmo.mit.util.HuffmanNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static itmo.mit.encoder.Encoder.getPowerOf2;

public class Decoder {
    private static String reverseMTF(int[] intArray, String alphabet) {
        StringBuilder sb = new StringBuilder();

        for (int value : intArray) {
            sb.append(alphabet.charAt(value));
            alphabet = alphabet.charAt(value)
                    + alphabet.substring(0, value)
                    + alphabet.substring(value + 1);
        }

        return sb.toString();
    }

    private static String reverseBWT(String inputString, String alphabet, int number) {
        int[] count = getCharBorderPositions(inputString, alphabet);
        char[] chars = new char[inputString.length()];

        for (int i = 0; i < inputString.length(); i++) {
            int index = alphabet.indexOf(inputString.charAt(i));
            chars[count[index]] = inputString.charAt(i);
            count[index]++;
        }
        String lexSorted = String.valueOf(chars);
        int[] count2 = new int[inputString.length()];

        count = getCharBorderPositions(inputString, alphabet);

        for (int i = 0; i < inputString.length(); i++) {
            int indexOf = alphabet.indexOf(inputString.charAt(i));
            count2[i] = lexSorted.indexOf(inputString.charAt(i), count[indexOf]);
            count[indexOf]++;
        }

        int indexOfNumber = getIndexOfNumber(count2, number);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < inputString.length(); i++) {
            sb.append(inputString.charAt(indexOfNumber));
            indexOfNumber = getIndexOfNumber(count2, indexOfNumber);
        }

        return sb.toString();
    }

    private static int getIndexOfNumber(int[] intArray, int number) {
        for (int i = 0; i < intArray.length; i++) {
            if (intArray[i] == number) {
                return i;
            }
        }
        return -1;
    }

    private static int[] getCharBorderPositions(String inputString, String alphabet) {
        int[] count = new int[alphabet.length()];

        for (int i = 0; i < inputString.length(); i++) {
            count[alphabet.indexOf(inputString.charAt(i))]++;
        }

        int sum = 0;
        for (int i = 0; i < alphabet.length(); i++) {
            sum = sum + count[i];
            count[i] = sum - count[i];
        }
        return count;
    }

    private static int[] getIntsFromString(String string) {
        int[] indexes = new int[5];
        int[] result = new int[5];
        int j = 0;

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ',') {
                indexes[j] = i;
                j++;
            }
        }
        indexes[4] = string.length();

        j = 0;
        int i = 0;
        while (i < string.length()) {
            if (j == 3) {
                System.out.println(string.charAt(i));
                result[j] = string.charAt(i);
            } else {
                result[j] = Integer.parseInt(string.substring(i, indexes[j]));
            }
            i = indexes[j] + 1;
            j++;
        }

        return result;
    }

    private static String getSortedStringFromCharArray(char[] chars) {
        List<Character> characters = new ArrayList<>();

        for (char c : chars) {
            characters.add(c);
        }

        Collections.sort(characters);

        StringBuilder sb = new StringBuilder();
        for (Character c : characters) {
            sb.append(c);
        }

        return sb.toString();
    }

    private static int[] getInsertionOrder(String original, String alphabet) {
        int[] result = new int[original.length()];

        for (int i = 0; i < original.length(); i++) {
            result[i] = alphabet.indexOf(original.charAt(i));
        }

        return result;
    }

    private static boolean[] concatenateBoolArrays(boolean[] first, boolean[] second) {
        boolean[] result = new boolean[first.length + second.length];

        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);

        return result;
    }

    private static boolean[] bitOctetFromShort(short number) {
        if (number < 256) {
            boolean[] result = new boolean[8];
            int i = 0;
            while (number > 0) {
                result[result.length - 1 - i] = number % 2 == 1;
                i++;
                number = (short) (number >> 1);
            }
            return result;

        } else {
            return null;
        }
    }

    private static boolean[] getBitArrayCharArray(char[] chars) {
        boolean[] result = new boolean[0];

        for (char a : chars) {
            result = concatenateBoolArrays(result, Objects.requireNonNull(bitOctetFromShort((short) a)));
        }

        return result;
    }

    private static String getStringFromBitSequence(boolean[] booleans) {
        StringBuilder sb = new StringBuilder();

        for (boolean aBoolean : booleans) {
            if (aBoolean) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }

        return sb.toString();
    }

    private static byte[] getOffsetsFromInt(int number) {
        byte[] offsets = new byte[2];
        boolean[] octet = bitOctetFromShort((short) number);

        assert octet != null;

        for (int i = 0; i < 3; i++) {
            if (octet[i + 2]) {
                offsets[0] += getPowerOf2(i);
            }
        }

        for (int i = 0; i < 3; i++) {
            if (octet[i + 5]) {
                offsets[1] += getPowerOf2(i);
            }
        }

        return offsets;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Wrong number of file path arguments");
        }

        String inPath = args[0];
        String outPath = args[1];


        try {
            BufferedReader is = new BufferedReader(new InputStreamReader(
                    new FileInputStream(inPath), StandardCharsets.UTF_8));

            File outputFile = new File(outPath);

            outputFile.createNewFile();
            BufferedWriter os = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFile, false), StandardCharsets.UTF_8));

            int chunkNumber = 1;
            while (is.ready()) {
                String line = is.readLine();
                System.out.println("Decoding chunk №" + chunkNumber);

                int[] chunkConfig = getIntsFromString(line);

                char[] alphabet = new char[chunkConfig[0]];
                char[] charSequence = new char[chunkConfig[1]];
                char[] huffStruct = new char[chunkConfig[2]];
                byte[] offsets = getOffsetsFromInt(chunkConfig[3]);

                is.read(alphabet);
                is.read(huffStruct);
                is.read(charSequence);

                String originalAlphabet = String.valueOf(alphabet);
                String sortedAlphabet = getSortedStringFromCharArray(alphabet);
                int[] order = getInsertionOrder(originalAlphabet, sortedAlphabet);

                boolean[] huffBitStruct = getBitArrayCharArray(huffStruct);
                huffBitStruct = Arrays.copyOf(huffBitStruct, huffBitStruct.length - offsets[0]);

                Huffman huffman = new Huffman(huffBitStruct, order);
                HuffmanNode root = new HuffmanNode(-1);
                huffman.setRoot(root);
                huffman.generateHuffmanTree(root);
                huffman.generateAnotherHuffmanCode(root, "");
                Map<Integer, String> resultCodes = huffman.getHuffmanCodes();
                Map<String, Integer> reversedMap = new HashMap<>();

                for (Map.Entry<Integer, String> codes : resultCodes.entrySet()) {
                    reversedMap.put(codes.getValue(), codes.getKey());
                }

                boolean[] bitSequence = getBitArrayCharArray(charSequence);
                bitSequence = Arrays.copyOf(bitSequence, bitSequence.length - offsets[1]);

                List<Integer> valuesToMTF = new ArrayList<>();

                int j = 1;
                int i = 0;
                while (i < bitSequence.length) {
                    final String mapKey = getStringFromBitSequence(Arrays.copyOfRange(bitSequence, i, i + j));
                    if (reversedMap.containsKey(mapKey)) {
                        valuesToMTF.add(reversedMap.get(mapKey));
                        i = i + j;
                        j = 1;
                    } else {
                        j++;
                    }
                }

                int[] ints = new int[valuesToMTF.size()];

                for (int k = 0; k < ints.length; k++) {
                    ints[k] = valuesToMTF.get(k);
                }

                String result = reverseBWT(reverseMTF(ints, sortedAlphabet), sortedAlphabet, chunkConfig[4]);
                os.write(result);
                chunkNumber++;
            }

            is.close();
            os.close();
        } catch (IOException e) {
            System.err.println("IO Exception");
            e.printStackTrace();
        }
    }
}