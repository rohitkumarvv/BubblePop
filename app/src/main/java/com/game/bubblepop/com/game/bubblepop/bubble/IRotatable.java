package com.game.bubblepop.com.game.bubblepop.bubble;

public interface IRotatable {

    int ROTATION_INCREMENTS = 5;

    long getStartRotation();

    void setStartRotation(long startRotation);

    long getCurrentRotation();

    void setCurrentRotation(long rotation);

}
