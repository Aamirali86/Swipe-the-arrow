package com.techytec.swipethearrow;

public class Util {

    public static int generateRandomNumber(int min, int max) {

        return min + (int)(Math.random() * ((max - min) + 1));
//        Random rand = new Random();
//        int randomNum = rand.nextInt((max - min) + 1) + min;
//
//        return randomNum;
    }
}
