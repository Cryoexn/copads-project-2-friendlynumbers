//******************************************************************************
//
// File       : FriendlySeq.java
// Assignment : Project2
// Author     : David Pitoniak dhp6797@rit.edu
// Date       : 03/11/2021
//
// Description: Sequential Version of Friendly.
//
//******************************************************************************

import edu.rit.pj2.Task;
import java.util.*;

/**
 * Class FriendlySeq is a sequential program that finds the friendly numbers
 * between a start and a finish value. It pretty-prints the entries with the
 * longest value lists.
 *
 * Usage: java pj2 FriendlySeq start finish
 *
 * @author David Pitoniak
 */
public class FriendlySeq extends Task {

    /**
     * Takes a start and finish value from command line arguments
     * and prints the top friendly number entries and the number
     * of friendly numbers.
     *
     * @param args - start and finish values from commandline.
     */
    public void main(String[]args) {

        if(args.length != 2) {
            System.err.println("Usage: java pj2 FriendlySeq start-integer end-integer # 0 < start < end");
        } else {

            try {

                // Make sure range arguments are legal Integers.
                int start  = Integer.parseInt(args[0]);
                int finish = Integer.parseInt(args[1]);

                // Display the top friendly numbers for range start - finish.
                friendly(start, finish);

            } catch (NumberFormatException ex) {
                System.err.println("Usage: java pj2 FriendlySeq start-integer end-integer # 0 < start < end");
            }
        }

    } // end main.

    /**
     * Finds Top friendly values for a given range of numbers from start to finish.
     *
     * @param start  value to start range.
     * @param finish value to finish range.
     */
    private void friendly(int start, int finish) {

        // Map to store friendly numbers before map reduce.
        Map<int[], int[]> abundancesMap = initAbundances(start, finish);

        // Map to store reduced abundancesMap.
        Map<String, int[]> friendlyMap = new LinkedHashMap<>();

        // Reduce abundancesMap.
        for(Map.Entry<int[], int[]> pair : abundancesMap.entrySet()) {
            if(friendlyMap.containsKey(Arrays.toString(pair.getKey()))) {
                friendlyMap.put(Arrays.toString(pair.getKey()), concatArrays(friendlyMap.get(Arrays.toString(pair.getKey())), pair.getValue()));
            } else {
                friendlyMap.put(Arrays.toString(pair.getKey()), pair.getValue());
            }
        }

        // Get the keys with the largest number of elements in its value array.
        ArrayList<Map.Entry<String, int[]>> greatest = getTopFriendlyEntries(friendlyMap);

        // Display the top results and the number of friendly numbers.
        System.out.print(prettify(greatest));
        System.out.println(countFriendly(friendlyMap) + " friendly numbers.");

    } // end friendly.

    /**
     * Creates a map of all abundances between start and finish values.
     *
     * @param start  int value to start range.
     * @param finish int value to finish range.
     *
     * @return - LinkedHashMap of abundances.
     */
    private Map<int[], int[]> initAbundances(int start, int finish) {

        // Map to store key - (numerator, denominator), value - (value).
        Map<int [], int []> abundancesMap = new LinkedHashMap<>();

        // Create array that contains the range of numbers
        // from start to finish.
        int[] values = new int[finish - start];

        // Fill array with values between start and finish.
        for(int i = 0; i + start < finish; i++) {
            values[i] = start + i;
        }

        for (int val : values) {

            // List of divisors for val.
            ArrayList<Integer> divs = getDivisors(val);

            // Array to hold ratio.
            int [] ratio = new int[2];

            int sum = sum(divs);
            int gcd = gcd(sum, val);

            ratio[0] = sum/gcd;
            ratio[1] = val/gcd;

            abundancesMap.put(ratio, new int[] {val});
        }

        return abundancesMap;

    } // end initAbundances.

    /**
     * Find the longest list of values in the map and create
     * a list of all entries with this length of value array.
     *
     * @param map Map to get top entries from.
     *
     * @return Sorted list of top entries.
     */
    private ArrayList<Map.Entry<String,int[]>> getTopFriendlyEntries(Map<String, int[]> map) {

        // List for the top entries in map.
        ArrayList<Map.Entry<String, int[]>> greatest = new ArrayList<>();

        // Current most values in array.
        int most = 0;

        for(Map.Entry<String, int[]> pair : map.entrySet()) {

            // If a new most is found, change the current most, and clear current list.
            if(pair.getValue().length > most) {
                most = pair.getValue().length;
                greatest.clear();
            }

            // If the value length is the same as the most add it to list.
            if(pair.getValue().length == most && most != 1) {
                greatest.add(pair);
            }
        }

        sortFriendly(greatest);

        return greatest;

    } // end getTopFriendlyEntries.

