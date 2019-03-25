package com.game.bubblepop;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.game.bubblepop.com.game.bubblepop.bubble.Bubble;

public class BitMapCache {

    private static BitMapCache cache = null;
    private static LruCache<String, Bitmap> mMemoryCache;

    public static BitMapCache getCache() {
        if (cache == null) {
            initCache();
            cache = new BitMapCache();
        }
        return cache;
    }

    private static void initCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/16th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 16;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public Bitmap getAppropriateBitmap(int bubble_type, int mScaledBitmapWidth, Game game) {
        String key = bubble_type + "_" + mScaledBitmapWidth;
        if (mMemoryCache.get(key) == null) {
            Bitmap bitmap;
            switch (bubble_type) {
                case Bubble.BUBBLE_BUSTER_TYPE:
                    bitmap = game.getBubbles().getPurpleBubble();
                    break;
                case Bubble.BUBBLE_FRICTION_TYPE:
                    bitmap = game.getBubbles().getGreenBubble();
                    break;
                default:
                    bitmap = game.getBubbles().getOrangeBubble();
                    break;
            }
            mMemoryCache.put(key, Bitmap.createScaledBitmap(bitmap, mScaledBitmapWidth,
                    mScaledBitmapWidth, false));
        }

        return mMemoryCache.get(key);

    }

}
