package bfst22.model;

public class Utility {

    private Utility() {    }

    /**
     * Calculates the distance between to points of the earth in meters
     * We have already tested the method by using doubles, however, the difference in outcome was miniscule compared to
     * the amount of data it costs for doubles. Between two points being 2000m apart, there was a difference of 10e-
     * Source: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
     *
     * @return Meters from start X,Y to end X,Y
     */
    public static float calculateDistance(float fromX, float fromY, float toX, float toY) {
        final float R = 6371.01f; // Radius of the earth
        float lon1 = Math.abs(fromX / 0.56f);
        float lon2 = Math.abs(toX / 0.56f);
        float lat1 = Math.abs(fromY);
        float lat2 = Math.abs(toY);

        float latDistance = (float) Math.toRadians(lat2 - lat1);
        float lonDistance = (float) Math.toRadians(lon2 - lon1);
        float a = (float) (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        float distance = R * c * 1000; // convert to meters

        distance = (float) Math.pow(distance, 2);
        return (float) Math.sqrt(distance);
    }

    public static double calculateDistanceDouble(float fromX, float fromY, float toX, float toY) {
        final float R = 6371.01f; // Radius of the earth
        double lon1 = Math.abs(fromX / 0.56f);
        double lon2 = Math.abs(toX / 0.56f);
        double lat1 = Math.abs(fromY);
        double lat2 = Math.abs(toY);

        double latDistance =  Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2));
        double c =  (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        double distance = R * c * 1000; // convert to meters

        distance =  Math.pow(distance, 2);
        return  Math.sqrt(distance);
    }

}
