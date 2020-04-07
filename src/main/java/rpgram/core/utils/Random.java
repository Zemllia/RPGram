package rpgram.core.utils;

import java.util.HashSet;

public class Random {
    public static HashSet<Integer> ids = new HashSet<>();

    public static final java.util.Random rnd = new java.util.Random();

    /**
     * Returns a random integer in range [min, max).
     */
    public static int randInt(int min, int max) {
        return rnd.nextInt(max - min) + min;
    }

    /**
     * Returns a unique id for game object.
     */
    public static int randId() {
        int id = rnd.nextInt(Integer.MAX_VALUE);
        while (ids.contains(id)) {
            id = rnd.nextInt(Integer.MAX_VALUE);
        }
        return id;
    }
}
