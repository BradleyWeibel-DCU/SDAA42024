package com.example.sda_a4_2024_bradleyweibel;

import static com.example.sda_a4_2024_bradleyweibel.Helper.showToast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;

public class CheckOut extends AppCompatActivity {

    private Context mNewContext;
    private SharedPreferences userDetails, bookDetails;
    private TextView bookAuthor, bookTitle, bookAvailability, userIdLabel, userIdHolder, userNameLabel, userNameHolder, checkoutDateLabel, checkoutDateHolder, returnDateLabel, returnDateHolder;
    private ImageView bookCover;
    private FloatingActionButton backBtn;
    private Button selectDateBtn, placeOrderBtn, returnBookBtn;
    private Calendar calenderChosenDateTime = Calendar.getInstance();
    private long currentDateTime = calenderChosenDateTime.getTimeInMillis();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseBookingsReference = firebaseDatabase.getReference(Helper.LibraryBookings_Database);;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        //set the toolbar we have overridden
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Map variables to UI elements
        bookTitle = findViewById(R.id.bookTitleRight);
        bookAuthor = findViewById(R.id.bookAuthorRight);
        bookCover = findViewById(R.id.bookImage);
        bookAvailability = findViewById(R.id.bookAvailabilityRight);
        backBtn = findViewById(R.id.backToListBtn);
        selectDateBtn = findViewById(R.id.selectDateBtn);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        returnBookBtn = findViewById(R.id.returnBookBtn);
        userIdLabel = findViewById(R.id.userIdLeft);
        userIdHolder = findViewById(R.id.userIdRight);
        userNameLabel = findViewById(R.id.userNameLeft);
        userNameHolder = findViewById(R.id.userNameRight);
        checkoutDateLabel = findViewById(R.id.checkoutDateLeft);
        checkoutDateHolder = findViewById(R.id.checkoutDateRight);
        returnDateLabel = findViewById(R.id.returnDateLeft);
        returnDateHolder = findViewById(R.id.returnDateRight);

        // Get context
        mNewContext = getApplicationContext();

        // Get user SharedPreferences
        userDetails = mNewContext.getSharedPreferences(Helper.UserDetails_SharedPreferences, Context.MODE_PRIVATE);
        // Get user id in SharedPreferences
        String userId = userDetails.getString(Helper.UserDetails_Preference_Id, "");
        // Get book details stored in SharedPreferences
        bookDetails = mNewContext.getSharedPreferences(Helper.BookDetails_SharedPreferences, Context.MODE_PRIVATE);
        String currentAuthor = bookDetails.getString(Helper.BookDetails_Preference_Author, "");
        String currentTitle = bookDetails.getString(Helper.BookDetails_Preference_Title, "");
        String currentCoverURL = bookDetails.getString(Helper.BookDetails_Preference_Cover, "");
        Boolean currentAvailabilityStatus = bookDetails.getBoolean(Helper.BookDetails_Preference_Availability, false);
        String currentlyBookedByUserId = bookDetails.getString(Helper.BookDetails_Preference_UserId, "");
        String currentlyBookedTill = bookDetails.getString(Helper.BookDetails_Preference_Booked_Till, "");

        // Display book elements in UI
        bookTitle.setText(currentTitle);
        bookAuthor.setText(currentAuthor);
        Glide.with(mNewContext).load(currentCoverURL).apply(new RequestOptions()).into(bookCover);
        // Hide UI elements not needed yet
        clearProposalSummaryDisplay();

        // Button handling
        if (currentAvailabilityStatus)
        {
            bookAvailability.setText(R.string.available_text);
            // Book is available
            // User can select a reserve date for the book
            setClickabilityOfSelectDateBtn(true);
            // User cannot return the book
            setClickabilityOfReturnBookBtn(false);
        }
        else
        {
            // Book is unavailable
            setClickabilityOfSelectDateBtn(false);
            if (currentlyBookedByUserId.equals(userId))
            {
                // Current user has reserved the book, allow user to RETURN the book
                String reservedText = getString(R.string.reserved_till_text) + " " + currentlyBookedTill + ")";
                bookAvailability.setText(reservedText);
                setClickabilityOfReturnBookBtn(true);
            }
            else
            {
                // Book is reserved by another user
                String reservedText = getString(R.string.unavailable_text) + " " + currentlyBookedTill + ")";
                bookAvailability.setText(reservedText);
                setClickabilityOfReturnBookBtn(false);
            }
        }

