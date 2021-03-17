
//******************************************************************************
//
// File       : FriendlySmp.java
// Assignment : Project2
// Author     : David Pitoniak dhp6797@rit.edu
// Date       : 03/11/2021
//
// Description: Symmetric Multiprocessing Version of Friendly.
//
//******************************************************************************

import edu.rit.pj2.Loop;
import edu.rit.pj2.Task;

import java.util.*;

/**
 * Class FriendlySmp is an SMP parallel program that finds the friendly numbers
 * between a start and a finish value. It pretty-prints the entries with the
 * longest value lists.
 *
 * Usage: java pj2 FriendlySmp start finish
 *
 * @author David Pitoniak
 */
public class FriendlySmp extends Task {

    /**
     * Takes a start and finish value from command line arguments
     * and prints the top friendly number entries and the number
     * of friendly numbers.
     *
     * @param args - start and finish values from commandline.
     */
    public void main(String[]args) {

        if(args.length != 2) {
            System.err.println("Usage: java pj2 FriendlySmp start-integer end-integer # 0 < start < end");
        } else {

            try {

                // Make sure arguments are legal Integers.
                int start  = Integer.parseInt(args[0]);
                int finish = Integer.parseInt(args[1]);

                // Display the top friendly numbers for range start - finish.
                friendly(start, finish);

            } catch (NumberFormatException ex) {
                System.err.println();
            }
        }

    } // end main.

    /**
     * Creates a map of all abundances between start and finish values.
     * Uses parallelization for distributing computation for multiple cores.
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
        int[] values = new int[(finish - start) + 1];

        // Fill array with values between start and finish.
        for(int i = 0; i + start < finish + 1; i++) {
            values[i] = start + i;
        }

        // Parallelization of initializing abundancesMap.
        parallelFor(0,  values.length - 1).exec( new Loop() {
            ArrayList<Integer> divs = new ArrayList<>();

            @Override
            public void run(int i) throws Exception {
                divs = getDivisors(values[i]);
                int [] arr = new int[2];

                int sum = sum(divs);
                int gcd = gcd(sum, values[i]);

                arr[0] = sum / gcd;
                arr[1] = values[i] / gcd;

                addEntryToMap(abundancesMap, arr, new int[] {values[i]});
            }
        });

        return abundancesMap;

    } // end initAbundances.

    /**
     * Thread safe method to add a new entry to abundances Map.
     *
     * @param map    Map to add key value pair to.
     * @param key    Key at which to put value.
     * @param values Value to put at key.
     */
    private synchronized void addEntryToMap(Map<int[], int[]> map, int[] key, int[] values) {

        // Add key value pair into map.
        map.put(key, values);

    } // end addEntryToMap.

    /**
     * Finds Top friendly values for a given range of numbers from start to finish.
     * Reduces the Map of friendly numbers with parallelization.
     *
     * @param start  int value to start range.
     * @param finish int value to finish range.
     */
    private void friendly(int start, int finish) {

        // Initial Map of values from start to finish.
        Map<int[], int[]> abundancesMap = initAbundances(start, finish);

        // Make accumulation variable for Map reduction.
        Map<String, int[]> friendlyMap = new LinkedHashMap<>();

        // Get lists of keys and values from non reduced Map.
        ArrayList<int[]> keyList = new ArrayList<>(abundancesMap.keySet());
        ArrayList<int[]> valueList = new ArrayList<>(abundancesMap.values());

        // Parallelization for Map Reduction.
        parallelFor(0, abundancesMap.size() - 1).exec(new Loop(){
            @Override
            public void run(int i) throws Exception {
                if(mapContainsKey(friendlyMap, keyList.get(i))) {
                    addExistingMapEntry(friendlyMap, keyList.get(i), valueList.get(i));
                } else {
                    addNewMapEntry(friendlyMap, keyList.get(i), valueList.get(i));
                }
            }
        });

        // List of entries that have the greatest number of values in value array.
        ArrayList<Map.Entry<String, int[]>> topFriendlyEntries = getTopFriendlyEntries(friendlyMap);

        // Display top entries and total number of friendly numbers.
        System.out.print(prettify(topFriendlyEntries));
        System.out.println(countFriendly(friendlyMap) + " friendly numbers.");

    } // end friendly.

    /**
     * Thread safe method to add an existing entry to abundances Map during parallel Map Reduction.
     *
     * @param map   Map to add key value pair to
     * @param key   Key to put value at in list.
     * @param value Value to put at key in map.
     */
    private synchronized void addExistingMapEntry(Map<String, int[]> map, int[] key, int[] value) {

        // Insert the value array concatenated to existing value in map.
        map.put(Arrays.toString(key), concatArrays(map.get(Arrays.toString(key)), value));

    } // end addExistingMapEntry.

    /**
     * Concatenate a2 to a1.
     *
     * @param a1 First array.
     * @param a2 Second array.
     *
     * @return Concatenation of a1 and a2.
     */
    private int[] concatArrays(int[] a1, int[] a2) {

        int [] val = new int[((int[])a1).length + ((int[])a2).length];

        System.arraycopy(a1, 0, val, 0, a1.length);
        System.arraycopy(a2, 0, val, a1.length, a2.length);

        return val;

    } // end concatArrays.

    /**
     * Thread safe method to add a new entry to abundances Map during parallel Map Reduction.
     *
     * @param map   Map to add new entry to.
     * @param key   Key at which to put value.
     * @param value Value to be put at key.
     */
    private synchronized void addNewMapEntry(Map<String, int[]> map, int[] key, int[] value) {

        map.put(Arrays.toString(key),value);

    } // end addNewMapEntry.

    /**
     * Thread safe method to check if map contains a key during parallelization.
     *
     * @param map Map to check for key.
     * @param key Key to check for in map.
     *
     * @return If map contains key.
     */
    private synchronized boolean mapContainsKey(Map<String, int[]> map, int[] key) {

        // If the key is in map.
        return map.containsKey(Arrays.toString(key));

    } // end mapContainsKey.

    /**
     * Find the longest list of values in the map and create
     * a list of all entries with this length of value array.
     *
     * @param map Map to get top entries from.
     *
     * @return Sorted list of top entries.
     */
    private ArrayList<Map.Entry<String,int[]>> getTopFriendlyEntries(Map<String, int[]> map) {

        ArrayList<Map.Entry<String, int[]>> greatest = new ArrayList<>();
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

        int count = 0;

        for(Map.Entry<String, int[]> e : numbers.entrySet()) {
            if(e.getValue().length >= 2) {
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
        ArrayList<Integer> divisors = new ArrayList<>();

        for (int i=1; i <= Math.sqrt(val); i++)
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
        StringBuilder string = new StringBuilder();

        Iterator<Map.Entry<String, int[]>> itr = entries.iterator();
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
