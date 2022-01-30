package main.java.utils;

import lombok.experimental.UtilityClass;
import main.java.Banner;
import main.java.Figure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@UtilityClass
public class FileUtils {

    private static String FILENAME = "C:"+ File.separator +"Users"+ File.separator +"uqern"+ File.separator +"Desktop" + File.separator + "AG_banner" + File.separator ;
    private static BufferedWriter writer;


    public void saveBanners(List<Banner> population, int step) throws IOException {
        for(int i = 0; i<population.size(); i++) {
            File banner = new File(FILENAME + "a_" + step + ".txt");
            if (!banner.exists())
                banner.createNewFile();

            writer = Files.newBufferedWriter(Paths.get(banner.getPath()), StandardOpenOption.APPEND);
            List<Integer> xCor = population.get(i).xToFile();
            List<Integer> yCor = population.get(i).yToFile();
            for(int j = 0; j < xCor.size(); j++) {
                writer.write(xCor.get(j) + " " + yCor.get(j) + " " + i);
                writer.newLine();
            }
            writer.flush();
        }
    }
    public void saveElements(List<Figure> elements, int step) throws IOException {
        for(int i = 0; i<elements.size(); i++) {
            File element = new File(FILENAME + "t_" + step + ".txt");
            if (!element.exists())
                element.createNewFile();
            writer = Files.newBufferedWriter(Paths.get(element.getPath()), StandardOpenOption.APPEND);
            List<Integer> xCor = elements.get(i).xToFile();
            List<Integer> yCor = elements.get(i).yToFile();
            for(int j = 0; j < xCor.size(); j++) {
                writer.write(xCor.get(j) + " " + yCor.get(j));
                writer.newLine();
            }
            writer.flush();
        }
    }
    public void saveAdaptation(List<Banner> population) throws IOException {
        for(int i = 0; i<population.size(); i++) {
            File banner = new File(FILENAME + "character" + ".txt");
            if (!banner.exists())
                banner.createNewFile();

            writer = Files.newBufferedWriter(Paths.get(banner.getPath()), StandardOpenOption.APPEND);
            writer.write(AlgorithmUtils.argFi(population) + "");
            writer.newLine();
            writer.flush();
        }
    }
}
