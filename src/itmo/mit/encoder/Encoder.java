package itmo.mit.encoder;

import itmo.mit.util.Huffman;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Encoder {
    private static BWTResult BWT(String inputString) {
        if (!inputString.isEmpty()){
            List<String> strings = new ArrayList<>();
            String buffer = inputString;
            strings.add(inputString);
            String result;

            int i = 0;
            while (i < inputString.length() - 1) {
                buffer = buffer.charAt(buffer.length() - 1) + buffer.substring(0, buffer.length() - 1);
                strings.add(buffer);
                i++;
            }

            Collections.sort(strings);

            StringBuilder stringBuilder = new StringBuilder();
            for (String string : strings) {
                stringBuilder.append(string.charAt(string.length() - 1));
            }
            result = stringBuilder.toString();

            return new BWTResult(result, strings.indexOf(inputString));
        }
        return null;
    }

    private static int[] MTF(String inputString, String alphabet) {
        int[] result = new int[inputString.length()];

        for (int i = 0; i < inputString.length(); i++) {
            int index = alphabet.indexOf(inputString.charAt(i));
            result[i] = index;
            alphabet = alphabet.charAt(index)
                    + alphabet.substring(0, index)
                    + alphabet.substring(index + 1);
        }

        return result;
    }

    private static String getAlphabet(String string) {
        Set<Character> charSet = new HashSet<>();

        for (int i = 0; i < string.length(); i++) {
            charSet.add(string.charAt(i));
        }

        List<Character> charList = new ArrayList<>(charSet);

        Collections.sort(charList);

        StringBuilder sb = new StringBuilder();

        for (Character c : charList) {
            sb.append(c);
        }

        return sb.toString();
    }

    public static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    public static int getPowerOf2(int power) {
        int result = 1;

        for (int i = 0; i < power; i++) {
            result = result << 1;
        }

        return result;
    }

    public static boolean[] fillBitArray(boolean[] bitList, boolean type, int bts, boolean fillType) {
        final int newLength = bitList.length + 1 + bts;
        boolean[] result = Arrays.copyOf(bitList, newLength);
        result[bitList.length] = type;

        if (bts > 0) {
            Arrays.fill(result, bitList.length + 1, newLength, fillType);
        }

        return result;
    }

    //Wasn't used in the final version, but you can try to implement it instead of Huffman encoding
    private static boolean[] arithmeticEncoding(int[] inputSequence, int[] cumulativeApps, int inputSize) {
        final int k = 32;
        final long R4 = getPowerOf2(k - 2);
        final long R2 = R4 << 1;
        final long R34 = R2 + R4;
        final long R = R2 << 1;

        long low = 0;
        long high = R - 1;
        int bitsToFollow = 0;

        boolean[] bitSequence = new boolean[0];

        for (int value : inputSequence) {
            final long Range = high - low + 1;
            high = low + Math.round(Math.floor(Range * cumulativeApps[value + 1] * cumulativeApps[cumulativeApps.length - 1] / (inputSize * inputSize)) - 1);
            low = low + Math.round(Math.floor(Range * cumulativeApps[value] * cumulativeApps[cumulativeApps.length - 1] / (inputSize * inputSize)));

            while (true) {
                if (high < R2) {
                    bitSequence = fillBitArray(bitSequence, false, bitsToFollow, true);
                    bitsToFollow = 0;
                    high = (high << 1) + 1;
                    low = low << 1;
                } else {
                    if (low >= R2) {
                        bitSequence = fillBitArray(bitSequence, true, bitsToFollow, false);
                        bitsToFollow = 0;
                        high = (high << 1) - R + 1;
                        low = (low << 1) - R;
                    } else {
                        if ((low >= R4) && (high < R34)) {
                            high = (high << 1) - R2 + 1;
                            low = (low << 1) - R2;
                            bitsToFollow++;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        if (low < R4) {
            bitSequence = fillBitArray(bitSequence, false, bitsToFollow + 1, true);
        } else {
            bitSequence = fillBitArray(bitSequence, true, bitsToFollow + 1, false);
        }

        return bitSequence;
    }

    private static short getShortFromBitOctet(boolean[] octet) {
        short result = 0;

        for (int i = 0; i < octet.length; i++) {
            if (octet[i]) {
                result = (short) (result + getPowerOf2(octet.length - i - 1));
            }
        }
        return result;
    }

    private static char[] charArrayFromBit(boolean[] bitList) {
        char[] about = new char[bitList.length / 8];

        for (int i = 0; i < about.length; i++) {
            about[i] = (char)(getShortFromBitOctet(Arrays.copyOfRange(bitList, i * 8, i * 8 + 8)));
        }

        return about;
    }

    private static int[] countAppearancesOfAlphabetNumber(int alphabetSize, int[] sequence) {
        int[] mentions = new int[alphabetSize];

        for (int i = 0; i < alphabetSize; i++) {
            final int a = i;
            mentions[i] = (int) Arrays.stream(sequence).filter(i1 -> i1 == a).count();
        }

        return mentions;
    }

    private static boolean[] getBitArrayFromString(String inputString) {
        boolean[] result = new boolean[inputString.length()];

        for (int i = 0; i < inputString.length(); i++) {
            result[i] = inputString.charAt(i) == '1';
        }
        return result;
    }

    private static boolean[] concatenateBoolArrays(boolean[] first, boolean[] second){
        boolean[] result = new boolean[first.length + second.length];

        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);

        return result;
    }

    private static boolean[] getBooleans(boolean[] encoded, byte seqOffset) {
        if (seqOffset > 0) {
            encoded = fillBitArray(encoded, true, seqOffset - 1, true);
        }
        return encoded;
    }

    private static byte getSeqOffset(boolean[] encoded) {
        byte seqOffset = 0;

        while ((seqOffset + encoded.length) % 8 != 0) {
            seqOffset++;
        }
        return seqOffset;
    }

    public static void main(String[] args) {
        if (args.length < 2){
            System.out.println("Wrong number of file path arguments");
        }

        String inPath = "";
        String outPath = args[1];

        try {FileInputStream is = new FileInputStream(new File(inPath));
            File outputFile = new File(outPath);

            outputFile.createNewFile();
            BufferedWriter os = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outputFile, false), StandardCharsets.UTF_8));

            //Java heap setup = 2048 MB

            byte[] inputBuffer = new byte[40960];
            int chunkLen;
            int chunk = 1;

            while ((chunkLen = is.read(inputBuffer)) != -1) {
                System.out.println("Encoding chunk №" + chunk);

                String text = new String(Arrays.copyOf(inputBuffer,chunkLen) , StandardCharsets.UTF_8);

                String alphabet = getAlphabet(text);
                BWTResult BWTResult = BWT(text);

                int[] mtfResult = MTF(BWTResult.getResult(), alphabet);
                int[] mtfAppearances = countAppearancesOfAlphabetNumber(alphabet.length(), mtfResult);

                Huffman huffman = new Huffman();
                Map<Integer, String> resultCodes = huffman.getHuffmanCodes(mtfAppearances);

                boolean[] encoded = new boolean[0];

                for (int value : mtfResult) {
                    encoded = concatenateBoolArrays(encoded, getBitArrayFromString(resultCodes.get(value)));
                }

                boolean[] huffStruct = new boolean[huffman.getStructure().size()];

                for (int i = 0; i < huffStruct.length; i++) {
                    huffStruct[i] = huffman.getStructure().get(i);
                }

                byte seqOffset = getSeqOffset(encoded);
                byte huffOffset = getSeqOffset(huffStruct);
                encoded = getBooleans(encoded, seqOffset);
                huffStruct = getBooleans(huffStruct, huffOffset);

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < alphabet.length(); i++) {
                    sb.append(alphabet.charAt(
                            huffman.getOrder().get(i)));
                }
                String charOrder = sb.toString();

                char[] byteEncoded = charArrayFromBit(encoded);
                char[] byteHuffman = charArrayFromBit(huffStruct);

                os.write(charOrder.length() + "," + byteEncoded.length + ","  + byteHuffman.length + "," +
                        + seqOffset + "," + huffOffset + "," +  BWTResult.getPosition());
                os.newLine();
                os.write(charOrder);
                os.write(byteHuffman);
                os.write(byteEncoded);
                chunk++;
            }

            is.close();
            os.close();
        } catch (IOException e) {
            System.err.println("I/O Error");
            e.printStackTrace();
        }
    }
}