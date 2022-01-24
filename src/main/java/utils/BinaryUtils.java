package main.java.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BinaryUtils {

    private String zeroBit = "0";
    private String oneBit = "1";

    public Pair<Integer, Integer> crossParents(int number1, int number2, int minBit, int maxBit){
        String parent1 = Integer.toBinaryString(number1);
        String parent2 = Integer.toBinaryString(number2);

        String childer1;
        String childer2;

        int begin = 16 - maxBit; // 0000111001101010   dla minBit = 3, maxBit = 6
        int end = 16 - minBit;

        while(parent1.length() < 16){
            parent1 = zeroBit.concat(parent1);
        }
        while (parent2.length() < 16){
            parent2 = zeroBit.concat(parent2);
        }

        String slice1 = parent1.substring(begin, end);
        String slice2 = parent2.substring(begin, end);

        childer1 = parent1.substring(0, begin) + slice2 + parent1.substring(begin + slice1.length());
        childer2 = parent2.substring(0, begin) + slice1 + parent2.substring(begin + slice2.length());

        return Pair.createPair(Integer.parseInt(childer1, 2), Integer.parseInt(childer2, 2));
    }

    public Integer positionMutation(int number, int position){
        String coordinate = Integer.toBinaryString(number);
        while (coordinate.length() < 16){
            coordinate = zeroBit.concat(coordinate);
        }
        int pos = 16 - position;
        String numberToChange = coordinate.substring(pos - 1, pos);
        numberToChange = zeroBit.equals(numberToChange) ? oneBit : zeroBit;
        String changed = coordinate.substring(0, pos - 1 ) + numberToChange + coordinate.substring(pos);
        return Integer.parseInt(changed, 2);
    }

}
