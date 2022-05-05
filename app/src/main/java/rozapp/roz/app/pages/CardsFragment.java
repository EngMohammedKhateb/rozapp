package rozapp.roz.app.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.TopTenAdabter;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.TopUser;
import rozapp.roz.app.profile.TargetProfileActivity;

public class CardsFragment extends Fragment {

    @BindView(R.id.rv_top)
    RecyclerView rv_top;

    @BindView(R.id.top1_image)
    CircleImageView top1_image;
    @BindView(R.id.top2_image)
    CircleImageView top2_image;
    @BindView(R.id.top3_image)
    CircleImageView top3_image;

    @BindView(R.id.top1_name)
    TextView top1_name;
    @BindView(R.id.top2_name)
    TextView top2_name;
    @BindView(R.id.top3_name)
    TextView top3_name;

    @BindView(R.id.top1_coins)
    TextView top1_coins;
    @BindView(R.id.top2_coins)
    TextView top2_coins;
    @BindView(R.id.top3_coins)
    TextView top3_coins;

    @BindView(R.id.pp)
    ProgressBar pp;
    @BindView(R.id.page)
    RelativeLayout page;


    private List<TopUser> topthree;
    private List<TopUser> topUsers;

    private TopTenAdabter topTenAdabter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_cards, container, false);
        ButterKnife.bind(this,v);

        topthree=new ArrayList<>();
        topUsers=new ArrayList<>();
        topTenAdabter=new TopTenAdabter(getContext(),topUsers);
        rv_top.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_top.setAdapter(topTenAdabter);

        getTopUsers();

        setTopThree();

        return v;
    }

    private void setTopThree() {
        if(topthree.size()>0){

            TopUser top1=topthree.get(0);
            TopUser top2=topthree.get(1);
            TopUser top3=topthree.get(2);


            top1_name.setText(top1.getName());
            top1_coins.setText(top1.getRate()+"");
            Picasso.with(getContext()).load(Constants.Image_URL+top1.getImage()).into(top1_image);

            top1_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), TargetProfileActivity.class).putExtra("target_id",top1.getId()+""));
                }
            });
            top1_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), TargetProfileActivity.class).putExtra("target_id",top1.getId()+""));
                }
            });


            top2_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), TargetProfileActivity.class).putExtra("target_id",top2.getId()+""));
                }
            });
            top2_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), TargetProfileActivity.class).putExtra("target_id",top2.getId()+""));
                }
            });

            top3_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), TargetProfileActivity.class).putExtra("target_id",top3.getId()+""));
                }
            });
            top3_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), TargetProfileActivity.class).putExtra("target_id",top3.getId()+""));
                }
            });

            top2_name.setText(top2.getName());
            top2_coins.setText(top2.getRate()+"");
            Picasso.with(getContext()).load(Constants.Image_URL+top2.getImage()).into(top2_image);

            top3_name.setText(top3.getName());
            top3_coins.setText(top3.getRate()+"");
            Picasso.with(getContext()).load(Constants.Image_URL+top3.getImage()).into(top3_image);




        }
    }

    private void getTopUsers() {
        pp.setVisibility(View.VISIBLE);
        page.setVisibility(View.GONE);
        KhateebPattern.getAuthServicesInstance(new CallData(getContext()).getAuthResponse().getAccessToken()).getTopTen().enqueue(new Callback<List<TopUser>>() {
            @Override
            public void onResponse(Call<List<TopUser>> call, Response<List<TopUser>> response) {

                if(response.code()==200){
                    if(response.body().size()>4){
                        for (TopUser user:response.body()) {
                            topthree.add(user);
                        }
                        for (int i=3 ;i<response.body().size();i++){
                            topUsers.add(response.body().get(i));
                        }
                        setTopThree();
                        topTenAdabter.notifyDataSetChanged();
                        pp.setVisibility(View.GONE);
                        page.setVisibility(View.VISIBLE);
                    }


                }


            }

            @Override
            public void onFailure(Call<List<TopUser>> call, Throwable t) {
                pp.setVisibility(View.GONE);
                page.setVisibility(View.VISIBLE);
            }
        });


    }
}