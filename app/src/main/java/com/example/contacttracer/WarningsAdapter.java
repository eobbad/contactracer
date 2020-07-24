package com.example.contacttracer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.contacttracer.models.Warning;
import java.util.List;

public class WarningsAdapter extends RecyclerView.Adapter<WarningsAdapter.ViewHolder>{

    private Context context;
    private List<Warning> warnings;

    public WarningsAdapter(Context context, List<Warning> warnings){
        this.context = context;
        this.warnings = warnings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_warning,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Warning warning = warnings.get(position);
        holder.bind(warning);
    }


    @Override
    public int getItemCount() {
        return warnings.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements com.example.contacttracer.ViewHolder {

        private TextView tvUsername;
        private TextView tvDescription;
        private TextView tvTimeStamp;
        private TextView tvLocation;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvLocation = itemView.findViewById(R.id.tvLocation);


        }

        public void bind(Warning warning) {

            //for now I will keep the description to same for all warnings
            tvDescription.setText(warning.getDescription());
            tvUsername.setText(warning.getUser().getUsername());
            //I will have to do more logic to find the time the two users came in contact
            tvTimeStamp.setText("Saturday");
            //tvLocation.setText(warning.getLocation());
        }

    }
}
