package com.example.just_hungry;

import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.PostModel;

import java.util.Comparator;

public class PostsByDistanceComparator implements Comparator<PostModel> {

    LocationModel currentDeviceLocation;

    public PostsByDistanceComparator() {
        this.currentDeviceLocation = new LocationModel(0,0);
    }

    public PostsByDistanceComparator(LocationModel currentDeviceLocation) {
        this.currentDeviceLocation = currentDeviceLocation;
    }

    @Override
    public int compare(PostModel post1, PostModel post2) {
        // posts latitude and longitude
        double lat1 = post1.getLocation().getLatitude();
        double lat2 = post2.getLocation().getLatitude();
        double lon1 = post1.getLocation().getLongitude();
        double lon2 = post2.getLocation().getLongitude();

        // device latitude and longitude
        double latDevice = currentDeviceLocation.getLatitude();
        double lonDevice = currentDeviceLocation.getLongitude();

        // device distance to each post
        double distToPost1 = distFrom(latDevice, lonDevice, lat1, lon1);
        double distToPost2 = distFrom(latDevice, lonDevice, lat2, lon2);

        if (distToPost1 < distToPost2) {
            return 1;
        }
        else if (distToPost1 == distToPost2) {
            return 0;
        }
        else {
            return -1;
        }

    }

    public static double distFrom(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lon2-lon1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return dist;
    }

}
