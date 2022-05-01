package rozapp.roz.app.pages;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.ContactAdabter;

import rozapp.roz.app.adabters.StroyAdabter;
import rozapp.roz.app.events.ChatMessageEvent;
import rozapp.roz.app.events.DeleteContactEvent;
import rozapp.roz.app.events.RefreshContactEvent;
import rozapp.roz.app.events.UserConnectedEvent;
import rozapp.roz.app.events.UserDisconnectedEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.models.DashboardUser;
import rozapp.roz.app.models.ErrorHandler;
import rozapp.roz.app.users.AllUsersActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatFragment extends Fragment {

    @BindView(R.id.pp_contact)
    ProgressBar pp_contact;

    @BindView(R.id.rv_contact)
    RecyclerView rvContact;

    @BindView(R.id.add_new)
    LinearLayout add_new;
    @BindView(R.id.swip)
    SwipeRefreshLayout swip;



    @BindView(R.id.ed_search)
    EditText ed_search;
    @BindView(R.id.btn_search)
    ImageView btn_search;


    private AuthResponse authResponse;

    private List<Contact> users;
    private List<DashboardUser> matches;
    private ContactAdabter contactAdabter;
    private StroyAdabter storyAdabter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this,v);
        authResponse=new CallData(getContext()).getAuthResponse();
        ui();
        getLatest();
        getContacts();
        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getContacts();
            }
        });

        return v;
    }

    private void getLatest() {


        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                contactAdabter.getFilter().filter(charSequence);
                Log.e("filter :",ed_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getContacts() {
        users.clear();
        rvContact.setVisibility(View.GONE);
        pp_contact.setVisibility(View.GONE);
        swip.setRefreshing(true);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getContacts().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if(response.code()==200){
                    users.clear();
                    for(Contact contact:response.body()){
                        users.add(contact);
                    }
                    contactAdabter.notifyDataSetChanged();
                    rvContact.setVisibility(View.VISIBLE);
                    swip.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
                rvContact.setVisibility(View.VISIBLE);
                swip.setRefreshing(false);
            }
        });


    }

    private void ui() {
        users=new ArrayList<>();
        contactAdabter=new ContactAdabter(getContext(),users);
        rvContact.setHasFixedSize(true);
        rvContact.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContact.setAdapter(contactAdabter);


        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AllUsersActivity.class));
            }
        });
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
    public void onChatMessageEvent(ChatMessageEvent message) {
        if(message.getToUser()==authResponse.getUser().getId())
            refreshContacts();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshContactEvent(RefreshContactEvent event) {
        refreshContacts();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserConnectedEvent(UserDisconnectedEvent event) {
            for (int i=0;i<users.size();i++){
                if(event.getId() == users.get(i).getId()){
                    users.get(i).setOnline("1");
                    break;
                }
            }
            contactAdabter.notifyDataSetChanged();

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserDisconnectedEvent(UserConnectedEvent event) {
        for (int i=0;i<users.size();i++){
            if(event.getId() == users.get(i).getId()){
                users.get(i).setOnline("0");
                break;
            }
        }
        contactAdabter.notifyDataSetChanged();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteContactEvent(DeleteContactEvent event) {
        for (int i=0;i<users.size();i++){
            if(event.getContact_id() == users.get(i).getId()){
                users.remove(users.get(i));
                break;
            }
        }
        contactAdabter.notifyDataSetChanged();

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).deleteUserContact(event.getContact_id()+"").enqueue(new Callback<ErrorHandler>() {
            @Override
            public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) { }
            @Override
            public void onFailure(Call<ErrorHandler> call, Throwable t) { }
        });


    }

    private void refreshContacts(){
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getContacts().enqueue(new Callback<List<Contact>>() {
            @Override
            public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                if(response.code()==200){
                    users.clear();
                    for(Contact contact:response.body()){
                        users.add(contact);
                    }
                    contactAdabter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Contact>> call, Throwable t) {
            }
        });
    }

}