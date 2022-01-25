package main.java.utils;

import lombok.experimental.UtilityClass;
import main.java.Banner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class BinaryUtils {

    private String zeroBit = "0";
    private String oneBit = "1";

    public List<Pair<Integer, Integer>> randomMinMaxFromParents(Banner parent1, Banner parent2){
        String xParent1 = Integer.toBinaryString(parent1.getX());
        String xParent2 = Integer.toBinaryString(parent2.getX());

        String yParent1 = Integer.toBinaryString(parent1.getY());
        String yParent2 = Integer.toBinaryString(parent2.getY());

        int x1 = ThreadLocalRandom.current().nextInt(0, Math.min(xParent1.length(), xParent2.length()) + 1);
        int x2 = ThreadLocalRandom.current().nextInt(0, Math.min(xParent1.length(), xParent2.length()) + 1);

        int y1 = ThreadLocalRandom.current().nextInt(0, Math.min(yParent1.length(), yParent2.length()) + 1);
        int y2 = ThreadLocalRandom.current().nextInt(0, Math.min(yParent1.length(), yParent2.length()) + 1);

        int xMin = Math.min(x1, x2);
        int xMax = Math.max(x1, x2);

        int yMin = Math.min(y1, y2);
        int yMax = Math.max(y1, y2);

        Pair<Integer, Integer> x_coordinates = crossParents(parent1.getX(), parent2.getX(), xMin, xMax);
        Pair<Integer, Integer> y_coordinates = crossParents(parent1.getY(), parent2.getY(), yMin, yMax);

        return Arrays.asList(x_coordinates, y_coordinates);
    }

    public List<Pair<Integer, Integer>> randomMinMaxFromParents2(Banner parent1, Banner parent2){
        String xParent1 = Integer.toBinaryString(parent1.getX());
        String xParent2 = Integer.toBinaryString(parent2.getX());

        String yParent1 = Integer.toBinaryString(parent1.getY());
        String yParent2 = Integer.toBinaryString(parent2.getY());

        int x1 = ThreadLocalRandom.current().nextInt(0, Math.min(xParent1.length(), xParent2.length()) + 1);
        int x2 = ThreadLocalRandom.current().nextInt(0, Math.min(xParent1.length(), xParent2.length()) + 1);

        int y1 = ThreadLocalRandom.current().nextInt(0, Math.min(yParent1.length(), yParent2.length()) + 1);
        int y2 = ThreadLocalRandom.current().nextInt(0, Math.min(yParent1.length(), yParent2.length()) + 1);

        int xMin = Math.min(0, x2);
        int xMax = Math.max(x1, x2);

        int yMin = Math.min(0, y2);
        int yMax = Math.max(y1, y2);

        Pair<Integer, Integer> x_coordinates = crossParents(parent1.getX(), parent2.getY(), xMin, xMax);
        Pair<Integer, Integer> y_coordinates = crossParents(parent1.getY(), parent2.getX(), yMin, yMax);

        return Arrays.asList(x_coordinates, y_coordinates);
    }

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
