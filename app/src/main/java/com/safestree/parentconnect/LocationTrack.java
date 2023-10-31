package com.safestree.parentconnect;

import java.util.ArrayList;
import java.util.List;

public class LocationTrack {
    private final List<List<String>> fireList;

    private final List<Integer> fireStatus;
//    public LocationTrack(List<List<String>> fireList) {
//        this.fireList = fireList;
//    }

    public LocationTrack()
    {
        fireList = new ArrayList<>();
        fireStatus = new ArrayList<>();
    }
    public List<List<String>> getFireList() {

        return fireList;
    }

    public List<Integer> getFireStatus() {
        return fireStatus;
    }

    public int getFireStatusSize()
    {
        return fireStatus.size();
    }
    public void addStatus(int count)
    {
        fireStatus.add(count);
    }
    public void addLocation(double lat, double lon)
    {
        List<String> temp = new ArrayList<>();
        temp.add(lat+"");
        temp.add(lon+"");
        fireList.add(temp);
    }
}
