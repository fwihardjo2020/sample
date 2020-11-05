/**
 * NextGateChallenge.java
 * Interviewer: Gevik Nalbandian
 *
 * @author Fei Wihardjo
 */
package com.nextgate;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Problem description:
 * Sort a file of ";" terminated English sentences.
 *
 * Constraints:
 * Memory - 1GB RAM available
 * Disk - 16GB.
 * File is 8GB in size, so available disk space is 8GB.
 *
 * Approach:
 *  Sort 0.5GB of sentences at a time, save the sorted sentences into a new file, for example sorted1.txt
 *  (assume sorting will require memory usage, so to be safe, we are processing 1/2GB RAM of sentences.
 *  Repeat until EOF of original file, then delete the original file.
 *
 *  We now have 16-0.5GB sorted files and 8GB disk space left.
 *  Merge 2 sorted files at a time, deleting the old files to make room,
 *  so we now have 8 - 1GB files.
 *  Repeat merging of sorted files until there is only 1 sorted file.
 */
public class NextGateChallenge {

    // Sentences to be sort in memory.
    private List<String> sentences = new ArrayList<>();
    // Generated sorted files.
    private List<String> sortedFileNames = new ArrayList<>();

    // Assuming sorting requires some memory usage, to be safe, we will process 1/2GB at a time.
    // Assuming further a char takes 2 bytes, sets the limit accordingly.
    // private static final long MEMORY_LIMIT = 250_000_000_000L;
    /* for testing use smaller limit */
    private static final long MEMORY_LIMIT = 200L;

    // Sorted file counter.
    private int fileCount = 1;

    /**
     * Sort sentences of the given file name.
     *
     * Some assumptions:
     *  sentences are not long, so n number of sentences can be < 1/2GB.
     *  assume a char takes 2 bytes in memory.
     *
     * @param filename
     * @throws IOException - no error handling at this time.
     */
    public void doSolve(String filename) throws IOException {
        File file = new File(filename);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        long count = 0;

        int c = 0;
        StringBuffer buffer = new StringBuffer();
        while ((c = bufferedReader.read()) != -1) {
            char ch = (char)c;
            buffer.append(ch);
            if (ch == ';') {
                sentences.add(buffer.toString());
                // clear the buffer
                buffer.setLength(0);
            }
            // assuming a character takes 2 bytes, read roughly 0.5GB worth of characters.
            if (count++ >= MEMORY_LIMIT) {
                String filename1 = "./test/sorted_"+(fileCount++)+".txt";
                Collections.sort(sentences);
                saveSortedSentences(sentences, filename1, false);
                // reset memory
                sentences.clear();
                count = buffer.length();
            }
        }

        // make sure we process the last section if the last section is less than the limit
        if (!sentences.isEmpty()) {
            String filename2 = "./test/sorted_"+(fileCount++)+".txt";
            Collections.sort(sentences);
            saveSortedSentences(sentences, filename2, false);
        }

        bufferedReader.close();
        fileReader.close();

        // delete original file to make room
        // commented for now, for verification purpose.
        // file.delete();

        for (int i = 0; i < sortedFileNames.size();) {
            String sortedFilename1 = sortedFileNames.get(i++);
            String sortedFilename2 = sortedFileNames.get(i++);
            doMerge(sortedFilename1, sortedFilename2);
        }

        // *** repeat until there is only one sorted file ***

    }

    /**
     * Sorts and creates sorted file.
     *
     * @param sentences
     * @param sortedFilename
     * @param isAppend
     * @throws IOException - no error handling at this time.
     */
    private void saveSortedSentences(List<String> sentences, String sortedFilename, boolean isAppend)
            throws IOException {

        File sortedFile = new File(sortedFilename);
        FileWriter fileWriter = new FileWriter(sortedFile, isAppend);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (String sentence : sentences) {
            char[] chars = sentence.toCharArray();
            for (int i=0; i<chars.length; i++) {
                bufferedWriter.write((int)chars[i]);
            }
        }

        bufferedWriter.flush();
        bufferedWriter.close();
        fileWriter.close();

        sortedFileNames.add(sortedFilename);

    }

    private void doMerge(String filename1, String filename2) throws IOException {
        File f1 = new File(filename1);
        FileReader fr1 = new FileReader(f1);
        BufferedReader br1 = new BufferedReader(fr1);

        File f2 = new File(filename2);
        FileReader fr2 = new FileReader(f2);
        BufferedReader br2 = new BufferedReader(fr2);

        List<String> f1Sentences = new ArrayList<>();
        List<String> sortedSentences = new ArrayList<>();

        String mergedFilename = "./test/merged_"+(fileCount++)+".txt";

        int c1 = 0; int c2 = 0;
        StringBuffer buffer1 = new StringBuffer();
        StringBuffer buffer2 = new StringBuffer();
        // read entire first file
        while ((c1 = br1.read()) != -1) {
            char ch1 = (char) c1;
            buffer1.append(ch1);
            if (ch1 == ';') {
                f1Sentences.add(buffer1.toString());
                buffer1.setLength(0);
            }
        }

        while ((c2 = br2.read()) != -1) {
            char ch2 = (char)c2;
            buffer2.append(ch2);
            if (ch2 == ';') {
                String f2Sentence = buffer2.toString();
                for (String f1Sentence : f1Sentences) {
                    System.out.println("compare - " + f1Sentence + " - " + f2Sentence);
                    if (f2Sentence.compareTo(f1Sentence) > 1) {
                        sortedSentences.add(f1Sentence);
                    } else {
                        break;
                    }
                }
                buffer2.setLength(0);

                // remove already processed sentences read from first file
                for (String sentence : sortedSentences) {
                    System.out.println("deleting - " + sentence);
                    f1Sentences.remove(sentence);
                }

                f1Sentences.add(f2Sentence);
                Collections.sort(f1Sentences);

                mergeAndSave(sortedSentences, mergedFilename);
                sortedSentences.clear();
            }
        }

    }

    private void mergeAndSave(List<String> sentences, String mergedFilename) throws IOException {
        File mergedFile = new File(mergedFilename);
        saveSortedSentences(sentences, mergedFilename, (mergedFile.exists()) ? true : false);

        for (String sentence : sentences) {
            System.out.println("merge - " + sentence);
        }
    }

    /**
     * Driver for testing.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        // generate test file
        // String testFileName = "./test/nextgate.txt";
        String testFileName = generateTestFile();

        NextGateChallenge myChallenge = new NextGateChallenge();
        myChallenge.doSolve(testFileName);
    }

    /**
     * Generates test file.
     *
     * @return String filename
     * @throws IOException
     */
    private static String generateTestFile() throws IOException {
        String testFilename = "./test/nextgate.txt";
        File file = new File(testFilename);
        boolean isCreated = file.createNewFile();
        PrintWriter writer = new PrintWriter(testFilename);

        // generate random string
        int leftLimit = 97;     // numeral 'A'
        int rightLimit = 122;   // letter 'z'

        Random random = new Random();
        for (int i = 1; i<=100; i++) {
            String generatedString =
                    random.ints(leftLimit, rightLimit + 1)
                            .limit(random.nextInt(10)+1)
                            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                            .toString();
            writer.print(generatedString+";");
        }

        writer.flush();
        writer.close();

        return testFilename;
    }
}
