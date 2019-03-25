package com.game.bubblepop.com.game.bubblepop.bubble;

public class RotationUtils {

    public static void setNewRotation(IBubble bubble){
        long newmRotate = bubble.getStartRotation() + bubble.getCurrentRotation();
        bubble.setCurrentRotation(newmRotate);
    }

}