    /**
     * Concatenate a2 to a1.
     *
     * @param a1 First array.
     * @param a2 Second array.
     *
     * @return Concatenation of a1 and a2.
     */
    private int[] concatArrays(int[] a1, int[] a2) {

        // new array large enough to store values from a1 and a2.
        int [] val = new int[(a1).length + (a2).length];

        System.arraycopy(a1, 0, val, 0, a1.length);
        System.arraycopy(a2, 0, val, a1.length, a2.length);

        return val;

    } // end concatArrays.

    /**
     * Sorts the friendly list value arrays in increasing order.
     * Sorts the friendly list based on the numerator denominator key.
     *
     * @param friendly List to sort based on the Map.Entry key - (numerator/denominator).
     */
    private void sortFriendly(ArrayList<Map.Entry<String, int[]>> friendly) {

        for(Map.Entry<String, int[]> e : friendly) {
            Arrays.sort(e.getValue());
        }

        friendly.sort((o1, o2) -> {
            String key1 = o1.getKey().replace("[", "").replace("]", "");
            String key2 = o2.getKey().replace("[", "").replace("]", "");

            String[] vals1 = key1.split(",");
            String[] vals2 = key2.split(",");

            return  Double.compare(Double.parseDouble(vals1[0].strip()) / Double.parseDouble(vals1[1].strip()),
                    Double.parseDouble(vals2[0].strip()) / Double.parseDouble(vals2[1].strip()));
        });

    } // end sortFriendly.

    /**
     * Counts the total number of friendly numbers in a Map.
     *
     * @param numbers Map of friendly numbers.
     *
     * @return Total count of each entry value length.
     */
    private int countFriendly(Map<String, int[]> numbers) {

        // Number of friendly numbers.
        int count = 0;

        for(Map.Entry<String, int[]> e : numbers.entrySet()) {
            if(e.getValue().length > 1) {
                count += e.getValue().length;
            }
        }

        return count;

    } // end countFriendly.

    /**
     * Get the greatest common divisor of num1 and num2.
     *
     * @param num1 first number.
     * @param num2 second number.
     *
     * @return Greatest common divisor of num1 and num2.
     */
    private int gcd(int num1, int num2) {

        if (num2 == 0)
            return num1;
        else
            return gcd(num2, (num1 % num2));

    } // end gcd.

    /**
     * Calculates the sum of all values in list.
     *
     * @param numbers List of numbers to add together.
     *
     * @return Total of all values in numbers.
     */
    private int sum(ArrayList<Integer> numbers) {

        return numbers.stream().mapToInt(Integer::intValue).sum();

    } // end sum.

    /**
     * Get sorted list of divisors for an int value.
     *
     * @param val Integer to get list of divisors for.
     *
     * @return Sorted list of divisors for val.
     */
    private ArrayList<Integer> getDivisors(int val) {

        // List of divisors.
        ArrayList<Integer> divisors = new ArrayList<>();

        for (int i=1; i<=Math.sqrt(val); i++)
        {
            if ((val % i) == 0)
            {
                // If divisors are equal
                if ((val / i) == i) {
                    divisors.add(i);
                } else { // Otherwise
                    divisors.add(i);
                    divisors.add(val / i);
                }
            }
        }

        divisors.sort(null);

        return divisors;

    } // end getDivisors.

    /**
     * Formats a list of friendly number entries to acceptable String for display.
     *
     * @param entries Top entries of friendly numbers to format.
     *
     * @return formatted String of entries.
     */
    private String prettify(ArrayList<Map.Entry<String, int[]>> entries) {

        // String to represent entries.
        StringBuilder string = new StringBuilder();

        // Iterator to iterate over entries.
        Iterator<Map.Entry<String, int[]>> itr = entries.iterator();

        // Temporary Map entry to store iterations of entries.
        Map.Entry<String, int[]> e = null;

        while(itr.hasNext()) {

            e = itr.next();
            string.append("{(");
            assert e != null;
            String[] values = e.getKey().replace("[", "").replace("]", "").split(",");
            string.append(values[0]);
            string.append("/");
            string.append(values[1].strip());
            string.append("): ");
            string.append(Arrays.toString(e.getValue()));
            string.append("}\n");
        }

        return string.toString();

    } // end prettify.

} // end FriendlySeq.
