package com.example.sda_a4_2024_bradleyweibel;

import static com.example.sda_a4_2024_bradleyweibel.Helper.showToast;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */

public class Settings extends Fragment {

    // Create variables for UI elements
    private Button clearBtn, resetBtn, saveBtn;
    private EditText userNameField, userAddressField, userZIPField, userTownField, userPhoneNumberField, userEmailAddressField, userIdField;
    // Other variables
    private SharedPreferences userDetails;

    /**
     * Required empty public constructor
     */
    public Settings() {} // Required empty public constructor

    /**
     * Used to get the page ready, map local variables to UI elements, populating the fields
     * with the saved data in the shared preferences.
     * OnClick for clearing UI fields of text.
     * Onclick for resetting the fields to the data already saved in shared preferences.
     * Onclick for updating the data in shard preferences if the data passes validation which
     * is done in another local method.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment get the root view.
        final View root = inflater.inflate(R.layout.fragment_settings, container, false);

        // Map UI elements to variables
        // Text fields
        userNameField = root.findViewById(R.id.userNameField);
        userAddressField = root.findViewById(R.id.userAddressField);
        userZIPField = root.findViewById(R.id.userZIPField);
        userTownField = root.findViewById(R.id.userTownField);
        userPhoneNumberField = root.findViewById(R.id.userPhoneNumberField);
        userEmailAddressField = root.findViewById(R.id.userEmailAddressField);
        userIdField = root.findViewById(R.id.userIdField);
        // Buttons
        clearBtn = root.findViewById(R.id.clearDetailsBtn);
        resetBtn = root.findViewById(R.id.resetDetailsBtn);
        saveBtn = root.findViewById(R.id.saveDetailsBtn);

        // Get shared preferences
        userDetails = this.getActivity().getSharedPreferences(Helper.UserDetails_SharedPreferences, Context.MODE_PRIVATE);
        // Populate fields from saved data
        populateUserDetailsFieldsWithSavedData();

        // Clear details onclick, remove text from UI fields, but not data in memory
        clearBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Clear data
                userNameField.setText("");
                userAddressField.setText("");
                userZIPField.setText("");
                userTownField.setText("");
                userPhoneNumberField.setText("");
                userEmailAddressField.setText("");
                userIdField.setText("");
                // Set focus to first field for fresh data entry
                userNameField.requestFocus();
            }
        });

        // Reset details onclick, fetch details from SharedPreferences if exist
        resetBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if account details already exist
                String currentSavedUserId = userDetails.getString(Helper.UserDetails_Preference_Id, "");
                // If no details exist, show error message
                if (currentSavedUserId.equals(""))
                    showToast(getString(R.string.missing_account_error), getContext());
                else
                {
                    // Populate fields from saved data
                    populateUserDetailsFieldsWithSavedData();
                    // Show success message
                    showToast(getString(R.string.details_reset), getContext());
                }
                // Set focus to first field for fresh data entry
                userNameField.requestFocus();
            }
        });

        // Save details onclick, check all fields are populated, save data if true
        saveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Check if all details are populated (user is prompted if not via Toast messages)
                if (areAllDetailsPopulated())
                {
                    // Create new Id in case of new username
                    // Get user's name without any spaces
                    String userName = userNameField.getText().toString().replaceAll("\\s+","");
                    // Add number behind
                    String userId = userName + Helper.UserName_Counter;
                    // insert into readonly Id field - to be saved below with the other values
                    userIdField.setText(userId);

                    // Save/overwrite details in SharedPreferences
                    SharedPreferences.Editor editor = userDetails.edit();
                    editor.putString(Helper.UserDetails_Preference_Name, userNameField.getText().toString().trim());
                    editor.putString(Helper.UserDetails_Preference_Address, userAddressField.getText().toString().trim());
                    editor.putString(Helper.UserDetails_Preference_Zip, userZIPField.getText().toString());
                    editor.putString(Helper.UserDetails_Preference_Town, userTownField.getText().toString().trim());
                    editor.putString(Helper.UserDetails_Preference_Phone, userPhoneNumberField.getText().toString());
                    editor.putString(Helper.UserDetails_Preference_Email, userEmailAddressField.getText().toString().trim());
                    editor.putString(Helper.UserDetails_Preference_Id, userIdField.getText().toString());
                    editor.apply();

                    // Show success message
                    showToast(getString(R.string.details_saved), getContext());
                }
            }
        });

        return root;
    }

    // Populate fields from saved data
    /**
     * Used to populate the UI elements with the data from shared preferences.
     */
    private void populateUserDetailsFieldsWithSavedData()
    {
        userNameField.setText(userDetails.getString(Helper.UserDetails_Preference_Name, ""));
        userAddressField.setText(userDetails.getString(Helper.UserDetails_Preference_Address, ""));
        userZIPField.setText(userDetails.getString(Helper.UserDetails_Preference_Zip, ""));
        userTownField.setText(userDetails.getString(Helper.UserDetails_Preference_Town, ""));
        userPhoneNumberField.setText(userDetails.getString(Helper.UserDetails_Preference_Phone, ""));
        userEmailAddressField.setText(userDetails.getString(Helper.UserDetails_Preference_Email, ""));
        userIdField.setText(userDetails.getString(Helper.UserDetails_Preference_Id, ""));
    }

