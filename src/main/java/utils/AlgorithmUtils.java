package main.java.utils;

import lombok.Data;
import lombok.experimental.UtilityClass;
import main.java.Banner;
import main.java.Figure;
import main.java.enums.CornerType;
import main.java.enums.SideType;
import main.java.impl.Building;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
@UtilityClass
public class AlgorithmUtils {

    public List<Banner> rouletteWheelSelection(List<Banner> population) {
        List<Banner> nextPopulationSelection = new ArrayList<>();
        final double sumF_i = population.stream().mapToDouble(Banner::getF_i).sum();
        for (int i = 0; i < population.size(); i++) {
            double random = Math.random();
            double rulete = 0.0;
            boolean result = true;
            while (result) {
                for (Banner banner : population) {
                    rulete += (banner.getF_i() / sumF_i);
                    if (rulete >= random) {
                        nextPopulationSelection.add(new Banner(banner.getX(), banner.getY(), banner.getWidth(), banner.getHight()));
                        result = false;
                        break;
                    }
                }
            }
        }
        return nextPopulationSelection;
    }

    public List<Banner> rankingSelection(List<Banner> population) {
        population.sort(Comparator.comparing(Banner::getF_i).reversed());
        final List<Banner> top50pop = population.subList(0, population.size()/2);
        List<Banner> notTop50pop = population.subList(population.size()/2, population.size());
        List<Banner> finalNotTop50pop = notTop50pop;
        notTop50pop.forEach(banner -> {
            banner.setX(top50pop.get(finalNotTop50pop.indexOf(banner)).getX());
            banner.setY(top50pop.get(finalNotTop50pop.indexOf(banner)).getY());
        });

        notTop50pop = crucifixion(notTop50pop);
        notTop50pop = mutation(notTop50pop);

        List<Banner> nextPopulation = new ArrayList<>();
        nextPopulation.addAll(top50pop);
        nextPopulation.addAll(notTop50pop);

        nextPopulation.forEach(e -> {
            e.setF_i(0.0);
            e.setCoveredSurface(0);
        });

        return nextPopulation;
    }

    public List<Banner> rouletteWheelSelectionScaled(List<Banner> population) {
        List<Banner> nextPopulationSelection = new ArrayList<>();
        final double sumF_i = population.stream().mapToDouble(Banner::getF_i).sum();
        while (nextPopulationSelection.size() < 50) {
            double rulete = 0.0;
            boolean result = true;
            while (result) {
                double random = Math.random();
                for (Banner banner : population) {
                    rulete += (banner.getF_i() / sumF_i);
                    if (rulete >= random) {
                        nextPopulationSelection.add(new Banner(banner.getX(), banner.getY(), banner.getWidth(), banner.getHight()));
                        result = false;
                        break;
                    }
                }
                rulete = 0;
            }
        }
        return nextPopulationSelection;
    }

    public List<Banner> crucifixion(List<Banner> selectedPopulation) {
        List<Banner> nextPopulationCrucifixion = new ArrayList<>();
        Collections.shuffle(selectedPopulation);
        for (int i = 0; i < selectedPopulation.size() / 2; i++) {
            Banner parent1 = selectedPopulation.get(i);
            Banner parent2 = selectedPopulation.get(selectedPopulation.size() - i - 1);

            Pair<Integer, Integer> x_coordinates = BinaryUtils.crossParents(parent1.getX(), parent2.getX(), 6, 11);
            Pair<Integer, Integer> y_coordinates = BinaryUtils.crossParents(parent1.getY(), parent2.getY(), 6, 11);
            parent1.setX(x_coordinates.getLeft());
            parent2.setX(x_coordinates.getRight());
            parent1.setY(y_coordinates.getLeft());
            parent2.setY(y_coordinates.getRight());

            nextPopulationCrucifixion.add(parent1);
            nextPopulationCrucifixion.add(parent2);
        }

        return nextPopulationCrucifixion;
    }

    public List<Banner> mutation(List<Banner> crucifixedPopulation) {
        List<Banner> nextGenerationPopulation = new ArrayList<>();
        crucifixedPopulation.forEach(banner -> {
            final double randomX = Math.random();
            final double randomY = Math.random();
            if (randomX <= 0.2) {
                final int randomPosition = ThreadLocalRandom.current().nextInt(0, Integer.toBinaryString(banner.getX()).length());
                banner.setX(BinaryUtils.positionMutation(banner.getX(), randomPosition));
            }
            if (randomY <= 0.2) {
                final int randomPosition = ThreadLocalRandom.current().nextInt(0, Integer.toBinaryString(banner.getY()).length());
                banner.setY(BinaryUtils.positionMutation(banner.getY(), randomPosition));
            }
            nextGenerationPopulation.add(banner);
        });

        return nextGenerationPopulation;
    }

