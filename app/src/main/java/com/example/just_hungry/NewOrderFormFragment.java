package com.example.just_hungry;

import static com.example.just_hungry.Utils.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.just_hungry.models.AssetModel;
import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.ParticipantModel;
import com.example.just_hungry.models.PostModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewOrderFormFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public ArrayList<PostModel> yourOrders = new ArrayList<>();
    EditText listingTitle;
    EditText groupbuyURL;
    Spinner spinnerCuisine;
    ToggleButton chipHalal;
    EditText timeLimit;
    EditText maxParticipants;
    String collectionPoint;
    Spinner spinnerLocation;
    Button submitButton;

    Context context;

    Map<String, Object> orderFormData = new HashMap<>();

    // form data
    private String posterId;
    private String dateCreated;
    private String timing;
    private ArrayList<ParticipantModel> participants;
    private ArrayList<AssetModel> assets;
    private LocationModel location;
    private String storeName;
    //    private Integer maxParticipants;
    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

    // new form data to be added to the PostModel
    private String cuisine;
    private boolean isHalal;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_order_form, container, false);
        context = rootView.getContext();

        // All edit texts
        listingTitle = (EditText) rootView.findViewById(R.id.etListingTitle);
        timeLimit = (EditText) rootView.findViewById(R.id.editTextTime);
        maxParticipants = (EditText) rootView.findViewById(R.id.editTextNumberPeopleLimit);
        groupbuyURL = (EditText) rootView.findViewById(R.id.editTextGrabURL);

        // chipHalal
        chipHalal = rootView.findViewById(R.id.chipHalal);

        // collectionPoint radio buttons
        RadioButton useLocation = (RadioButton) rootView.findViewById(R.id.radioButtonUseLocation);
        RadioButton chooseSUTD = (RadioButton) rootView.findViewById(R.id.radioButtonChooseSUTD);
        useLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionPoint = "Use my location";
            }
        });

        chooseSUTD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionPoint = "Choose SUTD";
            }
        });


        // Location spinner code
        spinnerLocation = (Spinner) rootView.findViewById(R.id.spinnerLocation);
        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.spinner_location, android.R.layout.simple_spinner_item);
        spinnerLocation.setAdapter(adapterLocation);

        // Cuisine spinner code - is there a function to add to all spinners?
        spinnerCuisine = (Spinner) rootView.findViewById(R.id.spinnerCuisine);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.spinner_cuisine, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisine.setAdapter(adapter);


        // submit button
        submitButton = (Button) rootView.findViewById(R.id.newOrderSubmitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()){
                    orderFormData.put("listingTitle", listingTitle.getText().toString());
                    orderFormData.put("groupbuyURL", groupbuyURL.getText().toString());
                    orderFormData.put("isHalal", chipHalal.isChecked());
                    orderFormData.put("timeLimit", timeLimit.getText().toString());
                    orderFormData.put("maxParticipants", maxParticipants.getText().toString());
                    orderFormData.put("collectionPoint", collectionPoint);
                    orderFormData.put("cuisine", spinnerCuisine.getSelectedItem().toString());
                    orderFormData.put("location", spinnerLocation.getSelectedItem().toString());
                    System.out.println(orderFormData);
                }
            }});

        // Inflate the layout for this fragment
        return rootView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // cuisine spinner selecting
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        cuisine= (String) adapterView.getItemAtPosition(i);
        Log.i(TAG, "onItemSelected: " + cuisine);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
    public boolean validateFields(){
        if(listingTitle.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a restaurant name", Toast.LENGTH_SHORT).show();
            return false;
        }
        String groupbuyURLString = groupbuyURL.getText().toString();
        if(groupbuyURLString.isEmpty()){
            Toast.makeText(context, "Please enter a groupbuy URL", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!groupbuyURLString.substring(groupbuyURLString.length()-4).equals(".com")){
            Toast.makeText(context, "Please enter a valid groupbuy URL", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(timeLimit.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a time limit", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(maxParticipants.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a maximum number of participants", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(collectionPoint == null){
            Toast.makeText(context, "Please select a collection point", Toast.LENGTH_SHORT).show();
            return false;
        }

        // can be refactored to a function
        String cuisine = spinnerCuisine.getSelectedItem().toString();
        if(cuisine.equals("Please select a cuisine")){
            Toast.makeText(context, "Please select a cuisine", Toast.LENGTH_SHORT).show();
            return false;
        }
        String location = spinnerCuisine.getSelectedItem().toString();
        if(location.equals("Please select a location")){
            Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}