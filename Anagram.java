import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Anagram {

    // Complete the anagram function below.
    static int anagram(String s) {
        // if s is odd length, not possible
        if (s.length() % 2 != 0) return -1;

        // split s into 2
        int count=0;
        String s1 = s.substring(0,s.length()/2);
        String s2 = s.substring(s.length()/2);

        char[] s1chars = s1.toCharArray();
        int s1len = s1chars.length;
        for (int i=0; i<s1len; i++) {
            int idx = s2.indexOf(s1chars[i]);
            if (idx == -1) {
                count++;
            } else {
                s2 = s2.substring(0,idx)+s2.substring(idx+1);
            }
        }
        return count;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int q = scanner.nextInt();
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int qItr = 0; qItr < q; qItr++) {
            String s = scanner.nextLine();

            int result = anagram(s);

            bufferedWriter.write(String.valueOf(result));
            bufferedWriter.newLine();
        }

        bufferedWriter.close();

        scanner.close();
    }
}