package main.java.impl;

import lombok.Data;
import main.java.Figure;

@Data
public class Building extends Figure {

    @Override
    public int getSurfaceArea() {
        return 0;
    }

    public Building(){
        this.setX(0);
        this.setY(1725);
        this.setWidth(2680);
        this.setHight(1725);
    }
}
