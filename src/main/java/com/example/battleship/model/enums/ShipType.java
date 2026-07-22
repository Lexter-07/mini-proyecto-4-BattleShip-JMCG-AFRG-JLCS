package com.example.battleship.model.enums;

public enum ShipType {

    AIRCRAFT(4),
    SUBMARINE(3),
    DESTROYER(2),
    FRIGATE(1);

    private final int size;

    ShipType(int size){
        this.size = size;
    }

    public int getSize(){return size;}

}
