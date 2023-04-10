package com.example.just_hungry.new_order;

import static com.example.just_hungry.Utils.TAG;
import static com.example.just_hungry.Utils.getDeviceLocation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.AssetModel;
import com.example.just_hungry.models.LocationModel;
import com.example.just_hungry.models.PostModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class NewOrderFormFragment extends Fragment {
    public ArrayList<PostModel> yourOrders = new ArrayList<>();
    EditText listingTitle;
    EditText groupbuyURL;
    Spinner spinnerCuisine;
    ToggleButton chipHalal;
    EditText maxParticipants;
    String collectionPoint;
    Spinner spinnerLocation;
    Button submitButton;
    Button timePickerButton;
    Date timeLimit;
    Context context;

    Map<String, Object> orderFormData = new HashMap<>();
    // TODO Delete this
    // form data
    private String posterId;
    private String dateCreated;
    private String timing;
    private ArrayList<String> participants;
    private ArrayList<AssetModel> assets;
    private LocationModel location;
    private String storeName;
    //    private Integer maxParticipants;
    SimpleDateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");

    // new form data to be added to the PostModel
    private String cuisine;
    private boolean isHalal;

    // location is default for now
    LocationModel currentLocation = new LocationModel();
    Utils utilsInstance = Utils.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_order_form, container, false);
        context = rootView.getContext();

        // All edit texts
        listingTitle = (EditText) rootView.findViewById(R.id.etListingTitle);
        maxParticipants = (EditText) rootView.findViewById(R.id.editTextNumberPeopleLimit);
        groupbuyURL = (EditText) rootView.findViewById(R.id.editTextGrabURL);

        // chipHalal
        chipHalal = rootView.findViewById(R.id.chipHalalFilter);

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

        // !TODO this is not working
        utilsInstance.getDeviceLocation((Activity) context, locationModel -> {
            currentLocation = locationModel;
        });
//        currentLocation = getDeviceLocation(this.getActivity(), fusedLocationClient, currentLocation);

        // timepicker button
        timePickerButton = (Button) rootView.findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popTimePicker();
            }
        });

        // submit button
        submitButton = (Button) rootView.findViewById(R.id.newOrderSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()){
                    // we should not handle the location server side
                    orderFormData.put("listingTitle", listingTitle.getText().toString());
                    orderFormData.put("groupbuyURL", groupbuyURL.getText().toString());
                    orderFormData.put("isHalal", chipHalal.isChecked());
                    orderFormData.put("timeLimit", timeLimit);
                    orderFormData.put("maxParticipants", maxParticipants.getText().toString());
                    orderFormData.put("collectionPoint", collectionPoint);
                    orderFormData.put("cuisine", spinnerCuisine.getSelectedItem().toString());
                    orderFormData.put("locationSUTD", spinnerLocation.getSelectedItem().toString());
                    System.out.println(orderFormData);
                    pushToFirebase();
                }
            }});

        // Inflate the layout for this fragment
        return rootView;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//        if(groupbuyURLString.length() < 18){