    // Check if all detail fields are properly populated (user is prompted if not via Toast messages)
    /**
     * Check of data in fields pass validation. Deeper validation is outsourced.
     * Error messages are shown if field is not acceptable.
     * @return true if data passes validation.
     */
    private boolean areAllDetailsPopulated()
    {
        // Get string values from UI
        String nameText = userNameField.getText().toString().trim();
        String addressText = userAddressField.getText().toString().trim();
        String zipText = userZIPField.getText().toString().trim();
        String townText = userTownField.getText().toString().trim();
        String phoneText = userPhoneNumberField.getText().toString().trim();
        String emailText = userEmailAddressField.getText().toString().trim().trim();
        // Get context for external showToast() helper method
        Context context = getContext();

        // Determine if detail fields are populated correctly
        if (nameText.equals(""))                                            // Is name empty
            showToast(getString(R.string.name_missing_error), context);
        else if (!isNameOrTownValid(nameText))                              // Is name invalid
            showToast(getString(R.string.name_invalid_error), context);
        else if (addressText.equals(""))                                    // Is street address empty
            showToast(getString(R.string.address_missing_error), context);
        else if (!isAddressValid(addressText))                              // Is street address invalid
            showToast(getString(R.string.address_invalid_error), context);
        else if (zipText.equals(""))                                        // Is zip code empty
            showToast(getString(R.string.zip_missing_error), context);
        else if (zipText.length() <= 3)                                     // Is zip code invalid
            showToast(getString(R.string.zip_invalid_error), context);
        else if (townText.equals(""))                                       // Is town empty
            showToast(getString(R.string.town_missing_error), context);
        else if (!isNameOrTownValid(townText))                              // Is town invalid
            showToast(getString(R.string.town_invalid_error), context);
        else if (phoneText.equals(""))                                      // Is phone number empty
            showToast(getString(R.string.phone_number_missing_error), context);
        else if (phoneText.length() <= 9)                                   // Is phone number invalid
            showToast(getString(R.string.phone_number_invalid_error), context);
        else if (emailText.equals(""))                                      // Is email address empty
            showToast(getString(R.string.email_missing_error), context);
        else if (!isEmailValid(emailText))                                  // Is email address invalid
            showToast(getString(R.string.email_invalid_error), context);
        else                                                                // All fields populated
            return true;
        // At least one field was not populated
        return false;
    }

    // Validation methods

    /**
     * Validation for name and town fields.
     * @param text from UI field
     * @return true is pass validation, false if not.
     */
    private boolean isNameOrTownValid(String text)
    {
        // Create return value
        Boolean result;
        // Check text is not shorter than 3 characters
        if (text.length() < 3)
            result = false;
        else
        {
            // Check text contains only spaces and letters
            Pattern pattern = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)?$");
            Matcher matcher = pattern.matcher(text);
            result = matcher.matches();
        }
        return result;
    }

    /**
     * Validation for address field.
     * @param address from UI field
     * @return true if data passes validation, false if not.
     */
    private boolean isAddressValid(String address)
    {
        // Create return value
        Boolean result;
        // Check address is not shorter than 4 characters
        if (address.length() < 4)
            result = false;
        else
        {
            // Check address contains only spaces, numbers and letters
            Pattern pattern = Pattern.compile("^[A-Za-z0-9 _]*[A-Za-z0-9][A-Za-z0-9 _]*$");
            Matcher matcher = pattern.matcher(address);
            result = matcher.matches();
        }
        return result;
    }

    /**
     * Validation for email field.
     * @param email text from Ui.
     * @return true if passes validation, false if not.
     */
    private boolean isEmailValid(String email)
    {
        Pattern pattern = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
