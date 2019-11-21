package com.tuzla.database.mRecycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tuzla.derzil3.R;

public class MyHolder extends RecyclerView.ViewHolder {

    TextView nametxt;
    TextView zamantxt;
    TextView gunlertxt;
    ImageView editImage;
    RelativeLayout bgItem;

    public MyHolder(View itemView) {
        super(itemView);

        this.nametxt = itemView.findViewById(R.id.nameTxt);
        this.zamantxt = itemView.findViewById(R.id.zamanTxt);
        this.gunlertxt = itemView.findViewById(R.id.textViewDays);
        this.editImage = itemView.findViewById(R.id.editimageView);
        this.bgItem = itemView.findViewById(R.id.itemBG);
    }

}
