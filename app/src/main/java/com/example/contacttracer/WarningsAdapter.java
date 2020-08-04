package com.example.contacttracer;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.contacttracer.models.Warning;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class WarningsAdapter extends RecyclerView.Adapter<WarningsAdapter.ViewHolder>{

    private Context context;
    private List<Warning> warnings;

    private ImageView ivProfilePicture;
    private TextView tvUser;
    private TextView tvTime;
    private TextView tvLoc;

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


        final Warning warning = warnings.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Clicked card", Toast.LENGTH_SHORT).show();
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);

                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                tvLoc =popupView.findViewById(R.id.tvLoc);
                tvUser =popupView.findViewById(R.id.tvUsername);
                tvTime =popupView.findViewById(R.id.tvTime);
                ivProfilePicture =popupView.findViewById(R.id.ivProfilePic);

                ParseUser thisUser = warning.getOtherUser();
                ParseFile image = thisUser.getParseFile("Image");
                if(image != null){
                    Glide.with(context).load(image.getUrl()).into(ivProfilePicture);
                }
                tvUser.setText(warning.getOtherUser().getUsername());
                tvTime.setText(warning.getCreatedTime());
                String[] address = warning.getLocation().split(",");
                String city = address[1];
                tvLoc.setText(city);



                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });
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
            tvUsername.setText(warning.getOtherUser().getUsername());
            tvTimeStamp.setText(warning.getCreatedTime());
            String[] address = warning.getLocation().split(",");
            String city = address[1];
            tvLocation.setText(city);

            //tvLocation.setText(warning.getLocation());

        }

    }
}