    public void calculateAdaptation(List<Banner> population, List<Figure> elementsOfBuilding, Banner current) {
        List<Banner> coveredBanners = population.stream()
                .filter(e -> !e.equals(current))
                .collect(Collectors.toList());

        coveredBanners.forEach(otherBanner -> {
            boolean corners = checkCorners(current, otherBanner);
            if (!corners) {
                boolean sides = checkSides(current, otherBanner);
                if (!sides) {
                    boolean complete = checkIfFullCovered(current, otherBanner);
                }
            }
        });
        elementsOfBuilding.forEach(element -> {
            boolean corners = checkCorners(current, element);
            if (!corners) {
                boolean sides = checkSides(current, element);
                if (!sides) {
                    boolean complete = checkIfFullCovered(current, element);
                }
            }
        });
        addSurfaceFromCrossingBuilding(current);
        calculateFi(current);
    }

    private void calculateFi(Banner banner) {
        banner.setF_i(1.0 -
                ((double) banner.getCoveredSurface() / (double) (banner.getCoveredSurface() + banner.getSurface())));
    }

    public double argFi(List<Banner> population) {
        return population.stream().mapToDouble(Banner::getF_i).sum() / (double) population.size();
    }

    private void addSurfaceFromCrossingBuilding(Banner current) {
        Building building = new Building();
        int width = 0;
        int height = 0;

        if (current.getX() + current.getWidth() > building.getWidth() && current.getY() - current.getHight() >= 0) { //side right
            width = (current.getX() + current.getWidth()) - building.getWidth();
            height = current.getHight();
            current.setCoveredSurface(current.getCoveredSurface() + width * height);
        }
        if (current.getX() + current.getWidth() > building.getWidth() && current.getY() - current.getHight() < 0) { //lower right corner
            width = building.getWidth() - current.getX();
            height = current.getY();
            int surface = (current.getWidth() * current.getHight()) - (width * height);
            current.setCoveredSurface(current.getCoveredSurface() + surface);
        }
        if (current.getX() + current.getWidth() < building.getWidth() && current.getY() - current.getHight() < 0) { //side lower
            width = building.getWidth();
            height = Math.abs(current.getY() - current.getHight());
            current.setCoveredSurface(current.getCoveredSurface() + width * height);
        }
        if ((current.getX() >= building.getWidth()) || (current.getY() <= 0) || (current.getX() <= 0))
            current.setCoveredSurface(current.getCoveredSurface() + current.getWidth() * current.getHight());

        if (current.getY() >= building.getHight()) {
            width = current.getWidth();
            height = Math.abs(current.getY() - building.getHight());
            current.setCoveredSurface(current.getCoveredSurface() + width * height);
        }
    }


