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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.Calendar;

public class CheckOut extends AppCompatActivity {

    private Context mNewContext;
    private SharedPreferences bookDetails;
    TextView displaySummary, bookAuthor, bookTitle;
    ImageView bookCover;
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

        // Get book details stored in SharedPreferences
        mNewContext = getApplicationContext();
        bookDetails = mNewContext.getSharedPreferences("BookDetailsPreferences", Context.MODE_PRIVATE);
        //String currentBookId = bookDetails.getString("bookId", "");
        String currentAuthor = bookDetails.getString("author", "");
        String currentTitle = bookDetails.getString("title", "");
        String currentCoverURL = bookDetails.getString("cover", "");

        // Display book elements in UI
        bookTitle.setText(currentTitle);
        bookAuthor.setText(currentAuthor);
        Glide.with(mNewContext).load(currentCoverURL).apply(new RequestOptions()).into(bookCover);

        // Attach summary textview variable to UI element
        displaySummary = findViewById(R.id.orderSummary);
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

    // Show chosen date and time in summary section
    private void updateDateAndTimeDisplay(long chosenDate)
    {
        //date time year
        CharSequence currentTime = DateUtils.formatDateTime(this, chosenDate, DateUtils.FORMAT_SHOW_TIME);
        CharSequence SelectedDate = DateUtils.formatDateTime(this, chosenDate, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
        String finalSummary = SelectedDate + " current time is " + currentTime;
        displaySummary.setText(finalSummary);
    }

    // Clear date and time summary section
    private void clearDateAndTimeDisplay()
    {
        displaySummary.setText("");
    }
}
