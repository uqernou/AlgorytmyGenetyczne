package main.java.utils;

import lombok.Data;
import lombok.experimental.UtilityClass;
import main.java.Banner;
import main.java.Figure;
import main.java.Individual;
import main.java.Pixel;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Data
@UtilityClass
public class AlgorithmUtils {


    public List<Individual> rankingIndividualSelection2(List<Individual> population){
        List<Individual> nextPopulationSelection = rouletteWheelSelectionIndividual(population);
        nextPopulationSelection = crucifixionAndMutationIndividuals(nextPopulationSelection);
        return nextPopulationSelection;
    }

    /**
     * Metoda rankingowa selekcji Osobników. 50% populacji osobników o najlepszym przystosowaniu przechodzi do
     * następnego etapu. Na podstawie 50% najlepszych osobników, dokonuje się krzyżowania oraz mutacji jako uzupełnienie
     * pozostałej części kolejenej generacji
     * @param population
     * @return
     */
    public List<Individual> rankingIndividualSelection(List<Individual> population){
        List<Individual> nextPopulationSelection = new ArrayList<>();
        population.sort(Comparator.comparing(Individual::getAvrg).reversed());

        final List<Individual> top50pop = population.subList(0, population.size()/2);
        List<Individual> rest50pop = population.subList(population.size()/2, population.size());
        for(int i = 0; i < top50pop.size(); i++){
            for (int j = 0; j < top50pop.get(i).getBannerList().size(); j++) {
                rest50pop.get(i).getBannerList().get(j).setX(
                        top50pop.get(i).getBannerList().get(j).getX());
                rest50pop.get(i).getBannerList().get(j).setY(
                        top50pop.get(i).getBannerList().get(j).getY());
            }
        }
        rest50pop = crucifixionAndMutationIndividuals(rest50pop);

        nextPopulationSelection.addAll(top50pop);
        nextPopulationSelection.addAll(rest50pop);
        return nextPopulationSelection;
    }


    /**
     * Metoda krzyżowania osobników populacji. Polega na losowym połączeniu w pary osobników, a następnie na wymianie
     * części genotypu subpopulacji. Wymiana części genotypu subpopulacji dotyczy połowy najgorzej przystosowanych bannerów.
     * Samo krzyżowanie polega na losowym wybraniu długości oraz pozycji początkowej allelu (bitu) do wymiany.
     * @param selectedPopulation
     * @return
     */
    public List<Individual> crucifixionAndMutationIndividuals(List<Individual> selectedPopulation){
        List<Individual> nextPopulationCrucifixion = new ArrayList<>();
        Collections.shuffle(selectedPopulation);
        for (int i = 0; i < selectedPopulation.size() / 2; i++) {
            Individual parent1 = selectedPopulation.get(i);
            Individual parent2 = selectedPopulation.get(selectedPopulation.size() - i - 1);


            parent1.getBannerList().sort(Comparator.comparing(Banner::getF_i));
            parent2.getBannerList().sort(Comparator.comparing(Banner::getF_i));

            for(int j = 0; j < parent1.getBannerList().size() / 4; j++){
                List<Pair<Integer, Integer>> crucifix = BinaryUtils.randomMinMaxFromParents2(parent1.getBannerList().get(j),
                                                                                             parent2.getBannerList().get(j));
                Pair<Integer, Integer> x_coordinates = crucifix.get(0);
                Pair<Integer, Integer> y_coordinates = crucifix.get(1);
                parent1.getBannerList().get(j).setX(x_coordinates.getLeft());
                parent2.getBannerList().get(j).setX(x_coordinates.getRight());
                parent1.getBannerList().get(j).setY(y_coordinates.getLeft());
                parent2.getBannerList().get(j).setY(y_coordinates.getRight());
            }

            nextPopulationCrucifixion.add(parent1);
            nextPopulationCrucifixion.add(parent2);
        }
        nextPopulationCrucifixion.forEach(individual -> {
            List<Banner> bannerList;
            bannerList = individual.getBannerList();
            bannerList = mutation(bannerList);
            individual.setBannerList(bannerList);
        });
        return nextPopulationCrucifixion;
    }