    private boolean checkCorners(Banner current, Banner potencialCover) {
        boolean upperLeftCorner = checkIfCovering(potencialCover.getX() + potencialCover.getWidth(), potencialCover.getY() - potencialCover.getHight(), current);
        if (upperLeftCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.UPPER_LEFT, current, potencialCover));
            return true;
        }
        boolean lowerLeftCorner = checkIfCovering(potencialCover.getX() + potencialCover.getWidth(), potencialCover.getY(), current);
        if (lowerLeftCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.LOWER_LEFT, current, potencialCover));
            return true;
        }
        boolean upperRightCorner = checkIfCovering(potencialCover.getX(), potencialCover.getY() - potencialCover.getHight(), current);
        if (upperRightCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.UPPER_RIGHT, current, potencialCover));
            return true;
        }
        boolean lowerRightCorner = checkIfCovering(potencialCover.getX(), potencialCover.getY(), current);
        if (lowerRightCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.LOWER_RIGHT, current, potencialCover));
            return true;
        }
        if (current.getX() == potencialCover.getX() && current.getY() == potencialCover.getY()){
            int surface = 0;
            if (current.getWidth() >= potencialCover.getWidth() && current.getHight() >= potencialCover.getHight()){
                surface=potencialCover.getWidth()*potencialCover.getHight();
            }
            else if (current.getWidth() < potencialCover.getWidth() && current.getHight() < potencialCover.getHight()){
                surface=current.getWidth()*current.getHight();
            } else {
                surface=current.getWidth()*current.getHight();
            }
            current.setCoveredSurface(current.getCoveredSurface() + surface);
            return true;
        }

        return false;
    }

    private boolean checkCorners(Banner current, Figure potencialCover) {
        boolean upperLeftCorner = checkIfCovering(potencialCover.getX() + potencialCover.getWidth(), potencialCover.getY() - potencialCover.getHight(), current);
        if (upperLeftCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.UPPER_LEFT, current, potencialCover));
            return true;
        }
        boolean lowerLeftCorner = checkIfCovering(potencialCover.getX() + potencialCover.getWidth(), potencialCover.getY(), current);
        if (lowerLeftCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.LOWER_LEFT, current, potencialCover));
            return true;
        }
        boolean upperRightCorner = checkIfCovering(potencialCover.getX(), potencialCover.getY() - potencialCover.getHight(), current);
        if (upperRightCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.UPPER_RIGHT, current, potencialCover));
            return true;
        }
        boolean lowerRightCorner = checkIfCovering(potencialCover.getX(), potencialCover.getY(), current);
        if (lowerRightCorner) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceByCornerCovered(CornerType.LOWER_RIGHT, current, potencialCover));
            return true;
        }
        if (current.getX() == potencialCover.getX() && current.getY() == potencialCover.getY()){
            int surface = 0;
            if (current.getWidth() >= potencialCover.getWidth() && current.getHight() >= potencialCover.getHight()){
                surface=potencialCover.getWidth()*potencialCover.getHight();
            }
            if (current.getWidth() < potencialCover.getWidth() && current.getHight() < potencialCover.getHight()){
                surface=current.getWidth()*current.getHight();
            }
            current.setCoveredSurface(current.getCoveredSurface() + surface);
            return true;
        }

        return false;
    }

    private boolean checkSides(Banner current, Banner potencialCover) {
        Optional<Pair<Integer, Integer>> resultUpper = potencialCover.sidePointList(SideType.UPPER).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultUpper.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.UPPER, current, potencialCover));
            return true;
        }

        Optional<Pair<Integer, Integer>> resultLower = potencialCover.sidePointList(SideType.LOWER).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultLower.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.LOWER, current, potencialCover));
            return true;
        }

        Optional<Pair<Integer, Integer>> resultLeft = potencialCover.sidePointList(SideType.LEFT).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultLeft.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.LEFT, current, potencialCover));
            return true;
        }

        Optional<Pair<Integer, Integer>> resultRight = potencialCover.sidePointList(SideType.RIGHT).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultRight.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.RIGHT, current, potencialCover));
            return true;
        }

        return false;
    }

    private boolean checkSides(Banner current, Figure potencialCover) {
        Optional<Pair<Integer, Integer>> resultUpper = potencialCover.sidePointList(SideType.UPPER).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultUpper.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.UPPER, current, potencialCover));
            return true;
        }

        Optional<Pair<Integer, Integer>> resultLower = potencialCover.sidePointList(SideType.LOWER).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultLower.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.LOWER, current, potencialCover));
            return true;
        }

        Optional<Pair<Integer, Integer>> resultLeft = potencialCover.sidePointList(SideType.LEFT).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultLeft.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.LEFT, current, potencialCover));
            return true;
        }

        Optional<Pair<Integer, Integer>> resultRight = potencialCover.sidePointList(SideType.RIGHT).stream()
                .filter(pair -> checkIfCovering(pair.getLeft(), pair.getRight(), current))
                .findFirst();
        if (resultRight.isPresent()) {
            current.setCoveredSurface(current.getCoveredSurface() + calculateSurfaceBySideCovered(SideType.RIGHT, current, potencialCover));
            return true;
        }

        return false;
    }

    /**
     * Oblicza pole powierzchni zasłoniętej przez Banner potencialCover
     *
     * @param cornerType     - Typ wierzchołka
     * @param current        - banner obecnie sprawdzany
     * @param potencialCover - potencjalnie przekrywajacy banner
     * @return pole powierzchni [cm2]
     */
    private int calculateSurfaceByCornerCovered(CornerType cornerType, Banner current, Banner potencialCover) {
        int width = 0;
        int height = 0;
        switch (cornerType) {
            case UPPER_LEFT -> {
                width = (potencialCover.getX() + potencialCover.getWidth()) - current.getX();
                height = current.getY() - (potencialCover.getY() - potencialCover.getHight());
            }
            case UPPER_RIGHT -> {
                width = (current.getX() + current.getWidth()) - potencialCover.getX();
                height = current.getY() - (potencialCover.getY() - potencialCover.getHight());
            }
            case LOWER_LEFT -> {
                width = (potencialCover.getX() + potencialCover.getWidth()) - current.getX();
                height = potencialCover.getY() - (current.getY() - current.getHight());
            }
            case LOWER_RIGHT -> {
                width = (current.getX() + current.getWidth()) - potencialCover.getX();
                height = potencialCover.getY() - (current.getY() - current.getHight());
            }
        }
        return width * height;
    }

    private int calculateSurfaceByCornerCovered(CornerType cornerType, Banner current, Figure potencialCover) {
        int width = 0;
        int height = 0;
        switch (cornerType) {
            case UPPER_LEFT -> {
                width = (potencialCover.getX() + potencialCover.getWidth()) - current.getX();
                height = current.getY() - (potencialCover.getY() - potencialCover.getHight());
            }
            case UPPER_RIGHT -> {
                width = (current.getX() + current.getWidth()) - potencialCover.getX();
                height = current.getY() - (potencialCover.getY() - potencialCover.getHight());
            }
            case LOWER_LEFT -> {
                width = (potencialCover.getX() + potencialCover.getWidth()) - current.getX();
                height = potencialCover.getY() - (current.getY() - current.getHight());
            }
            case LOWER_RIGHT -> {
                width = (current.getX() + current.getWidth()) - potencialCover.getX();
                height = potencialCover.getY() - (current.getY() - current.getHight());
            }
        }
        return width * height;
    }

    /**
     * Oblicza pole powierzchni zasłoniętej przez Banner potencialCover
     *
     * @param sideType       - Typ bocznego zasłonięcia
     * @param current        - banner obecnie sprawdzany
     * @param potencialCover - potencjalnie przekrywajacy banner
     * @return pole powierzchni [cm2]
     */
    private int calculateSurfaceBySideCovered(SideType sideType, Banner current, Banner potencialCover) {
        int width = 0;
        int height = 0;
        switch (sideType) {
            case UPPER -> {
                width = current.getWidth();
                height = current.getY() - (potencialCover.getY() - potencialCover.getHight());
            }
            case LOWER -> {
                width = current.getWidth();
                height = potencialCover.getY() - (current.getY() - current.getHight());
            }
            case LEFT -> {
                width = (potencialCover.getX() + potencialCover.getWidth()) - current.getX();
                height = current.getHight();
            }
            case RIGHT -> {
                width = (current.getX() + current.getWidth()) - potencialCover.getX();
                height = current.getHight();
            }
        }
        return width * height;
    }

    private int calculateSurfaceBySideCovered(SideType sideType, Banner current, Figure potencialCover) {
        int width = 0;
        int height = 0;
        switch (sideType) {
            case UPPER -> {
                width = current.getWidth();
                height = current.getY() - (potencialCover.getY() - potencialCover.getHight());
            }
            case LOWER -> {
                width = current.getWidth();
                height = potencialCover.getY() - (current.getY() - current.getHight());
            }
            case LEFT -> {
                width = (potencialCover.getX() + potencialCover.getWidth()) - current.getX();
                height = current.getHight();
            }
            case RIGHT -> {
                width = (current.getX() + current.getWidth()) - potencialCover.getX();
                height = current.getHight();
            }
        }
        return width * height;
    }

    /**
     * Sprawdza czy banner potencialCover przekrywa cały banner badany
     *
     * @param current        - banner obecnie sprawdzany
     * @param potencialCover - potencjalnie przekrywajacy banner
     * @return true/false
     */
    private boolean checkIfFullCovered(Banner current, Banner potencialCover) {
        if (checkIfCovering(potencialCover.getX(), potencialCover.getY(), current)) {
            int surface = current.getCoveredSurface() + (current.getHight() * current.getWidth());
            current.setCoveredSurface(current.getCoveredSurface() + surface);
            return true;
        }
        return false;
    }

    private boolean checkIfFullCovered(Banner current, Figure potencialCover) {
        if (checkIfCovering(potencialCover.getX(), potencialCover.getY(), current)) {
            int surface = current.getCoveredSurface() + (current.getHight() * current.getWidth());
            current.setCoveredSurface(current.getCoveredSurface() + surface);
            return true;
        }
        return false;
    }

    /**
     * Sprawdza czy Punkt (x, y) znajduje sie w banerze Banner
     *
     * @param x      - wspolrzedna X potencjalnego obiektu przekrywajacego
     * @param y      - wspolrzedna Y potencjalnego obiektu przekrywajacego
     * @param banner - banner obecnie sprawdzany
     * @return true/false
     */
    private boolean checkIfCovering(int x, int y, Banner banner) {
        return ((x >= banner.getX()) && (x <= banner.getX() + banner.getWidth())) &&
                ((y <= banner.getY()) && (y >= banner.getY() - banner.getHight()));
    }
}
