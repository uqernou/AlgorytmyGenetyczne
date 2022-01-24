package main.java.impl;

import lombok.Data;
import main.java.Figure;

@Data
public class SmallWindow extends Figure {

    private int[] x_pos = {760, 1032, 1576, 1848,
            760, 1032, 1304, 1576, 1848};
    private int[] y_pos = {335, 335, 335, 335,
            770, 770, 770, 770, 770};

    public SmallWindow(){
        this.setWidth(172);
        this.setHight(235);
    }
}
