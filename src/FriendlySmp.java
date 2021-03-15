
//******************************************************************************
//
// File       : FriendlySmp.java
// Assignment : Project2
// Author     : David Pitoniak dhp6797@rit.edu
// Date       : 03/11/2021
//
// Description:
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

    public void main(String[]args) {
        if(args.length != 2) {
            System.out.println("Usage: java pj2 FriendlySmp start-integer end-integer # 0 < start < end");
        } else {
            friendly(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }
    }

    private void friendly(int start, int finish) {

        int[] values = new int[(finish - start) + 1];

        for(int i = 0; i + start < finish + 1; i++) {
            values[i] = start + i;
        }

        Map<int[], int[]> abundancesList = new LinkedHashMap<>();
        ArrayList<Integer> divs;

        for (int val : values) {
            divs = getDivisors(val);

            int [] arr = new int[2];

            int sum = sum(divs);
            int gcd = gcd(sum, val);

            arr[0] = sum/gcd;
            arr[1] = val/gcd;

            abundancesList.put(arr, new int[] {val});
        }

        // ********************************************** Parallelize **********************************************

        Map<String, int[]> friendlyList = new LinkedHashMap<>();

        ArrayList<int[]> keyList = new ArrayList<>(abundancesList.keySet());
        ArrayList<int[]> valueList = new ArrayList<>(abundancesList.values());

        parallelFor(0, abundancesList.size() - 1).exec(new Loop(){

            @Override
            public void run(int i) throws Exception {

                if(friendlyList.containsKey(Arrays.toString(keyList.get(i)))) {
                    friendlyList.put(Arrays.toString(keyList.get(i)), concatArrays(friendlyList.get(Arrays.toString(keyList.get(i))), valueList.get(i)));
                } else {
                    friendlyList.put(Arrays.toString(keyList.get(i)), valueList.get(i));
                }
            }
        });

        ArrayList<Map.Entry<String, int[]>> greatest = new ArrayList<>();
        int count = countFriendlyNums(friendlyList);
        int most = 0;

                for(Map.Entry<String, int[]> pair : friendlyList.entrySet()) {
        if(pair.getValue().length > most) {
            most = pair.getValue().length;
        }
    }

        // ********************************************** Parallelize **********************************************

        for(Map.Entry<String, int[]> pair : friendlyList.entrySet()) {
            if(pair.getValue().length == most && most != 1) {
                greatest.add(pair);
            }
        }

        sortFriendly(greatest);

        System.out.print(prettify(greatest));
        System.out.println(count + " friendly numbers.");
    }

    private void sortFriendly(ArrayList<Map.Entry<String, int[]>> friendly) {

        for(Map.Entry<String, int[]> e : friendly) {
            Arrays.sort(e.getValue());
        }

        friendly.sort((o1, o2) -> {
            String key1 = o1.getKey().replace("[", "").replace("]", "");
            String key2 = o2.getKey().replace("[", "").replace("]", "");

            String[] vals1 = key1.split(",");
            String[] vals2 = key2.split(",");

            return Double.compare(Double.parseDouble(vals1[0].strip()) / Double.parseDouble(vals1[1].strip()), Double.parseDouble(vals2[0].strip()) / Double.parseDouble(vals2[1].strip()));
        });
    }

    private int countFriendlyNums(Map<String, int[]> nums) {
        int count = 0;

        for(Map.Entry<String, int[]> e : nums.entrySet()) {
            if(e.getValue().length >= 2) {
                count += e.getValue().length;
            }
        }

        return count;
    }

    private int gcd(int num1, int num2) {
        if (num2 == 0)
            return num1;
        else
            return gcd(num2, num1%num2);
    }

    private int[] concatArrays(int[] a1, int[] a2) {
        int [] val = new int[a1.length + a2.length];

        System.arraycopy(a1, 0, val, 0, a1.length);
        System.arraycopy(a2, 0, val, a1.length, a2.length);

        return val;
    }

    private int sum(ArrayList<Integer> nums) {
        return nums.stream().mapToInt(Integer::intValue).sum();
    }

    private ArrayList<Integer> getDivisors(int val) {
        ArrayList<Integer> divisors = new ArrayList<>();

        for (int i=1; i<=Math.sqrt(val); i++)
        {
            if (val%i==0)
            {
                // If divisors are equal, print only one
                if (val/i == i) {
                    divisors.add(i);

                } else { // Otherwise print both
                    divisors.add(i);
                    divisors.add(val / i);
                }
            }
        }

        divisors.sort(null);

        return divisors;
    }

    private String prettify(ArrayList<Map.Entry<String, int[]>> entries) {
        StringBuilder string = new StringBuilder();

        Iterator<Map.Entry<String, int[]>> itr = entries.iterator();
        Map.Entry<String, int[]> e = null;

        while(itr.hasNext()) {

            e = itr.next();
            string.append("{(");
            assert e != null;
            String[] vals = e.getKey().replace("[", "").replace("]", "").split(",");
            string.append(vals[0]);
            string.append("/");
            string.append(vals[1].strip());
            string.append("): ");
            string.append(Arrays.toString(e.getValue()));
            string.append("}\n");
        }

        return string.toString();
    }
}
