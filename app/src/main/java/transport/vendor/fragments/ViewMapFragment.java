package transport.vendor.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

import transport.vendor.activity.GMapV2GetRouteDirection;
import transport.vendor.activity.HomeActivity;
import transport.vendor.activity.R;
import utils.Constants;
import utils.NetConnection;

/**
 * Created by sandeep on 12/30/15.
 */
public class ViewMapFragment extends Fragment {
    View rootView;
    Typeface face ;
    Boolean isConnected;
    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng fromPosition;
    LatLng toPosition;
    Drawable drawable;
    Document document;
    SupportMapFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     //   rootView = inflater.inflate(R.layout.fragment_map, container, false);
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_map, container,
                    false);
        }

        catch(Exception e){
            e.printStackTrace();
            return rootView;
        }
        face= Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");
        HomeActivity.changeTitle("Route", true, false);
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        init();

        return  rootView ;
    }

    private void init() {

       /* SupportMapFragment supportMapFragment = (SupportMapFragment) .getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mGoogleMap = supportMapFragment.getMap();*/

        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }

        if (mGoogleMap == null) {
            mGoogleMap = fragment.getMap();
        }

        v2GetRouteDirection = new GMapV2GetRouteDirection();

        // Enabling MyLocation in Google Map
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.setTrafficEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(4));
        markerOptions = new MarkerOptions();
        fromPosition = new LatLng(Constants.FromLat,Constants.FromLng);
        toPosition = new LatLng(Constants.ToLat,Constants.ToLng);


        LatLng markerLoc=new LatLng(Constants.FromLat, Constants.FromLng);
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markerLoc)      // Sets the center of the map to Mountain View
                .zoom(4)                   // Sets the zoom// Sets the tilt of the camera to 30 degrees
                .build();                   //
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(Constants.FromLat, Constants.FromLng)).title("Source")
        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        GetRouteTask getRoute = new GetRouteTask();
        getRoute.execute();

    }

    /**
     * get route
     */

    private class GetRouteTask extends AsyncTask<String, Void, String> {

        private ProgressDialog Dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
            Dialog = new ProgressDialog(getActivity());
            Dialog.setMessage("Loading route...");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            document = v2GetRouteDirection.getDocument(fromPosition, toPosition, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;

        }

        @Override
        protected void onPostExecute(String result) {
            mGoogleMap.clear();
            if(response.equalsIgnoreCase("Success")){
                ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                PolylineOptions rectLine = new PolylineOptions().width(10).color(
                        Color.RED);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                // Adding route on the map
                mGoogleMap.addPolyline(rectLine);
                markerOptions.position(toPosition);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions.title("Destination");
                markerOptions.draggable(false);
                mGoogleMap.addMarker(markerOptions);

                markerOptions.position(fromPosition);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions.title("Source");
                markerOptions.draggable(false);
                mGoogleMap.addMarker(markerOptions);

            }

            Dialog.dismiss();
        }
    }

}
