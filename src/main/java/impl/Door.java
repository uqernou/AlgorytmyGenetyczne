package main.java.impl;

import lombok.Data;
import main.java.Figure;

@Data
public class Door extends Figure {
    public Door(){
        this.setX(1318);
        this.setY(355);
        this.setWidth(155);
        this.setHight(355);
    }
}
