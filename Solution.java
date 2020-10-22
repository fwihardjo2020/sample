// Maximum Number of Vowels in a Substring of Given Length
// https://leetcode.com/submissions/detail/411966572/ 
	class Solution {
        public int maxVowels(String s, int k) {
            char[] chars = s.toCharArray();

            int count = 0;
            // start window
            String sub = s.substring(0,k);
            //System.out.println("--"+sub);
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
                //System.out.println(s.substring(start,i));
            }

            //System.out.println("--"+sub);
            return max;
        }
    }