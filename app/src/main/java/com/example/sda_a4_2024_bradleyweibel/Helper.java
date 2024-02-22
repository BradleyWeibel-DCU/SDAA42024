package com.example.sda_a4_2024_bradleyweibel;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

public class Helper {

    // Firebase database names
    public final static String LibraryBooks_Database = "Library_Books";
    public final static String LibraryBookings_Database = "Library_Bookings";
    // Firebase column key names
    // Books
    public final static String LibraryBook_Title = "Title";
    public final static String LibraryBook_Author = "Author";
    public final static String LibraryBook_Cover = "Cover";
    // Bookings
    public final static String LibraryBookings_Availability = "Availability";
    public final static String LibraryBookings_Booked_From = "Booked_From";
    public final static String LibraryBookings_Booked_Till = "Booked_Till";
    public final static String LibraryBookings_Order_Placed = "Order_Placed";
    public final static String LibraryBookings_User_Id = "User_Id";

    // SharedPreference names
    public final static String UserDetails_SharedPreferences = "UserDetailsPreferences";
    public final static String BookDetails_SharedPreferences = "BookDetailsPreferences";
    // SharedPreference key names
    // User details
    public final static String UserDetails_Preference_Id = "id";
    public final static String UserDetails_Preference_Name = "name";
    public final static String UserDetails_Preference_Address = "address";
    public final static String UserDetails_Preference_Zip = "zip";
    public final static String UserDetails_Preference_Town = "town";
    public final static String UserDetails_Preference_Phone = "phone";
    public final static String UserDetails_Preference_Email = "email";
    // Book details
    public final static String BookDetails_Preference_BookId = "bookId";
    public final static String BookDetails_Preference_Author = "author";
    public final static String BookDetails_Preference_Title = "title";
    public final static String BookDetails_Preference_Cover = "cover";
    public final static String BookDetails_Preference_Availability = "availability";
    public final static String BookDetails_Preference_UserId = "bookedByUserId";
    public final static String BookDetails_Preference_Booked_From = "bookedFrom";
    public final static String BookDetails_Preference_Booked_Till = "bookedTill";
    public final static String BookDetails_Preference_Time_of_Booking = "timeOfBooking";

    // General
    public final static String DBEntry_RootName = "sku1000";
    public final static String UserName_Counter = "_001";

    // Show a customized toast message
    public static void showToast(String message, Context context)
    {
        // Create toast object
        Toast toastr = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = toastr.getView();
        // Make changes to colour appearance
        view.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.colorToast), PorterDuff.Mode.SRC_IN);
        // Insert message
        TextView text = view.findViewById(android.R.id.message);
        // Set text colour
        text.setTextColor(ContextCompat.getColor(context, R.color.colorToastText));
        // Show toast
        toastr.show();
    }
}
