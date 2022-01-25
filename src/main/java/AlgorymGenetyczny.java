package main.java;


import main.java.impl.BigWindow;
import main.java.impl.Building;
import main.java.impl.Door;
import main.java.impl.SmallWindow;
import main.java.utils.AlgorithmUtils;
import main.java.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AlgorymGenetyczny {

    private List<Banner> bannerList = new ArrayList<>();
    private List<Banner> nextBannerList = new ArrayList<>();

    private List<Figure> facadeElementList = new ArrayList<>();
    private SmallWindow smallWindow = new SmallWindow();
    private BigWindow bigWindow = new BigWindow();
    private Building building = new Building();

    private void symulacja() throws IOException {
        generateFacadeElement();
        generateFirstPopulation();
        for(int i = 0; i < 1000; i++) {
            calculateAdaptation(i);
            double avrFi = bannerList.stream().mapToDouble(Banner::getF_i).sum()/(double) bannerList.size();
            System.out.println(avrFi);
            if(avrFi > 0.9)
                return;
            nextBannerList = AlgorithmUtils.rankingSelection(bannerList);
//            nextBannerList = AlgorithmUtils.rouletteWheelSelectionScaled(bannerList);
//            nextBannerList = AlgorithmUtils.crucifixion(nextBannerList);
//            nextBannerList = AlgorithmUtils.mutation(nextBannerList);
            bannerList = new ArrayList<>();
            bannerList = nextBannerList;
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
//        facadeElementList.forEach(Figure::printCoorinates);
//        building.printCoorinates();
    }

    private void generateFirstPopulation() throws IOException {
//        fieldFreeSurfaceInfo();
        for(int i = 0; i < 20; i ++){
            Banner banner = new Banner();
            banner.setWidth(banner.getA()[ThreadLocalRandom.current().nextInt(0, 5)]);
            banner.setHight(banner.getB()[ThreadLocalRandom.current().nextInt(0, 3)]);
            banner.setX(ThreadLocalRandom.current().nextInt(0, building.getWidth()));
            banner.setY(ThreadLocalRandom.current().nextInt(0, building.getHight()));
            bannerList.add(banner);
        }
//        bannerList.forEach(Banner::printCoorinates);
        List<Figure> fig = facadeElementList;
        fig.add(building);
        FileUtils.saveElements(fig, 0);
    }

    private void calculateAdaptation(int step) throws IOException {
        bannerList.forEach(banner -> {
            AlgorithmUtils.calculateAdaptation(bannerList, facadeElementList, banner);
//            System.out.println("Banner f_i: " + banner.getF_i() + " " + banner.getX() + " " + banner.getY());
        });
        FileUtils.saveAdaptation(bannerList);
        FileUtils.saveBanners(bannerList, step);
    }

    private void fieldFreeSurfaceInfo(){
        int surfaceOfElements = facadeElementList.stream().mapToInt(Figure::getSurfaceArea).sum();
        int buildingSurface = building.getWidth() * building.getHight();
        System.out.println("Pole budynku: " + (double) (buildingSurface) / 10000.0 + " [m2]");
        System.out.println("Pole wolnego miejsca: " + (double) (buildingSurface - surfaceOfElements) / 10000.0 + " [m2]");
        System.out.println("Wolne miejsce: " + ((double) (buildingSurface - surfaceOfElements) / 10000.0) / ((double) (buildingSurface) / 10000.0) + " [m2]");
    }

    public static void main(String[] args) throws IOException {
        AlgorymGenetyczny algorymGenetyczny = new AlgorymGenetyczny();
        algorymGenetyczny.symulacja();

    }
}
