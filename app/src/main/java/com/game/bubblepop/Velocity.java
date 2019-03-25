package com.game.bubblepop;

import java.io.Serializable;

public class Velocity implements Serializable{
    public final double dx;
    public final double dy;

    Velocity(double dx, double dy){
        this.dx = dx;
        this.dy = dy;
    }

    float absoluteVelocity(){
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
