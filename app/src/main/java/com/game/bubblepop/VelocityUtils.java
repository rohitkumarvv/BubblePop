package com.game.bubblepop;

public class VelocityUtils {

    public static Velocity getDefaultMinVelocity(){
        return new Velocity(4, 4);
    }

    public static Velocity getDefaultMaxVelocity(){
        return  new Velocity(7, 7);
    }

    public static Velocity getNewVelocity(Velocity velocity, long areaPixels, long defaultAreaPixels) {
        return new Velocity(velocity.dx * areaPixels / defaultAreaPixels, velocity.dy * areaPixels / defaultAreaPixels);
    }
}
