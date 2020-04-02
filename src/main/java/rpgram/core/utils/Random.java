package rpgram.core.utils;

public class Random {
    public static final java.util.Random rnd = new java.util.Random();

    /**
     * Returns a random integer in range [min, max).
     */
    public static int randInt(int min, int max) {
        return rnd.nextInt(max - min) + min;
    }
}
