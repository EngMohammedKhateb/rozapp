package rozapp.roz.app.adabters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.models.VideoChatMessage;

public class VideoChatAdaber extends RecyclerView.Adapter<VideoChatAdaber.ViewHolder> {

    private Context context;
    private List<VideoChatMessage> messages;

    public VideoChatAdaber(Context context, List<VideoChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_video_message,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tv.setText(messages.get(position).getFrom_name()+" : "+messages.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message_tv)
        TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
