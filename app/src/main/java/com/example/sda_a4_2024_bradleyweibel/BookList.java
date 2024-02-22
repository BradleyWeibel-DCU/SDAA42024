package com.example.sda_a4_2024_bradleyweibel;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Images used are sourced from Public Domain Day 2019.
 * by Duke Law School's Center for the Study of the Public Domain
 * is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * A simple {@link Fragment} subclass.
 * @author Chris Coughlan
 */

public class BookList extends Fragment {

    private static final String TAG = "BookList";
    public BookList() { } // Required empty public constructor

    // Array for each value of a book
    private ArrayList<String> idList = new ArrayList<>();
    private ArrayList<String> authorList = new ArrayList<>();
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> bookCoverURLList = new ArrayList<>();
    private RecyclerView recyclerView;

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * Used to get the book data from the Firebase Database and pass the arrays to the LibraryViewAdapter to create each item in the UI list.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_book_list, container, false);

        recyclerView = root.findViewById(R.id.bookView_view);

        // Get default FirebaseDatabase instance
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        // Get reference for database containing all library books and their info
        DatabaseReference databaseLibraryReference = firebaseDatabase.getReference(Helper.LibraryBooks_Database);

        // Loop to add each book's data to the appropriate array
        for (int i = 1; i <= 14; i++)
        {
            // Get book details at specific key - for example: sku10001
            databaseLibraryReference.child(Helper.DBEntry_RootName + i).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        if (task.getResult().exists())
                        {
                            Log.i(TAG, "------------------------- Successfully Read entry  -------------------------");
                            // Get desired row/entry from Library_Books table
                            DataSnapshot dataSnapshot = task.getResult();
                            // Get cells in table entry
                            idList.add(String.valueOf(dataSnapshot.getKey()));
                            authorList.add(String.valueOf(dataSnapshot.child(Helper.LibraryBook_Author).getValue()));
                            titleList.add(String.valueOf(dataSnapshot.child(Helper.LibraryBook_Title).getValue()));
                            bookCoverURLList.add(String.valueOf(dataSnapshot.child(Helper.LibraryBook_Cover).getValue()));
                            // Check if UI Book tab can be populated due to all data being present in lists
                            populateBookList();
                        }
                        else
                            Log.e(TAG, "------------------------- No entry exists -------------------------");
                    }
                    else
                        Log.e(TAG, "------------------------- Failed to read -------------------------");
                }
            });
        }
        return root;
    }

    // Only populate the Books tab when all Library_Books table data has been retrieved from the Firebase Realtime Database

    /**
     * populateBookList() is called each time a response comes from the database, however the
     * method only passes the values to the LibraryViewAdapter once all 14 DB entries are in
     * each array.
     */
    private void populateBookList()
    {
        if (idList.size() == 14 && authorList.size() == 14 && titleList.size() == 14 && bookCoverURLList.size() == 14)
        {
            Log.i(TAG, "----------------------------- Ready to populate Book list page !!!!!!!!!!!!!");
            LibraryViewAdapter recyclerViewAdapter = new LibraryViewAdapter(getContext(), idList, authorList, titleList, bookCoverURLList);
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Log.i(TAG, "----------------------------- Finished populating Book list page !!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        else
            Log.i(TAG, "----------------------------- NOT Ready to populate Book page yet!");
    }
}
