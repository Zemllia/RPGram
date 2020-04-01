package core.utils;

public class Random {
    public static int randInt(int min, int max) {
        int x = (int) (Math.random() * ((max - min) + 1)) + min;
        return x;
    }
}
