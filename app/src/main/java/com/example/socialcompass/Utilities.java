package com.example.socialcompass;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Pair;

import java.util.List;
import java.util.Optional;

public class Utilities {
    static final int MAX_LABEL_LENGTH = 20;
    static final String USE_PHONE_ORIENTATION = "-1";

    public static void displayAlert(Activity activity, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder
                .setTitle("Alert!")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    public static float radiansToDegreesFloat(float rad){
        return (float)(rad*180/Math.PI);
    }

    public static double radiansToDegreesDouble(double rad){
        return (double) rad*180/Math.PI;
    }

    public static float relativeAngle(Pair<Double, Double> currentLocation, Pair<Double, Double> destination){
        return relativeAngleUtils(currentLocation.first, currentLocation.second, destination.first, destination.second);
    }

    public static float relativeAngleUtils(Double userLat, Double userLong, Double destLat, Double destLong){
        Double latitudeDifference = destLat-userLat;
        Double longitudeDifference = destLong - userLong;

        if(latitudeDifference>0 && longitudeDifference>0){
            return (float) radiansToDegreesDouble(Math.atan(longitudeDifference/latitudeDifference));
        }

        if(latitudeDifference<0 && longitudeDifference>0){
            return (float) (180 - radiansToDegreesDouble(Math.atan(-longitudeDifference/latitudeDifference)));
        }

        if(latitudeDifference<0 && longitudeDifference<0){
            return (float) (180 + radiansToDegreesDouble(Math.atan(longitudeDifference/latitudeDifference)));
        }

        if(latitudeDifference>0 && longitudeDifference<0){
            return (float) (360 - radiansToDegreesDouble(Math.atan(-longitudeDifference/latitudeDifference)));
        }

        return 0f;
    }

    public static Optional<float[]> parseCoordinate(String str) {
        String[] cordArr = str.split(" ");
        if (cordArr.length != 2) {
            return Optional.empty();
        }
        try {
            float latitude = Float.parseFloat(cordArr[0]);
            float longitude = Float.parseFloat(cordArr[1]);
            return Optional.of(new float[]{latitude, longitude});
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }


    public static boolean namedLabels(List<String> labelNames) {
        int totalNames = 0;
        for(int i = 0; i < labelNames.size(); i++) {
            if(!labelNames.get(i).isBlank()) {
                totalNames ++;
            }
        }
        return totalNames == 3;
    }

    public static boolean validLabelLengths(List<String> labelNames) {
        for(int i = 0; i < labelNames.size(); i++) {
            if(labelNames.get(i).length() > 20) {
                return false;
            }
        }
        return true;
    }

    public static boolean validCoordinates(List<float[]> coordinates) {
        for(int i = 0; i < coordinates.size(); i++) {
            if (!validCoordinates(coordinates.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean validCoordinates(float[] coordinate) {
        return (Math.abs(coordinate[0]) <= 90 && Math.abs(coordinate[1]) <= 180);
    }


    public static boolean validOrientation(String orientation) {
        if (orientation.equals(USE_PHONE_ORIENTATION)) return true;
        try {
            int orientationValue = Integer.parseInt(orientation);
            return orientationValue <= 359 && orientationValue >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


