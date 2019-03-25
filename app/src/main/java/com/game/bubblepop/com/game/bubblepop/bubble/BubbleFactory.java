package com.game.bubblepop.com.game.bubblepop.bubble;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.game.bubblepop.Game;

public class BubbleFactory {

    private static BubbleFactory bubbleFactory = null;

    public static BubbleFactory getBubbleFactory(){

        if(bubbleFactory == null){
            return new BubbleFactory();
        }
        return bubbleFactory;
    }

    @Nullable public IBubble createBubble(int bubbleType , @NonNull Game game){

        if(bubbleType == IBubble.BUBBLE_FRICTION_TYPE){
            return new SpecialBubble(game);
        }
        else if (bubbleType == IBubble.BUBBLE_REGULAR_TYPE){
            return new DefaultBubble(game);
        }
        return null;
    }
}
