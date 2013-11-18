package com.example.sidescroller;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Owner on 11/18/13.
 */
public class MenuFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vw = inflater.inflate(R.layout.menu_fragment, container, false);
        vw.setBackgroundColor(getResources().getColor(android.R.color.background_light));
        //start the xml files using this class not by just setting content view
        return vw;
    }
}
