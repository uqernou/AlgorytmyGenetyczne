package main.java.impl;

import lombok.Data;
import main.java.Figure;

@Data
public class BigWindow extends Figure {

    private int[] x_pos = {760, 1032, 1304, 1576, 1848};
    private int[] y_pos = {1370, 1370, 1370, 1370, 1370};

    public BigWindow(){
        this.setWidth(172);
        this.setHight(400);
    }
}
