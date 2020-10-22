public static class HackerRank {

    public static int longestSequence(int[] badNumbers, int[] sequence) {
        Arrays.sort(badNumbers);

        int max = 0;
        int length = 0;
        int start = sequence[0];

        int i = 0;
        int j = 0;
        for (; i < sequence.length; ) {
            if (sequence[i] == badNumbers[j]) {
                length = sequence[i] - start;
                if (length > max) {
                    max = length;
                }
                i++;
                j++;
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