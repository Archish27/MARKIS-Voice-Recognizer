package com.markis;

import com.markis.models.Apps;

import java.util.ArrayList;

/**
 * Created by Archish on 2/16/2017.
 */

public class Dictionary {
    public ArrayList<Apps> appsArrayList;

    public Dictionary() {
        appsArrayList =  new ArrayList<>();
        appsArrayList.add(new Apps("gmail","com.google.android.gm"));
        appsArrayList.add(new Apps("whatsapp","com.whatsapp"));
        appsArrayList.add(new Apps("facebook messenger","com.facebook.orca"));
        appsArrayList.add(new Apps("youtube","com.google.android.youtube"));
        appsArrayList.add(new Apps("maps","com.google.android.apps.maps"));
        appsArrayList.add(new Apps("instagram","com.instagram.android"));



    }
    public ArrayList<Apps> getDictionary()
    {
        return appsArrayList;
    }
}
