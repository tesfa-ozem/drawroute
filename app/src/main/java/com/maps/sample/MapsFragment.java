package com.maps.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.maps.route.callbacks.EstimationsCallBack;
import com.maps.route.extensions.MapExtensionKt;
import com.maps.route.model.Legs;
import com.maps.route.model.TravelMode;

import io.reactivex.rxjava3.disposables.Disposable;

public class MapsFragment extends Fragment implements EstimationsCallBack { // Only need to implement EstimationsCallBack when you need estimated time of arrival or the distance between two locations

    private Disposable disposable;

    private final OnMapReadyCallback callback = googleMap -> {

        LatLng source = new LatLng(31.490127, 74.316971); //starting point (LatLng)
        LatLng destination = new LatLng(31.474316, 74.316112); // ending point (LatLng)

        //zoom/move cam on map ready
        MapExtensionKt.moveCameraOnMap(googleMap, 16, true, source);

        //draw route on map
       disposable = MapExtensionKt.drawRouteOnMap(googleMap,
                getString(R.string.google_map_api_key),
                getContext(),
                source,
                destination,
                getActivity().getColor(R.color.pathColor),
                true, true, 13, TravelMode.DRIVING, this/*(this if you want the ETA otherwise just pass null)*/);
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onDestroy() {
        if(disposable!=null)
            disposable.dispose();
        super.onDestroy();

    }

    // Only need to Override this method when you need estimated time of arrival or the distance between two locations
    @Override
    public void routeEstimations(@org.jetbrains.annotations.Nullable Legs legs) {
        if(legs == null)
            return;

        //Estimated time of arrival
        Log.d("estimatedTimeOfArrival", "withUnit"+ legs.getDuration().getText());
        Log.d("estimatedTimeOfArrival", "InMilliSec"+ legs.getDuration().getValue());

        //Google suggested path distance
        Log.d("GoogleSuggestedDistance", "withUnit"+ legs.getDistance().getText());
        Log.d("GoogleSuggestedDistance", "InMilliSec"+ legs.getDistance().getValue());
    }
}