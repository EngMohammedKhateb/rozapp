package rozapp.roz.app.pages;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.DashboardAdabter;
import rozapp.roz.app.events.RefreshDashboardEvent;
import rozapp.roz.app.events.UserConnectedEvent;
import rozapp.roz.app.events.UserDisconnectedEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;

import rozapp.roz.app.tabs.MyFragmentAdapter;

public class DashboardFragment extends Fragment {


    @BindView(R.id.viewPager2)
    ViewPager2 view_pager;
    @BindView(R.id.tab_layout)
    TabLayout tab_layout;

    boolean is_search=false;

    private AuthResponse authResponse;

    private List<DashboardUser> users;
    private DashboardAdabter dashboardAdabter;

    private MyFragmentAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this,v);
        authResponse=new CallData(getContext()).getAuthResponse();
        ui();
        // getDashboardUser("all");
        filtersListener();



        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        adapter = new MyFragmentAdapter(fragmentManager , getLifecycle());
        view_pager.setAdapter(adapter);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                view_pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        view_pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tab_layout.selectTab(tab_layout.getTabAt(position));
            }
        });


        return v;
    }

    private void filtersListener() {

    }

    private void ui() {
//        users=new ArrayList<>();
//        arrayList_country=new ArrayList<>();
//        dashboardAdabter=new DashboardAdabter(getContext(),users);
//        rvDash.setLayoutManager(new GridLayoutManager(getContext(),2));
//        rvDash.setAdapter(dashboardAdabter);

    }



//    private void getDashboardUser( String filter ) {
//
//        users.clear();
//        dashboardAdabter.notifyDataSetChanged();
//        pp.setVisibility(View.VISIBLE);
//        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getDashboardUsers(filter).enqueue(new Callback<List<DashboardUser>>() {
//            @Override
//            public void onResponse(Call<List<DashboardUser>> call, Response<List<DashboardUser>> response) {
//
//                if (response.code()==200){
//                    users.clear();
//                    for (DashboardUser user:response.body()) {
//                        users.add(user);
//                    }
//                 }
//                dashboardAdabter.notifyDataSetChanged();
//                pp.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<List<DashboardUser>> call, Throwable t) {
//                pp.setVisibility(View.GONE);
//            }
//        });
//
//    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserConnectedEvent(UserConnectedEvent event) {
//        for (int i=0;i<users.size();i++){
//            if(event.getId() == users.get(i).getId()){
//                users.get(i).setOnline("1");
//                break;
//            }
//
//        }
//        dashboardAdabter.notifyDataSetChanged();

    }
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onToggleSearchEvent(ToggleSearchEvent event) {
//        if(is_search){
//            search_bar.setVisibility(View.GONE);
//        }else{
//            search_bar.setVisibility(View.VISIBLE);
//        }
//        is_search= !is_search;
//    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserDisconnectedEvent(UserDisconnectedEvent event) {
        for (int i=0;i<users.size();i++){
            if(event.getId() == users.get(i).getId()){
                users.get(i).setOnline("0");
                break;
            }
        }
        dashboardAdabter.notifyDataSetChanged();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshDashboardEvent(RefreshDashboardEvent event) {
        refreshgetDashboardUser("all");
    }

    private void refreshgetDashboardUser( String filter ) {
//
//
//        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getDashboardUsers(filter).enqueue(new Callback<List<DashboardUser>>() {
//            @Override
//            public void onResponse(Call<List<DashboardUser>> call, Response<List<DashboardUser>> response) {
//
//                if (response.code()==200){
//                    users.clear();
//                    for (DashboardUser user:response.body()) {
//                        users.add(user);
//                    }
//                }
//                dashboardAdabter.notifyDataSetChanged();
//
//            }
//
//            @Override
//            public void onFailure(Call<List<DashboardUser>> call, Throwable t) {
//
//            }
//        });

    }

}