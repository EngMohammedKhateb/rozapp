package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.models.ChatCategory;

public class CategoryAdabter extends RecyclerView.Adapter<CategoryAdabter.CategoryViewHolder> {

    Context context;
    List<ChatCategory> data;

    public CategoryAdabter(Context context,List<ChatCategory> data){
        this.data=data;
        this.context=context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.row_category_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picasso.with(context).load(Constants.Image_URL_Slashed+data.get(position).getIcon()).into(holder.imageView);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(data.get(position));
            }
        });
        if (data.get(position).getSelected().equals("yes")){
            holder.tab_border.setVisibility(View.VISIBLE);
        }else{
            holder.tab_border.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.row)
        RelativeLayout relativeLayout;
        @BindView(R.id.tab_border)
        View tab_border;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
