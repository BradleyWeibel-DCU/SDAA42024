package com.example.sda_a4_2024_bradleyweibel;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.example.sda_a4_2024_bradleyweibel.Helper.showToast;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;

/*
 * @author Chris Coughlan 2019
 */

public class LibraryViewAdapter extends RecyclerView.Adapter<LibraryViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private Context mNewContext;
    // Arrays for each aspect of a book
    private ArrayList<String> authorList, titleList, bookCoverUrlList;

    // Get context for Glide and lists containing string data
    LibraryViewAdapter(Context mNewContext, ArrayList<String> author, ArrayList<String> title, ArrayList<String> bookCoverURL) {
        this.mNewContext = mNewContext;
        this.authorList = author;
        this.titleList = title;
        this.bookCoverUrlList = bookCoverURL;
    }

    // Declare methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position)
    {
        // Populate this Book's details
        // Get Author text
        viewHolder.authorText.setText(authorList.get(position));
        // Get Book title text
        viewHolder.titleText.setText(titleList.get(position));
        // Use Glide to get the image from the URL and insert it into the imageItem object
        Glide.with(mNewContext).load(bookCoverUrlList.get(position)).apply(new RequestOptions()).into(viewHolder.imageItem);

        // TODO should check here to see if the book is available.
        viewHolder.checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showToast(titleList.get(position), mNewContext);
                //...
                Intent myOrder = new Intent (mNewContext, CheckOut.class);
                mNewContext.startActivity(myOrder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    // Populate the UI elements using the view holder class for the UI side recycler_list_item.xml
    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageItem;
        TextView authorText;
        TextView titleText;
        Button checkOut;
        RelativeLayout itemParentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //grab the image, the text and the layout id's
            imageItem = itemView.findViewById(R.id.bookImage);
            authorText = itemView.findViewById(R.id.authorText);
            titleText = itemView.findViewById(R.id.bookTitle);
            checkOut = itemView.findViewById(R.id.out_button);
            itemParentLayout = itemView.findViewById(R.id.listItemLayout);
        }
    }
}
