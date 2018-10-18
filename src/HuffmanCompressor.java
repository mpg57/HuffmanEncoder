// Mia Gorczyca mpg57

import java.io.*;

public class HuffmanCompressor {
    // huffman tree class
    public static class HuffmanTree implements Comparable<HuffmanTree> {
        HuffmanNode root; //  root of the tree

        // create a tree with 2 subtrees
        public HuffmanTree(HuffmanTree t1, HuffmanTree t2) {
            root = new HuffmanNode();
            root.left = t1.root;
            root.right = t2.root;
            root.freq = t1.root.freq + t2.root.freq;
        }

        // create a tree containing a leaf node
        public HuffmanTree(int freq, Character inChar) {
            root = new HuffmanNode(freq, inChar);
        }

        @Override // compare trees based on their frequencies
        public int compareTo(HuffmanTree t) {
            if (root.freq < t.root.freq)
                return 1;
            else if (root.freq == t.root.freq)
                return 0;
            else //root.freq > t.root.freq
                return -1;
        }

        // huffman node class
        public class HuffmanNode {
            Character inChar;
            int freq;
            HuffmanNode left;
            HuffmanNode right;
            String triple = ""; // encoding triple

            // empty node
            public HuffmanNode() {}

            // node with specified frequency and character
            public HuffmanNode(int freq, Character inChar) {
                this.freq = freq;
                this.inChar = inChar;
            }
        }
    }

    // get encoding triples for characters, method is called after huffman tree is build
    private static String[] getTripleEncoding(HuffmanTree.HuffmanNode root) {
        if (root == null) return null;
        String[] triples = new String[26]; // 26 letters of alphabet
        setTriples(root, triples);
        return triples;
    }

    // recursive method to set encoding triples for the leaf node
    private static void setTriples(HuffmanTree.HuffmanNode root, String[] triples) {
        if (root.left != null) {
            // left is 0
            root.left.triple = root.triple + "0";
            setTriples(root.left, triples);
            // right is 1
            root.right.triple = root.triple + "1";
            setTriples(root.right, triples);
        }
        else {
            triples[(int)(root.inChar - 'a')] = root.triple;
        }
    }

    // produce Huffman Tree
    public static HuffmanTree produceTree(int[] freqs) {
        // create heap to hold trees
        Heap<HuffmanTree> heap = new Heap<HuffmanTree>();
        for (int i = 0; i < 26; i++) {
            heap.insert(new HuffmanTree(freqs[i], (char) (i + 'a'))); // a leaf node
        }
        while (heap.getSize() > 1) {
            HuffmanTree tree1 = heap.delete(); // smallest frequency tree is deleted
            HuffmanTree tree2 = heap.delete(); // next smallest frequency is deleted
            heap.insert(new HuffmanTree(tree1, tree2)); // merge two trees in correct order
        }
        return heap.delete(); // returns the resulting tree
    }

    // scan file and get list of huffman nodes
    public static int[] scanFile(String fileName) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        //array of letters in alphabet, each index holds frequency of that letter
        int[] freqs = new int[26];
        int nextLetter;

        // goes through characters in file until reaches end of document
        while((nextLetter = br.read()) != -1) {
            char current = (char) nextLetter;
            current = Character.toLowerCase(current); //make lowercase
            //increase frequency of each character
            if (current >= 'a' && current <= 'z')
                freqs[current - 'a']++;
        }
        return freqs;
    }

    // traverse Huffman tree to output the character encoding
    public static void produceOutputFile(HuffmanTree tree, String inputFile, String outputFile) throws IOException {
        // initialize variables
        FileReader fr = new FileReader(inputFile);
        BufferedReader br = new BufferedReader(fr);
        int nextLetter;
        File file = new File(outputFile);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        String[] triples = getTripleEncoding(tree.root); // get codes

        // scan input file for letters, not distinguishing between uppercase and lowercase
        while((nextLetter = br.read()) != -1) {
            char current = (char) nextLetter;
            current = Character.toLowerCase(current);
            if (current >= 'a' && current <= 'z')
                bw.write(triples[current - 'a']);
        }

        // close file reader/writer and buffered reader/writer
        br.close(); bw.close(); fr.close(); fw.close();
    }

    public static String huffmanEncoder(String inputFileName, String encodingFileName, String outputFileName) throws IOException {
        // handle errors
        String outcome = "Error";
        if (inputFileName == null) {
            outcome = "Input file error";
        }
        if (encodingFileName == null) {
            outcome = "Encoding file error";
        }
        if (outputFileName == null) {
            outcome = "Input file error";
        }

        int[] freqs = scanFile(encodingFileName);
        HuffmanTree tree = produceTree(freqs);
        String[] triples = getTripleEncoding(tree.root);

        // create table of character/frequency/encoding triples
        System.out.println(encodingFileName + " character frequencies and codes: ");
        for (int i = 0; i < 26; i++) {
            System.out.println((char) (i + 'a') + ": " + freqs[i] + ": " + triples[i]);
            outcome = "OK";
        }

        produceOutputFile(tree, inputFileName, outputFileName);
        return "Outcome: " + outcome;
    }

    public static String computeSavings(String inputFileName, String outputFileName) throws IOException {
        int charCount = 0;
        int binaryCount = 0;
        int[] freqs = scanFile(inputFileName);

        // get character count and add 8 bits per character
        for (int i = 0; i < 26; i++)
            charCount = charCount + (8 * freqs[i]);

        // get the binary count
        FileReader fileReader = new FileReader(outputFileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // add 1 bit per character
        while(bufferedReader.read() != -1)
            binaryCount = binaryCount + 1;

        bufferedReader.close();
        return "character count: " + charCount + "\t binary count: " + binaryCount + "\nSavings (Relative efficiency): " + (charCount - binaryCount) + "\n\n";
    }

    public static void main(String[] args) throws IOException {
        // First encoding: based on Gadsby.txt
        // java HuffmanCompressor Gadsby.txt Gadsby.txt GadsbyOutput.txt
        // typing this in the terminal window computes the following methods:
        System.out.println(huffmanEncoder("Gadsby.txt", "Gadsby.txt", "GadsbyOutput.txt"));
        System.out.println(computeSavings("Gadsby.txt","GadsbyOutput.txt"));

        // second encoding based on Dictionary_rev.txt:
        // java HuffmanCompressor Gadsby.txt Dictionary_rev.txt DictionaryOutput.txt
        // typing this in the terminal window computes the following methods:
         System.out.println(huffmanEncoder("Gadsby.txt", "Dictionary_rev.txt", "DictionaryOutput.txt"));
         System.out.println(computeSavings("Gadsby.txt","DictionaryOutput.txt"));

//         System.out.println(huffmanEncoder(args[0], args[1], args[2]));
//         System.out.println(computeSavings(args[0], args[2]));
    }
}