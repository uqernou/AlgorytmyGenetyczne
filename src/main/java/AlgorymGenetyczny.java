package main.java;


import main.java.impl.BigWindow;
import main.java.impl.Building;
import main.java.impl.Door;
import main.java.impl.SmallWindow;
import main.java.utils.AlgorithmUtils;
import main.java.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AlgorymGenetyczny {

    private List<Individual> bannerIndividualList = new ArrayList<>();
    private List<Double> adaptationList = new ArrayList<>();
    private List<Banner> bannerList = new ArrayList<>();
    private List<Individual> nextBannerIndividualList = new ArrayList<>();

    private List<Figure> facadeElementList = new ArrayList<>();
    private SmallWindow smallWindow = new SmallWindow();
    private BigWindow bigWindow = new BigWindow();
    private Building building = new Building();

    private void symulacja(int Nbanners, int Nindividual) throws IOException {
        generateFacadeElement();
        generateFirstPopulation(Nbanners, Nindividual);
        for(int i = 0; i < 100; i++) {
            calculateAdaptation(i);
            List<Individual> sorted = bannerIndividualList.stream().sorted(Comparator.comparing(Individual::getAvrg).reversed()).collect(Collectors.toList());
            System.out.println(" " + sorted.get(0).getAvrg());
            Optional<Individual> winner = bannerIndividualList.stream().filter(e -> e.getAvrg() > 0.93).findFirst();
            if(winner.isPresent()) {
                FileUtils.saveBanners(winner.get().getBannerList(), i);
                facadeElementList.add(building);
                FileUtils.saveElements(facadeElementList, i);
                return;
            }
            nextBannerIndividualList = AlgorithmUtils.rankingIndividualSelection2(bannerIndividualList);
            bannerIndividualList = new ArrayList<>();
            bannerIndividualList = nextBannerIndividualList;
        }
    }

    private void generateFacadeElement(){
        facadeElementList.add(new Door());
        for(int i = 0; i < smallWindow.getX_pos().length; i++) {
            SmallWindow smallWindowTmp = new SmallWindow();
            smallWindowTmp.setX(smallWindow.getX_pos()[i]);
            smallWindowTmp.setY(smallWindow.getY_pos()[i]);
            facadeElementList.add(smallWindowTmp);
        }
        for(int i = 0; i < bigWindow.getX_pos().length; i++) {
            BigWindow bigWindowTmp = new BigWindow();
            bigWindowTmp.setX(bigWindow.getX_pos()[i]);
            bigWindowTmp.setY(bigWindow.getY_pos()[i]);
            facadeElementList.add(bigWindowTmp);
        }
    }

    private void generateFirstPopulation(int Nbanners, int Npopulation) throws IOException {
        List<Banner> bannerList = new ArrayList<>();
        for (int i = 0; i < Nbanners; i ++) {
            Banner banner = new Banner();
            banner.setWidth(banner.getA()[ThreadLocalRandom.current().nextInt(0, 5)]);
            banner.setHight(banner.getB()[ThreadLocalRandom.current().nextInt(0, 3)]);
            banner.setX(ThreadLocalRandom.current().nextInt(0, building.getWidth()));
            banner.setY(ThreadLocalRandom.current().nextInt(0, building.getHight()));
            bannerList.add(banner);
        }
        Individual individual = new Individual();
        individual.setBannerList(bannerList);
        bannerIndividualList.add(individual);
        for (int i = 0; i < Npopulation - 1; i++) {
            List<Banner> restBannerList = new ArrayList<>();
            bannerIndividualList.get(0).getBannerList().forEach(currnet -> {
                Banner banner = new Banner();
                banner.setWidth(currnet.getWidth());
                banner.setHight(currnet.getHight());
                banner.setX(ThreadLocalRandom.current().nextInt(0, building.getWidth()));
                banner.setY(ThreadLocalRandom.current().nextInt(0, building.getHight()));
                restBannerList.add(banner);
            });
            Individual restIndividual = new Individual();
            restIndividual.setBannerList(restBannerList);
            bannerIndividualList.add(restIndividual);
        }
    }


    private void calculateAdaptation(int step) throws IOException {
        bannerIndividualList.forEach(bannerList -> {
                    bannerList.setAvrg(0);
        });
        bannerIndividualList.forEach(bannerList -> {
            bannerList.getBannerList().forEach(banner -> {
                AlgorithmUtils.calculateAdaptation(bannerList.getBannerList(), facadeElementList, banner);
            });
            double avrFi = bannerList.getBannerList().stream().mapToDouble(Banner::getF_i).sum()/(double) bannerList.getBannerList().size();
            bannerList.setAvrg(avrFi);
        });

    }

    public static void main(String[] args) throws IOException {
        AlgorymGenetyczny algorymGenetyczny = new AlgorymGenetyczny();
        algorymGenetyczny.symulacja(10, 10000);

    }
}
