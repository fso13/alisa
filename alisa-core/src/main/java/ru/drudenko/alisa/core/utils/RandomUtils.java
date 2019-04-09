package ru.drudenko.alisa.core.utils;

public final class RandomUtils {
    RandomUtils() {
    }

    public static String getOtp() {
        return String.valueOf(100000 + (long) (Math.random() * (999999 - 100000)));
    }
}
