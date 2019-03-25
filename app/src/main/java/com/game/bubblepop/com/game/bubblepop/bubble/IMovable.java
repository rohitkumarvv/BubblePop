package com.game.bubblepop.com.game.bubblepop.bubble;


public interface IMovable {

    void stopMovement(final boolean wasPopped);

    void move();

    void setSpeedAndDirection();
}