    public List<Banner> crucifixion(List<Banner> selectedPopulation) {
        List<Banner> nextPopulationCrucifixion = new ArrayList<>();
        Collections.shuffle(selectedPopulation);
        for (int i = 0; i < selectedPopulation.size() / 2; i++) {
            Banner parent1 = selectedPopulation.get(i);
            Banner parent2 = selectedPopulation.get(selectedPopulation.size() - i - 1);

            List<Pair<Integer, Integer>> crucifix = BinaryUtils.randomMinMaxFromParents2(parent1, parent2);
            Pair<Integer, Integer> x_coordinates = crucifix.get(0);
            Pair<Integer, Integer> y_coordinates = crucifix.get(1);
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
            if (randomX <= 0.9) {
                final int randomPosition = ThreadLocalRandom.current().nextInt(0, Integer.toBinaryString(banner.getX()).length());
                banner.setX(BinaryUtils.positionMutation(banner.getX(), randomPosition));
            }
            if (randomY <= 0.9) {
                final int randomPosition = ThreadLocalRandom.current().nextInt(0, Integer.toBinaryString(banner.getY()).length());
                banner.setY(BinaryUtils.positionMutation(banner.getY(), randomPosition));
            }
            nextGenerationPopulation.add(banner);
        });

        return nextGenerationPopulation;
    }


    public List<Individual> rouletteWheelSelectionIndividual(List<Individual> population) {
        List<Individual> nextPopulationSelection = new ArrayList<>();
        final double sumF_i = population.stream().mapToDouble(Individual::getAvrg).sum();
        for (int i = 0; i < population.size(); i++) {
            double random = Math.random();
            double rulete = 0.0;
            boolean result = true;
            while (result) {
                for (Individual banner : population) {
                    rulete += (banner.getAvrg() / sumF_i);
                    if (rulete >= random) {
                        nextPopulationSelection.add(new Individual(population.get(i).getBannerList(), population.get(i).getAvrg()));
                        result = false;
                        break;
                    }
                }
            }
        }
        return nextPopulationSelection;
    }

    public List<Banner> rouletteWheelSelectionBanner(List<Banner> population) {
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
                        nextPopulationSelection.add(new Banner(banner.getX(), banner.getY(), population.get(i).getWidth(), population.get(i).getHight()));
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
        final List<Banner> top20pop = population.subList(0, population.size()/5);
        List<Banner> rest80pop = population.subList(population.size()/5, population.size());
        List<Banner> finalRest80pop = rouletteWheelSelectionBanner(rest80pop);
        for(int i = 0; i < rest80pop.size(); i++) {
            rest80pop.get(i).setX(finalRest80pop.get(i).getX());
            rest80pop.get(i).setY(finalRest80pop.get(i).getY());
        }

        rest80pop = crucifixion(rest80pop);
        rest80pop = mutation(rest80pop);

        List<Banner> nextPopulation = new ArrayList<>();
        nextPopulation.addAll(top20pop);
        nextPopulation.addAll(rest80pop);

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

    public void calculateAdaptation(List<Banner> population, Banner current, boolean[][] pixelList) {
        List<Banner> coveredBanners = population.stream()
                .filter(e -> !e.equals(current))
                .collect(Collectors.toList());
        boolean[][] zakryte = Arrays.stream(pixelList)
                .map(boolean[]::clone)
                .toArray(boolean[][]::new);

        // zakrywanie powierzchni bannerami
        coveredBanners.forEach(otherBanner -> {
            for (int i = otherBanner.getX(); i < otherBanner.getX() + otherBanner.getWidth(); i++ ){
                for (int j = otherBanner.getY() - otherBanner.getHight(); j < otherBanner.getY(); j++){

                    zakryte[i+200][j+400] = true;
                }
            }
        });
        // obliczenie Fi

        calculateFi(current, zakryte);
//        System.out.println(current.getF_i());
    }

    private void calculateFi(Banner banner, boolean[][] zakryte) {
        int fieldSum = banner.getHight() * banner.getWidth();
        int fieldOtherElements = 0;
        for(int i = banner.getX(); i < banner.getX() + banner.getWidth(); i++)
            for(int j = banner.getY() - banner.getHight(); j < banner.getY(); j++){
                if(zakryte[i+200][j+400])
                    fieldOtherElements++;
            }
        double fi = (double) fieldOtherElements / (double) fieldSum;
        banner.setF_i(1.0 - fi);
    }

    public double argFi(List<Banner> population) {
        return population.stream().mapToDouble(Banner::getF_i).sum() / (double) population.size();
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

    public boolean czyPunktWElemencie(Figure figure, int x, int y){
        return (x >= figure.getX() && x <= figure.getX() + figure.getWidth()) &&
                (y >= figure.getY() - figure.getHight() && y <= figure.getY());
    }

    public boolean czyPunktWBanerze(Banner banner, int x, int y){
        return (x >= banner.getX() || x <= banner.getX() + banner.getWidth()) &&
                (y >= banner.getY() - banner.getHight() || y <= banner.getY());
    }

}
