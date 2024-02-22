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
    LibraryViewAdapter(Context mNewContext, ArrayList<String> idList, ArrayList<String> authorList, ArrayList<String> titleList, ArrayList<String> bookCoverURLList)
    {
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
        viewHolder.checkOutBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if user has an account before proceeding to book checkout
                // Get user SharedPreferences
                userDetails = mNewContext.getSharedPreferences(Helper.UserDetails_SharedPreferences, Context.MODE_PRIVATE);
                // Get user id in SharedPreferences
                String currentSavedUserId = userDetails.getString(Helper.UserDetails_Preference_Id, "");
                // If no details exist, show error message
                if (currentSavedUserId.equals(""))
                    showToast(mNewContext.getString(R.string.create_account_error), mNewContext);
                else
                {
                    // Account accepted!
                    // Get book details
                    String bookId = v.getTag().toString();
                    // Get default FirebaseDatabase instance
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    // Get references for database containing all library books, bookings and their info
                    DatabaseReference databaseLibraryReference = firebaseDatabase.getReference(Helper.LibraryBooks_Database);
                    DatabaseReference databaseBookingsReference = firebaseDatabase.getReference(Helper.LibraryBookings_Database);
                    databaseLibraryReference.child(bookId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task)
                        {
                            // Get desired book entry from Library_Books table
                            DataSnapshot dataSnapshot = task.getResult();
                            // Get cells in table entry
                            String author = dataSnapshot.child(Helper.LibraryBook_Author).getValue().toString();
                            String title = dataSnapshot.child(Helper.LibraryBook_Title).getValue().toString();
                            String cover = dataSnapshot.child(Helper.LibraryBook_Cover).getValue().toString();
                            // Save details in SharedPreferences
                            // Initiate book details shared preferences
                            bookDetails = mNewContext.getSharedPreferences(Helper.BookDetails_SharedPreferences, Context.MODE_PRIVATE);
                            // Create edit shared preferences variable
                            SharedPreferences.Editor editor = bookDetails.edit();
                            // Insert values
                            editor.putString(Helper.BookDetails_Preference_BookId, bookId);
                            editor.putString(Helper.BookDetails_Preference_Author, author);
                            editor.putString(Helper.BookDetails_Preference_Title, title);
                            editor.putString(Helper.BookDetails_Preference_Cover, cover);
                            editor.apply();

                            // Get availability information
                            databaseBookingsReference.child(bookId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task)
                                {
                                    // Get desired book entry from Library_Bookings table
                                    DataSnapshot dataSnapshot = task.getResult();
                                    // Get cells in table entry
                                    Boolean availabilityStatus = Boolean.parseBoolean(dataSnapshot.child(Helper.LibraryBookings_Availability).getValue().toString());
                                    String bookedByUserId = dataSnapshot.child(Helper.LibraryBookings_User_Id).getValue().toString();
                                    String bookedFrom = dataSnapshot.child(Helper.LibraryBookings_Booked_From).getValue().toString();
                                    String bookedTill = dataSnapshot.child(Helper.LibraryBookings_Booked_Till).getValue().toString();

                                    // Insert values into SharedPreferences
                                    editor.putBoolean(Helper.BookDetails_Preference_Availability, availabilityStatus);
                                    editor.putString(Helper.BookDetails_Preference_UserId, bookedByUserId);
                                    editor.putString(Helper.BookDetails_Preference_Booked_From, bookedFrom);
                                    editor.putString(Helper.BookDetails_Preference_Booked_Till, bookedTill);
                                    editor.apply();

                                    // Open checkout page
                                    Intent myOrder = new Intent(mNewContext, CheckOut.class);
                                    mNewContext.startActivity(myOrder);
                                }
                            });
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

        ViewHolder(@NonNull View itemView)
        {
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