//            Toast.makeText(context, "Grabfood URL: https://r.grab.com/...", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(!groupbuyURLString.substring(0,18).equals("https://r.grab.com")){
//            Toast.makeText(context, "Grabfood URL: https://r.grab.com/...", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if(timeLimit == null){
            Toast.makeText(context, "Please set a time limit", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(maxParticipants.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a maximum number of participants", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!maxParticipants.getText().toString().matches("[0-9]+")){
            Toast.makeText(context, "Please enter a valid maximum number of participants", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(collectionPoint == null){
            Toast.makeText(context, "Please select a collection point", Toast.LENGTH_SHORT).show();
            return false;
        }

        // can be refactored to a function
        String cuisine = spinnerCuisine.getSelectedItem().toString();
        if(cuisine.equals("No Category")){
            Toast.makeText(context, "Please select a cuisine", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (collectionPoint.equals("Choose SUTD")) {
            String locationSUTD = spinnerLocation.getSelectedItem().toString();
            if(locationSUTD.equals("Please select a location")){
                Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show();
                return false;
            }
            currentLocation = getSUTDLocation(locationSUTD);
        }
        return true;
    }


    public void pushToFirebase(){
        // Generate new order and push to firebase
        SharedPreferences preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String userId = preferences.getString("userId", "");
        String postId = UUID.randomUUID().toString();

        /**
         *  String postId,
         *     String posterId,
         *     String dateCreated,
         *     String timing,
         *     ArrayList<String> participants,
         *     ArrayList<AssetModel> assets,
         *     LocationModel location,
         *     String storeName,
         *     Integer maxParticipants,
         *     String cuisine,
         *     String grabFoodUrl
         */
        // initialise new participants arraylist
        // TODO: isOwner attribute
        ArrayList<String> participants = new ArrayList<>();
        HashMap<String, Object> ownerHM = new HashMap<>();
        ownerHM.put("dateJoined", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'").toString());
        ownerHM.put("userId", userId);

        participants.add(userId);

        // initialise new assets arraylist
        ArrayList<AssetModel> assets = new ArrayList<AssetModel>();
        assets.add(new AssetModel());

        HashMap<String, Object> newRandomOrderHM = new PostModel(
                postId,
                userId,
                // get the current time
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()),
                // time limit is the time limit set by the user in String Simple Date time format
                (Date) orderFormData.get("timeLimit"),
                // participants is just the current user
                participants,
                getAssetModelfromCuisine(orderFormData.get("cuisine").toString()),
                currentLocation,
                orderFormData.get("listingTitle").toString(),
                Integer.parseInt(orderFormData.get("maxParticipants").toString()),
                orderFormData.get("cuisine").toString(),
                orderFormData.get("groupbuyURL").toString(),

                // TODO add the rest of the fields in the firebase
                (Boolean) orderFormData.get("isHalal")
        ).getHashMapForFirestore();

        System.out.println(newRandomOrderHM);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println(userId);
        db.collection("posts").document(postId).set(newRandomOrderHM);

        // redirect to the new order page
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewOrderFragment()).commit();
    }

    // Hardcoded for now, because we dont intend to change the locations in SUTD often
    public LocationModel getSUTDLocation(String locationSUTD){
        LocationModel location = new LocationModel();
        if (locationSUTD.equals("SUTD Hostel Blk 55 Level 2")){
            location.setLatitude(1.3422013952064185);
            location.setLongitude(103.96446692148714);
        }
        else if (locationSUTD.equals("SUTD Hostel Blk 57 Pick up point")){
            location.setLatitude(1.3419031583156191);
            location.setLongitude(103.96438658928977);
        }
        else if (locationSUTD.equals("SUTD Hostel Blk 59 Level 2")){
            location.setLatitude(1.3417702020042102);
            location.setLongitude(103.96407607350737);
        }
        else if (locationSUTD.equals("SUTD Campus Centre")){
            location.setLatitude(1.3406340270699488);
            location.setLongitude(103.96317166232835);
        }
        else if (locationSUTD.equals("SUTD Sports and Recreation Centre Level 1")){
            location.setLatitude(1.3416589687547023);
            location.setLongitude(103.96483830118937);
        }
        else {
            Log.e(TAG, "getSUTDLocation: Invalid spinnerLocation value", null);
        }
        return location;
    }

    public ArrayList<AssetModel> getAssetModelfromCuisine(String cuisine){
        ArrayList<AssetModel> assetList = new ArrayList<>();
        AssetModel cuisineAsset = new AssetModel();

        if (cuisine.equals("Chinese")) {//
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1623689046286-01d812cc8bad?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=875&q=80");
        } else if (cuisine.equals("Western")) {//
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1603073163308-9654c3fb70b5?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=427&q=80");
        } else if (cuisine.equals("Japanese")) { //
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1580822184713-fc5400e7fe10?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8amFwYW5lc2UlMjBmb29kfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Indian")) { //
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1505253758473-96b7015fcd40?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8NHx8aW5kaWFuJTIwZm9vZHxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Malay")) { //
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1590809632480-2bd15d90c92f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8bWFsYXklMjBmb29kfGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Thai")) {
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1627308595186-e6bb36712645?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Nnx8dGhhaSUyMGZvb2R8ZW58MHx8MHx8&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Italian")) {
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1546549032-9571cd6b27df?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8aXRhbGlhbiUyMGZvb2R8ZW58MHx8MHx8&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Vegetarian")) {
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1599020792689-9fde458e7e17?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmVnZXRhcmlhbiUyMGZvb2R8ZW58MHx8MHx8&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Korean")) {
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1635363638580-c2809d049eee?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8a29yZWFuJTIwZm9vZHxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Vietnamese")) {
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dmlldG5hbWVzZSUyMGZvb2R8ZW58MHx8MHx8&auto=format&fit=crop&w=500&q=60");
        } else if (cuisine.equals("Others")) {
            cuisineAsset.setAssetUrl("https://images.unsplash.com/photo-1555939594-58d7cb561ad1?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8Zm9vZHxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60");
        } else {
            Log.e(TAG, "getAssetModelfromCuisine: Invalid cuisine value", null);
        }
        assetList.add(cuisineAsset);
        Log.d(TAG, "getAssetModelfromCuisine: " + assetList.get(0).getAssetUrl() + "");
        return assetList;
    }

    private void popTimePicker(){
        TimePickerFragment timePickerFragment = new TimePickerFragment(this);
        timePickerFragment.show(getChildFragmentManager(), "time picker");
    }

    public void onTimeSet(int hourOfDay, int minute) {
        // Convert the selected hour and minute into a SimpleDateFormat string
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // If the selected time is before the current time, set the timeLimit to the next day
        if (hourOfDay < currentHour || (hourOfDay == currentHour && minute <= currentMinute)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        timeLimit = calendar.getTime();

        SimpleDateFormat simpleDateFormatDisplayed = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeLimitDisplayed = simpleDateFormatDisplayed.format(calendar.getTime());

        // Do something with the time chosen by the user, e.g. update a TextView.
        timePickerButton.setText("Cut off time for joining: " + timeLimitDisplayed);

//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        calendar.set(Calendar.MINUTE, minute);
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        timeLimit = simpleDateFormat.format(calendar.getTime());
//        Log.d(TAG, "onTimeSet: " + timeLimit);
//        timePickerButton.setText("Cut off time for joining: " + timeLimit);
    }
}

