package main.java;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.java.enums.SideType;
import main.java.utils.Pair;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Banner {

    private int A[] = {120, 160, 200, 240, 280};
    private int B[] = {120, 160, 200};

    private int x;
    private int y;
    private int width;
    private int hight;
    private int coveredSurface = 0;

    private double f_i;

    public List<Pair<Integer, Integer>> sidePointList(SideType sideType){
        List<Pair<Integer, Integer>> iteratedPoints = new ArrayList<>();
        switch (sideType){
            case UPPER -> {
                for(int i = x; i<x+width; i++) {
                    Pair<Integer, Integer> point = Pair.createPair(i, y);
                    iteratedPoints.add(point);
                }
            }
            case LOWER -> {
                for(int i = x; i<x+width; i++) {
                    Pair<Integer, Integer> point = Pair.createPair(i, y-hight);
                    iteratedPoints.add(point);
                }
            }
            case LEFT -> {
                for(int i = y-hight; i<y; i++){
                    Pair<Integer, Integer> point = Pair.createPair(x, i);
                    iteratedPoints.add(point);
                }
            }
            case RIGHT -> {
                for(int i = y-hight; i<y; i++){
                    Pair<Integer, Integer> point = Pair.createPair(x+width, i);
                    iteratedPoints.add(point);
                }
            }
        }
        return iteratedPoints;
    }

    public void printCoorinates(){
        List<Integer> punktyXDoRysowania = new ArrayList<>();
        List<Integer> punktyYDoRysowania = new ArrayList<>();
        for(int i = x; i<x+width; i++) {
            punktyXDoRysowania.add(i);
            punktyYDoRysowania.add(y);
        }
        for(int i = x; i<x+width; i++) {
            punktyXDoRysowania.add(i);
            punktyYDoRysowania.add(y-hight);
        }
        for(int i = y-hight; i<y; i++){
            punktyXDoRysowania.add(x);
            punktyYDoRysowania.add(i);
        }
        for(int i = y-hight; i<y; i++){
            punktyXDoRysowania.add(x+width);
            punktyYDoRysowania.add(i);
        }
        for(int j = 0; j < punktyXDoRysowania.size(); j++)
            System.out.println(punktyXDoRysowania.get(j) + "\t" + punktyYDoRysowania.get(j));
    }

    public List<Integer> xToFile(){
        List<Integer> punktyXDoRysowania = new ArrayList<>();
        for(int i = x; i<x+width; i++) {
            punktyXDoRysowania.add(i);
        }
        for(int i = x; i<x+width; i++) {
            punktyXDoRysowania.add(i);
        }
        for(int i = y-hight; i<y; i++){
            punktyXDoRysowania.add(x);
        }
        for(int i = y-hight; i<y; i++){
            punktyXDoRysowania.add(x+width);
        }
        return punktyXDoRysowania;
    }

    public List<Integer> yToFile(){
        List<Integer> punktyYDoRysowania = new ArrayList<>();
        for(int i = x; i<x+width; i++) {
            punktyYDoRysowania.add(y);
        }
        for(int i = x; i<x+width; i++) {
            punktyYDoRysowania.add(y-hight);
        }
        for(int i = y-hight; i<y; i++){
            punktyYDoRysowania.add(i);
        }
        for(int i = y-hight; i<y; i++){
            punktyYDoRysowania.add(i);
        }
        return punktyYDoRysowania;
    }
}
