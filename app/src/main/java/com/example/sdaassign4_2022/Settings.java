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

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    // Create variables for UI elements
    Button saveBtn, clearBtn;
    EditText userNameField, userAddressField, userZIPField, userTownField, userPhoneNumberField, userEmailAddressField, userIdField;

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
        saveBtn = root.findViewById(R.id.saveDetailsBtn);
        clearBtn = root.findViewById(R.id.clearDetailsBtn);

        // Insert previously saved details into fields
        userDetails = this.getActivity().getSharedPreferences("UserDetailsPreferences", Context.MODE_PRIVATE);
        userNameField.setText(userDetails.getString("name", ""));
        userAddressField.setText(userDetails.getString("address", ""));
        userZIPField.setText(userDetails.getString("zip", ""));
        userTownField.setText(userDetails.getString("town", ""));
        userPhoneNumberField.setText(userDetails.getString("phone", ""));
        userEmailAddressField.setText(userDetails.getString("email", ""));
        userIdField.setText(userDetails.getString("id", ""));

        // Save details onclick, check all fields are populated, save data if true
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all details are populated (user is prompted if not via Toast messages)
                if (areAllDetailsPopulated())
                {
                    // If user Id field is empty
                    if (userIdField.getText().toString().equals("")) {
                        // Create new Id
                        // Get user's name without any spaces
                        String userName = userNameField.getText().toString().replaceAll("\\s+","");
                        // Add number behind
                        String userId = userName + "_001";
                        // insert into readonly Id field - to be saved below with the other values
                        userIdField.setText(userId);
                    }

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

        return root;
    }

    // Check if all detail fields are properly populated (user is prompted if not via Toast messages)
    private boolean areAllDetailsPopulated()
    {
        // Determine if detail fields are populated
        if (userNameField.getText().toString().equals(""))                                                              // Is name field empty
            Toast.makeText(getContext(), getString(R.string.name_missing_error), Toast.LENGTH_SHORT).show();
        else if (userAddressField.getText().toString().equals(""))                                                      // Is street address empty
            Toast.makeText(getContext(), getString(R.string.address_missing_error), Toast.LENGTH_SHORT).show();
        else if (userZIPField.getText().toString().equals(""))                                                          // Is zip code empty
            Toast.makeText(getContext(), getString(R.string.zip_missing_error), Toast.LENGTH_SHORT).show();
        else if (userTownField.getText().toString().equals(""))                                                         // Is town empty
            Toast.makeText(getContext(), getString(R.string.town_missing_error), Toast.LENGTH_SHORT).show();
        else if (userPhoneNumberField.getText().toString().equals(""))                                                  // Is phone number empty
            Toast.makeText(getContext(), getString(R.string.phone_number_missing_error), Toast.LENGTH_SHORT).show();
        else if (userEmailAddressField.getText().toString().equals(""))                                                 // Is email address empty
            Toast.makeText(getContext(), getString(R.string.email_missing_error), Toast.LENGTH_SHORT).show();
        else                                                                                                            // All fields populated
            return true;
        // At least one field was not populated
        return false;
    }
}
