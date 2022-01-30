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

    private void symulacja(int Nbanners, int Ntime) throws IOException {
        generateFacadeElement();
        generateFirstPopulation(Nbanners);
        for(int i = 0; i < Ntime; i++) {
            calculateAdaptation(i);
            double avrFi = bannerList.stream().mapToDouble(Banner::getF_i).sum()/(double) bannerList.size();
            System.out.println(avrFi);
            if(avrFi > 0.9)
                return;
            nextBannerList = AlgorithmUtils.rankingSelection(bannerList);
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
    }

    private void generateFirstPopulation(int Nbanners) throws IOException {
        for(int i = 0; i < Nbanners; i ++){
            Banner banner = new Banner();
            banner.setWidth(banner.getA()[ThreadLocalRandom.current().nextInt(0, 5)]);
            banner.setHight(banner.getB()[ThreadLocalRandom.current().nextInt(0, 3)]);
            banner.setX(ThreadLocalRandom.current().nextInt(0, building.getWidth()));
            banner.setY(ThreadLocalRandom.current().nextInt(0, building.getHight()));
            bannerList.add(banner);
        }
        List<Figure> fig = facadeElementList;
        fig.add(building);
        FileUtils.saveElements(fig, 0);
    }

    private void calculateAdaptation(int step) throws IOException {
        bannerList.forEach(banner -> {
            AlgorithmUtils.calculateAdaptation(bannerList, facadeElementList, banner);
        });
        FileUtils.saveAdaptation(bannerList);
        FileUtils.saveBanners(bannerList, step);
    }

    public static void main(String[] args) throws IOException {
        AlgorymGenetyczny algorymGenetyczny = new AlgorymGenetyczny();
        algorymGenetyczny.symulacja(20, 100);

    }
}
