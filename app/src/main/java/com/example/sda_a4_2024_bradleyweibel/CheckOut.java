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
import java.util.Calendar;

public class CheckOut extends AppCompatActivity {

    private Context mNewContext;
    private SharedPreferences userDetails, bookDetails;
    TextView displaySummary, bookAuthor, bookTitle, bookAvailability;
    ImageView bookCover;
    FloatingActionButton backBtn;
    Button selectDateBtn, placeOrderBtn, returnBookBtn;
    Calendar calenderChosenDateTime = Calendar.getInstance();
    long currentDateTime = calenderChosenDateTime.getTimeInMillis();

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

        // Get context
        mNewContext = getApplicationContext();

        // Get user SharedPreferences
        userDetails = mNewContext.getSharedPreferences("UserDetailsPreferences", Context.MODE_PRIVATE);
        // Get user id in SharedPreferences
        String userId = userDetails.getString("id", "");

        // Get book details stored in SharedPreferences
        bookDetails = mNewContext.getSharedPreferences("BookDetailsPreferences", Context.MODE_PRIVATE);
        //String currentBookId = bookDetails.getString("bookId", "");
        String currentAuthor = bookDetails.getString("author", "");
        String currentTitle = bookDetails.getString("title", "");
        String currentCoverURL = bookDetails.getString("cover", "");
        Boolean currentAvailabilityStatus = bookDetails.getBoolean("availability", false);
        String currentlyBookedByUserId = bookDetails.getString("bookedByUserId", "");
        String currentlyBookedFrom = bookDetails.getString("bookedFrom", "");

        // Display book elements in UI
        bookTitle.setText(currentTitle);
        bookAuthor.setText(currentAuthor);
        Glide.with(mNewContext).load(currentCoverURL).apply(new RequestOptions()).into(bookCover);

        // Button handling
        if (currentAvailabilityStatus)
        {
            bookAvailability.setText(R.string.available_text);
            // Book is available
            // User can select a reserve date for the book
            setClickabilityOfSelectDateBtn(true);
            // User must first choose a date to submit the order
            setClickabilityOfPlaceOrderBtn(false);
            // User cannot return the book
            setClickabilityOfReturnBookBtn(false);
        }
        else
        {
            // Book is unavailable
            bookAvailability.setText(R.string.unavailable_text);
            setClickabilityOfSelectDateBtn(false);
            setClickabilityOfPlaceOrderBtn(false);
            if (currentlyBookedByUserId.equals(userId))     // Current user has reserved the book, allow user to RETURN the book
                setClickabilityOfReturnBookBtn(true);
            else                                            // Book is reserved by another user
                setClickabilityOfReturnBookBtn(false);
        }

        // Attach summary textview variable to UI element
        displaySummary = findViewById(R.id.orderSummary);

        // Button to return to previous page is clicked
        backBtn.setOnClickListener(new View.OnClickListener() {
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
                // Check if chosen date is already passed
                long chosenDate = calenderChosenDateTime.getTimeInMillis();
                if (currentDateTime > chosenDate)
                {
                    // Chosen date is in the past
                    showToast(getString(R.string.passed_date_error), getApplicationContext());
                    clearDateAndTimeDisplay();
                }
                else // Chosen date is in the future
                    updateDateAndTimeDisplay(chosenDate);
            }
        };
        // Show date picker popup
        new DatePickerDialog(CheckOut.this, mDateListener, calenderChosenDateTime.get(Calendar.YEAR), calenderChosenDateTime.get(Calendar.MONTH), calenderChosenDateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Return-book button clicked
    public void onReturnBookClicked(View v)
    {
        // Remove user data from Firebase Database
        // TODO
        // Reload page
    }

    // Place order button clicked
    public void onPlaceOrderClicked(View v)
    {
        // Submit user data to Firebase Database
        // TODO
        // Reload page
    }

    // Show chosen date and time in summary section
    private void updateDateAndTimeDisplay(long chosenDate)
    {
        //date time year
        CharSequence currentTime = DateUtils.formatDateTime(this, chosenDate, DateUtils.FORMAT_SHOW_TIME);
        CharSequence SelectedDate = DateUtils.formatDateTime(this, chosenDate, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String finalSummary = SelectedDate + " current time is " + currentTime;
        displaySummary.setText(finalSummary);
        setClickabilityOfPlaceOrderBtn(true);
    }

    // Clear date and time summary section
    private void clearDateAndTimeDisplay()
    {
        displaySummary.setText("");
    }
}
