package com.example.sdaassign4_2022;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    // Create variables for UI elements
    Button clearBtn, resetBtn, saveBtn;
    EditText userNameField, userAddressField, userZIPField, userTownField, userPhoneNumberField, userEmailAddressField, userIdField;
    // Other variables
    SharedPreferences userDetails;

    public Settings() {} // Required empty public constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        userDetails = this.getActivity().getSharedPreferences("UserDetailsPreferences", Context.MODE_PRIVATE);
        // Populate fields from saved data
        populateUserDetailsFieldsWithSavedData();

        // Clear details onclick, remove text from UI fields, but not data in memory
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if account details already exist
                String currentSavedUserId = userDetails.getString("id", "");
                // If no details exist, show error message
                if (currentSavedUserId.equals(""))
                    Toast.makeText(getContext(), getString(R.string.missing_account_error), Toast.LENGTH_SHORT).show();
                else
                {
                    // Populate fields from saved data
                    populateUserDetailsFieldsWithSavedData();
                    // Show success message
                    Toast.makeText(getContext(), getString(R.string.details_reset), Toast.LENGTH_SHORT).show();
                }
                // Set focus to first field for fresh data entry
                userNameField.requestFocus();
            }
        });

        // Save details onclick, check all fields are populated, save data if true
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all details are populated (user is prompted if not via Toast messages)
                if (areAllDetailsPopulated())
                {
                    // Create new Id in case of new username
                    // Get user's name without any spaces
                    String userName = userNameField.getText().toString().replaceAll("\\s+","");
                    // Add number behind
                    String userId = userName + "_001";
                    // insert into readonly Id field - to be saved below with the other values
                    userIdField.setText(userId);

                    // Save/overwrite details in SharedPreferences
                    SharedPreferences.Editor editor = userDetails.edit();
                    editor.putString("name", userNameField.getText().toString().trim());
                    editor.putString("address", userAddressField.getText().toString().trim());
                    editor.putString("zip", userZIPField.getText().toString());
                    editor.putString("town", userTownField.getText().toString().trim());
                    editor.putString("phone", userPhoneNumberField.getText().toString());
                    editor.putString("email", userEmailAddressField.getText().toString().trim());
                    editor.putString("id", userIdField.getText().toString());
                    editor.apply();

                    // Show success message
                    Toast.makeText(getContext(), getString(R.string.details_saved), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    // Populate fields from saved data
    private void populateUserDetailsFieldsWithSavedData()
    {
        userNameField.setText(userDetails.getString("name", ""));
        userAddressField.setText(userDetails.getString("address", ""));
        userZIPField.setText(userDetails.getString("zip", ""));
        userTownField.setText(userDetails.getString("town", ""));
        userPhoneNumberField.setText(userDetails.getString("phone", ""));
        userEmailAddressField.setText(userDetails.getString("email", ""));
        userIdField.setText(userDetails.getString("id", ""));
    }

    // Check if all detail fields are properly populated (user is prompted if not via Toast messages)
    private boolean areAllDetailsPopulated()
    {
        // Get string values from UI
        String nameText = userNameField.getText().toString().trim();
        String addressText = userAddressField.getText().toString().trim();
        String zipText = userZIPField.getText().toString().trim();
        String townText = userTownField.getText().toString().trim();
        String phoneText = userPhoneNumberField.getText().toString().trim();
        String emailText = userEmailAddressField.getText().toString().trim().trim();
        // Determine if detail fields are populated correctly
        if (nameText.equals(""))                                                                                        // Is name empty
            Toast.makeText(getContext(), getString(R.string.name_missing_error), Toast.LENGTH_SHORT).show();
        else if (!isNameOrTownValid(nameText))                                                                          // Is name invalid
            Toast.makeText(getContext(), getString(R.string.name_invalid_error), Toast.LENGTH_SHORT).show();
        else if (addressText.equals(""))                                                                                // Is street address empty
            Toast.makeText(getContext(), getString(R.string.address_missing_error), Toast.LENGTH_SHORT).show();
        else if (!isAddressValid(addressText))                                                                          // Is street address invalid
            Toast.makeText(getContext(), getString(R.string.address_invalid_error), Toast.LENGTH_SHORT).show();
        else if (zipText.equals(""))                                                                                    // Is zip code empty
            Toast.makeText(getContext(), getString(R.string.zip_missing_error), Toast.LENGTH_SHORT).show();
        else if (zipText.length() <= 3)                                                                                 // Is zip code invalid
            Toast.makeText(getContext(), getString(R.string.zip_invalid_error), Toast.LENGTH_SHORT).show();
        else if (townText.equals(""))                                                                                   // Is town empty
            Toast.makeText(getContext(), getString(R.string.town_missing_error), Toast.LENGTH_SHORT).show();
        else if (!isNameOrTownValid(townText))                                                                          // Is town invalid
            Toast.makeText(getContext(), getString(R.string.town_invalid_error), Toast.LENGTH_SHORT).show();
        else if (phoneText.equals(""))                                                                                  // Is phone number empty
            Toast.makeText(getContext(), getString(R.string.phone_number_missing_error), Toast.LENGTH_SHORT).show();
        else if (phoneText.length() <= 9)                                                                               // Is phone number invalid
            Toast.makeText(getContext(), getString(R.string.phone_number_invalid_error), Toast.LENGTH_SHORT).show();
        else if (emailText.equals(""))                                                                                  // Is email address empty
            Toast.makeText(getContext(), getString(R.string.email_missing_error), Toast.LENGTH_SHORT).show();
        else if (!isEmailValid(emailText))                                                                              // Is email address invalid
            Toast.makeText(getContext(), getString(R.string.email_invalid_error), Toast.LENGTH_SHORT).show();
        else                                                                                                            // All fields populated
            return true;
        // At least one field was not populated
        return false;
    }

    // Validation methods
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
    private boolean isEmailValid(String email)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