        // Button to return to previous page is clicked
        backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Return to Book list page
                finish();
            }
        });
    }

    // Clickability of button handling
    private void setClickabilityOfSelectDateBtn(Boolean status)
    {
        if (status)
        {
            selectDateBtn.setBackgroundColor(mNewContext.getColor(R.color.colorButtonBackground));
            selectDateBtn.setClickable(true);
        }
        else
        {
            selectDateBtn.setBackgroundColor(mNewContext.getColor(R.color.colorButtonDisabledBackground));
            selectDateBtn.setClickable(false);
        }
    }
    private void setClickabilityOfPlaceOrderBtn(Boolean status)
    {
        if (status)
        {
            placeOrderBtn.setBackgroundColor(mNewContext.getColor(R.color.colorButtonBackground));
            placeOrderBtn.setClickable(true);
        }
        else
        {
            placeOrderBtn.setBackgroundColor(mNewContext.getColor(R.color.colorButtonDisabledBackground));
            placeOrderBtn.setClickable(false);
        }
    }
    private void setClickabilityOfReturnBookBtn(Boolean status)
    {
        if (status)
        {
            returnBookBtn.setBackgroundColor(mNewContext.getColor(R.color.colorButtonBackground));
            returnBookBtn.setClickable(true);
        }
        else
        {
            returnBookBtn.setBackgroundColor(mNewContext.getColor(R.color.colorButtonDisabledBackground));
            returnBookBtn.setClickable(false);
        }
    }

    // Select-date button clicked
    public void onDateClicked(View v)
    {
        DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener()
        {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                // Date has been chosen
                calenderChosenDateTime.set(Calendar.YEAR, year);
                calenderChosenDateTime.set(Calendar.MONTH, monthOfYear);
                calenderChosenDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // Handle booked till date (standard 14 day period)
                Calendar calenderTillDate = Calendar.getInstance();
                calenderTillDate.set(Calendar.YEAR, year);
                calenderTillDate.set(Calendar.MONTH, monthOfYear);
                calenderTillDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calenderTillDate.add(Calendar.DAY_OF_YEAR, 14);
                // Check if chosen date is already passed
                long chosenFromDate = calenderChosenDateTime.getTimeInMillis();
                long chosenTillDate = calenderTillDate.getTimeInMillis();
                if (currentDateTime > chosenFromDate)
                {
                    // Chosen date is in the past
                    showToast(getString(R.string.passed_date_error), getApplicationContext());
                    clearProposalSummaryDisplay();
                }
                else // Chosen date is in the future
                    updateProposalDisplay(chosenFromDate, chosenTillDate);
            }
        };
        // Show date picker popup
        new DatePickerDialog(CheckOut.this, mDateListener, calenderChosenDateTime.get(Calendar.YEAR), calenderChosenDateTime.get(Calendar.MONTH), calenderChosenDateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Show chosen date and time in summary section
    private void updateProposalDisplay(long chosenDateLong, long proposedTillDateLong)
    {
        // Get current date, time, selected date and proposed date
        CharSequence currentTime = DateUtils.formatDateTime(this, chosenDateLong, DateUtils.FORMAT_SHOW_TIME);
        CharSequence currentDate = DateUtils.formatDateTime(this, currentDateTime, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String timeOfOrder = currentDate + " " + currentTime;
        CharSequence selectedDate = DateUtils.formatDateTime(this, chosenDateLong, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        CharSequence proposedTillDate = DateUtils.formatDateTime(this, proposedTillDateLong, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);

        // Insert proposal data in UI elements
        userIdHolder.setText(userDetails.getString(Helper.UserDetails_Preference_Id, ""));
        userNameHolder.setText(userDetails.getString(Helper.UserDetails_Preference_Name, ""));
        checkoutDateHolder.setText(selectedDate);
        returnDateHolder.setText(proposedTillDate);

        // Show UI elements
        userIdLabel.setVisibility(View.VISIBLE);
        userIdHolder.setVisibility(View.VISIBLE);
        userNameLabel.setVisibility(View.VISIBLE);
        userNameHolder.setVisibility(View.VISIBLE);
        checkoutDateLabel.setVisibility(View.VISIBLE);
        checkoutDateHolder.setVisibility(View.VISIBLE);
        returnDateLabel.setVisibility(View.VISIBLE);
        returnDateHolder.setVisibility(View.VISIBLE);
        // Make placeOrder button clickable
        setClickabilityOfPlaceOrderBtn(true);

        // Populate current date and time in SharedPreferences
        // Create edit shared preferences variable
        SharedPreferences.Editor editor = bookDetails.edit();
        // Insert values into SharedPreferences
        editor.putString(Helper.BookDetails_Preference_Time_of_Booking, timeOfOrder);
        editor.apply();
    }

    // Place order button clicked
    public void onPlaceOrderClicked(View v)
    {
        // Get data from UI elements and SharedPreferences
        String currentBookId = bookDetails.getString(Helper.BookDetails_Preference_BookId, "");
        String userId = userDetails.getString(Helper.UserDetails_Preference_Id, "");
        String selectedDate = checkoutDateHolder.getText().toString();
        String returnDate = returnDateHolder.getText().toString();
        String currentDateTime = bookDetails.getString(Helper.BookDetails_Preference_Time_of_Booking, "");

        // Submit user data to Firebase Database
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Availability).setValue(false);
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Booked_From).setValue(selectedDate);
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Booked_Till).setValue(returnDate);
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Order_Placed).setValue(currentDateTime);
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_User_Id).setValue(userId);

        // Update SharedPreferences
        // Create edit shared preferences variable
        SharedPreferences.Editor editor = bookDetails.edit();
        // Insert values into SharedPreferences
        editor.putBoolean(Helper.BookDetails_Preference_Availability, false);
        editor.putString(Helper.BookDetails_Preference_UserId, userId);
        editor.putString(Helper.BookDetails_Preference_Booked_From, selectedDate);
        editor.putString(Helper.BookDetails_Preference_Booked_Till, returnDate);
        editor.apply();

        // I chose to show a toast success rather than change the text in the summary section - I thought this to be cleaner
        showToast(getString(R.string.success_book_reserved), mNewContext);

        // Make final UI changes
        setClickabilityOfSelectDateBtn(false);
        setClickabilityOfPlaceOrderBtn(false);
        setClickabilityOfReturnBookBtn(true);
        String reservedText = getString(R.string.reserved_till_text) + " " + returnDate + ")";
        bookAvailability.setText(reservedText);
    }

    // Return-book button clicked
    public void onReturnBookClicked(View v)
    {
        // Remove user-data from Firebase Library_Bookings Database to release book
        // Get book Id
        String currentBookId = bookDetails.getString(Helper.BookDetails_Preference_BookId, "");
        // Update columns in desired entry/row
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Availability).setValue(true);
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Booked_From).setValue("");
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Booked_Till).setValue("");
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_Order_Placed).setValue("");
        databaseBookingsReference.child(currentBookId).child(Helper.LibraryBookings_User_Id).setValue("");

        // Update SharedPreferences
        // Create edit shared preferences variable
        SharedPreferences.Editor editor = bookDetails.edit();
        // Insert values into SharedPreferences
        editor.putBoolean(Helper.BookDetails_Preference_Availability, true);
        editor.putString(Helper.BookDetails_Preference_UserId, "");
        editor.putString(Helper.BookDetails_Preference_Booked_From, "");
        editor.putString(Helper.BookDetails_Preference_Booked_Till, "");
        editor.apply();

        // Reload page
        finish();
        startActivity(getIntent());
    }

    // Clear proposal summary section and disable place order button
    private void clearProposalSummaryDisplay()
    {
        setClickabilityOfPlaceOrderBtn(false);
        userIdLabel.setVisibility(View.INVISIBLE);
        userIdHolder.setVisibility(View.INVISIBLE);
        userNameLabel.setVisibility(View.INVISIBLE);
        userNameHolder.setVisibility(View.INVISIBLE);
        checkoutDateLabel.setVisibility(View.INVISIBLE);
        checkoutDateHolder.setVisibility(View.INVISIBLE);
        returnDateLabel.setVisibility(View.INVISIBLE);
        returnDateHolder.setVisibility(View.INVISIBLE);
    }
}
