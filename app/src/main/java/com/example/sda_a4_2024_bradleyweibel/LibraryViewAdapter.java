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
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

/*
 * @author Chris Coughlan 2019
 */

public class LibraryViewAdapter extends RecyclerView.Adapter<LibraryViewAdapter.ViewHolder> {

    private SharedPreferences userDetails, bookDetails;
    private Context mNewContext;
    // Arrays for each aspect of a book
    private ArrayList<String> idList, authorList, titleList, bookCoverUrlList;

    // Get context for Glide and lists containing string data
    LibraryViewAdapter(Context mNewContext, ArrayList<String> idList, ArrayList<String> authorList, ArrayList<String> titleList, ArrayList<String> bookCoverURLList) {
        this.mNewContext = mNewContext;
        this.idList = idList;
        this.authorList = authorList;
        this.titleList = titleList;
        this.bookCoverUrlList = bookCoverURLList;
    }

    // Declare methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position)
    {
        // Populate this Book's details
        // Get Id text
        viewHolder.checkOutBtn.setTag(idList.get(position));
        // Get Author text
        viewHolder.authorText.setText(authorList.get(position));
        // Get Book title text
        viewHolder.titleText.setText(titleList.get(position));
        // Use Glide to get the image from the URL and insert it into the imageItem object
        Glide.with(mNewContext).load(bookCoverUrlList.get(position)).apply(new RequestOptions()).into(viewHolder.imageItem);

        // Checkout button clicked
        viewHolder.checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Check if user has an account before proceeding to book checkout
                // Get shared preferences
                userDetails = mNewContext.getSharedPreferences("UserDetailsPreferences", Context.MODE_PRIVATE);
                // Get user id in SharedPreferences
                String currentSavedUserId = userDetails.getString("id", "");
                // If no details exist, show error message
                if (currentSavedUserId.equals(""))
                    showToast(mNewContext.getString(R.string.create_account_error), mNewContext);
                else {
                    // Account accepted!
                    // Get book details
                    String bookId = v.getTag().toString();
                    // Get default FirebaseDatabase instance
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    // Get reference for database containing all library books and their info
                    DatabaseReference databaseLibraryReference = firebaseDatabase.getReference("Library_Books");
                    databaseLibraryReference.child(bookId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            // Get desired book entry from Library_Books table
                            DataSnapshot dataSnapshot = task.getResult();
                            // Get cells in table entry
                            String author = dataSnapshot.child("Author").getValue().toString();
                            String title = dataSnapshot.child("Title").getValue().toString();
                            String cover = dataSnapshot.child("Cover").getValue().toString();
                            // Save details in SharedPreferences
                            // Initiate book details shared preferences
                            bookDetails = mNewContext.getSharedPreferences("BookDetailsPreferences", Context.MODE_PRIVATE);
                            // Create edit shared preferences variable
                            SharedPreferences.Editor editor = bookDetails.edit();
                            // Insert values
                            editor.putString("bookId", bookId);
                            editor.putString("author", author);
                            editor.putString("title", title);
                            editor.putString("cover", cover);
                            editor.apply();

                            // Open checkout page
                            Intent myOrder = new Intent(mNewContext, CheckOut.class);
                            mNewContext.startActivity(myOrder);
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    // Attach the variables to the UI elements in the recycler_list_item.xml
    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageItem;
        TextView authorText, titleText;
        Button checkOutBtn;
        RelativeLayout itemParentLayout;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Attach the image, textViews to the UI counterparts and get the checkout button
            imageItem = itemView.findViewById(R.id.bookImage);
            authorText = itemView.findViewById(R.id.authorText);
            titleText = itemView.findViewById(R.id.bookTitle);
            checkOutBtn = itemView.findViewById(R.id.out_button);
            itemParentLayout = itemView.findViewById(R.id.listItemLayout);
        }
    }
}
