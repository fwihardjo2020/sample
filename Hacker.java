import java.util.Arrays;

public class Hacker {

    // Maximum Number of Vowels in a Substring of Given Length
    // https://leetcode.com/submissions/detail/411966572/
    public static int maxVowels(String s, int k) {
        char[] chars = s.toCharArray();

        int count = 0;
        // start window
        String sub = s.substring(0,k);
        for (int i=0; i<sub.length(); i++) {
            if (sub.charAt(i)=='a'
                    || sub.charAt(i)=='i'
                    || sub.charAt(i)=='e'
                    || sub.charAt(i)=='o'
                    || sub.charAt(i)=='u')
                count++;
        }

        // slide window
        int max = count;
        int start = 0;
        for (int i=k; i<s.length(); i++, start++) {
            if (s.charAt(i)=='a'
                    || s.charAt(i)=='i'
                    || s.charAt(i)=='e'
                    || s.charAt(i)=='o'
                    || s.charAt(i)=='u')
                count++;
            if (s.charAt(start)=='a'
                    || s.charAt(start)=='i'
                    || s.charAt(start)=='e'
                    || s.charAt(start)=='o'
                    || s.charAt(start)=='u')
                count--;
            if (count > max) {
                max = count;
                sub = s.substring(start,i);
            }
        }

        return max;
    }

    public static int longestSequence(int[] badNumbers, int[] sequence) {
        Arrays.sort(badNumbers);

        int max = 0;
        int length = 0;
        int start = sequence[0];

        int i=0;
        int j=0;
        for (; i<sequence.length;) {
            if (sequence[i] == badNumbers[j]) {
                length = sequence[i]-start;
                if (length > max) {
                    max = length;
                }
                i++; j++;
                if (i == sequence.length) break;
                if (j == badNumbers.length) break;
                start = sequence[i];
            } else {
                if (sequence[i] < badNumbers[j]) i++;
                else if (sequence[i] > badNumbers[j]) j++;
            }
        }
        if (i == sequence.length) {
            length = (sequence[i - 1] - start);
        }
        if (length > max) max = length;
        return max;
    }

}
