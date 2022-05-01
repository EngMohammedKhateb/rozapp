package rozapp.roz.app.adabters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.models.Plan;
import rozapp.roz.app.setting.CheckOutPlan;

public class PlanAdabter extends RecyclerView.Adapter<PlanAdabter.PlanViewHolder> {

    private Context context;
    private List<Plan> data;

    public PlanAdabter(Context context, List<Plan> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlanViewHolder(LayoutInflater.from(context).inflate(R.layout.row_plan,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, @SuppressLint("RecyclerView") int position) {
           holder.plan_name_tv.setText(data.get(position).getName());
           holder.coins_tv.setText(data.get(position).getCoins()+" token");
           holder.messages_tv.setText(data.get(position).getMessages()+" message");
           holder.price_tv.setText(" $ "+data.get(position).getPrice());
           holder.pay_plan.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent intent=new Intent(context, CheckOutPlan.class);
                   intent.putExtra("plan_id",data.get(position).getId()+"");
                   intent.putExtra("amount",data.get(position).getPrice()+"");
                   context.startActivity(intent);
               }
           });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.plan_name_tv)
        TextView plan_name_tv;

        @BindView(R.id.coins_tv)
        TextView coins_tv;

        @BindView(R.id.messages_tv)
        TextView messages_tv;

        @BindView(R.id.price_tv)
        TextView price_tv;

        @BindView(R.id.pay_plan)
        RelativeLayout pay_plan;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
