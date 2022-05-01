package rozapp.roz.app.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.FollowAdabter;
import rozapp.roz.app.events.RefreshDashboardEvent;
import rozapp.roz.app.events.UserConnectedEvent;
import rozapp.roz.app.events.UserDisconnectedEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentFollowing extends Fragment {


    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.pp)
    ProgressBar pp;
    @BindView(R.id.swip)
    SwipeRefreshLayout swip;
    private AuthResponse authResponse;

    private List<DashboardUser> users;
    private FollowAdabter dashboardAdabter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_following, container, false);
        ButterKnife.bind(this ,v);
        ui();
        getDashboardUser("followed");
        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swip.setRefreshing(true);
                getDashboardUser("followed");
            }
        });
        return v;
    }

    private void getDashboardUser( String filter ) {

        users.clear();
        dashboardAdabter.notifyDataSetChanged();
        swip.setRefreshing(true);
        pp.setVisibility(View.GONE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getDashboardUsers(filter).enqueue(new Callback<List<DashboardUser>>() {
            @Override
            public void onResponse(Call<List<DashboardUser>> call, Response<List<DashboardUser>> response) {

                if (response.code()==200){
                    users.clear();
                    for (DashboardUser user:response.body()) {
                        users.add(user);
                    }
                }
                dashboardAdabter.notifyDataSetChanged();
                pp.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                swip.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<DashboardUser>> call, Throwable t) {
                pp.setVisibility(View.GONE);
                swip.setRefreshing(false);
            }
        });

    }


    private void ui() {
        authResponse=new CallData(getContext()).getAuthResponse();
        users=new ArrayList<>();
        dashboardAdabter=new FollowAdabter(getContext(),users);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(dashboardAdabter);

    }
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
        for (int i=0;i<users.size();i++){
            if(event.getId() == users.get(i).getId()){
                users.get(i).setOnline("1");
                break;
            }

        }
        dashboardAdabter.notifyDataSetChanged();

    }

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
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onRefreshDashboardEvent(RefreshDashboardEvent event) {
//        getDashboardUser("followed");
//    }
}