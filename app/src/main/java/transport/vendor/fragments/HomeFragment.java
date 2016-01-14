package transport.vendor.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import transport.vendor.activity.HomeActivity;
import transport.vendor.activity.R;
import utils.Constants;

/**
 * Created by bharat on 12/22/15.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    private Animation anim;
    ImageView welcome_img;
    View rootView;
    TextView welcome_tv;
    Button my_profile_bt, manage_work_bt,search_rate_bt;
    Typeface face;
    boolean srchBtnClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         rootView = inflater.inflate(R.layout.fragment_home, container, false);
        face= Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");

        init();

        return  rootView ;
    }

    private void init() {
        welcome_img = (ImageView) rootView.findViewById(R.id.welcome_img);
        welcome_tv = (TextView) rootView.findViewById(R.id.welcome_tv);
        my_profile_bt = (Button) rootView.findViewById(R.id.my_profile_bt);
        manage_work_bt = (Button) rootView.findViewById(R.id.manage_work_bt);
        search_rate_bt = (Button) rootView.findViewById(R.id.search_rate_bt);

        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        welcome_img.startAnimation(anim);

        HomeActivity.changeTitle("HOME", false, false);

        welcome_tv.setTypeface(face);
        my_profile_bt.setTypeface(face);
        manage_work_bt.setTypeface(face);
        search_rate_bt.setTypeface(face);

        my_profile_bt.setOnClickListener(this);
        manage_work_bt.setOnClickListener(this);
        search_rate_bt.setOnClickListener(this);

        if(Constants.ROLE_ID.equals("2")){
            search_rate_bt.setVisibility(View.VISIBLE);
        } else {
            search_rate_bt.setVisibility(View.GONE);
        }
    }



    @Override
    public void onClick(View v) {
        if(v==my_profile_bt){
            srchBtnClick = false;
            HomeActivity.ChangeProfileColor();
            Fragment fragment = new ProfileFragment();
            replaceWithAnotherFragment(fragment);
        }else if(v==manage_work_bt){
            srchBtnClick = false;
            HomeActivity.ChangeManageWorkOrderColor();
           /* Fragment fragment = new WorkListFragment();
            replaceWithAnotherFragment(fragment);*/

            if(Constants.ROLE_ID.equals("2")) {
                Fragment fragment = new WorkListFragment();
                replaceWithAnotherFragment(fragment);
            } else {
                Fragment fragment = new WorkListVendorFragment();
                replaceWithAnotherFragment(fragment);
            }
        } if(v==search_rate_bt){
            srchBtnClick = true;
            Fragment fragment = new CustomerSearchRates();
            replaceWithAnotherFragment(fragment);
        }
    }

    private void replaceWithAnotherFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment != null) {
            ft.replace(R.id.frame_layout, fragment);
        }
        else {
            ft.add(R.id.frame_layout, fragment);
        }
        if(srchBtnClick) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}
