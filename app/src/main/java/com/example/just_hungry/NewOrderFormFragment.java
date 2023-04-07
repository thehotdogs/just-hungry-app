package com.example.just_hungry;

import static com.example.just_hungry.Utils.TAG;
import static com.example.just_hungry.Utils.getDeviceLocation;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewOrderFormFragment extends Fragment {
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

    // location is default for now
    LocationModel currentLocation = new LocationModel();

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

        // get current location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        currentLocation = getDeviceLocation(this.getActivity(), fusedLocationClient, currentLocation);


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
                    orderFormData.put("timeLimit", timeLimit.getText().toString());
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
//            Toast.makeText(context, "Please enter a valid groupbuy URL - it should be in the form: https://r.grab.com/...", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(!groupbuyURLString.substring(0,18).equals("https://r.grab.com")){
//            Toast.makeText(context, "Please enter a valid groupbuy URL - it should be in the form: https://r.grab.com/...", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if(timeLimit.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a time limit", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!timeLimit.getText().toString().matches("[0-9]+")){
            Toast.makeText(context, "Please enter a valid time limit", Toast.LENGTH_SHORT).show();
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
        if(cuisine.equals("Please select a cuisine")){
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
         *     ArrayList<ParticipantModel> participants,
         *     ArrayList<AssetModel> assets,
         *     LocationModel location,
         *     String storeName,
         *     Integer maxParticipants,
         *     String cuisine,
         *     String grabFoodUrl
         */
        // initialise new participants arraylist
        ArrayList<ParticipantModel> participants = new ArrayList<>();
        participants.add(new ParticipantModel(UUID.randomUUID().toString(), userId, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'").toString()));

        // initialise new assets arraylist
        ArrayList<AssetModel> assets = new ArrayList<AssetModel>();
        assets.add(new AssetModel());

        HashMap<String, Object> newRandomOrderHM = new PostModel(
                postId,
                userId,
                // get the current time
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()),
                orderFormData.get("timeLimit").toString(),
                // participants is just the current user
                participants,
                assets,
                currentLocation,
                orderFormData.get("listingTitle").toString(),
                Integer.parseInt(orderFormData.get("maxParticipants").toString()),
                orderFormData.get("cuisine").toString(),
                orderFormData.get("groupbuyURL").toString()

                // TODO add the rest of the fields in the firebase
//                orderFormData.get("isHalal"),
        ).getHashMapForFirestore();

        System.out.println(newRandomOrderHM);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println(userId);
        db.collection("posts").document(postId).set(newRandomOrderHM);

        // redirect to the new order page
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewOrderFragment()).commit();
    }

    // TODO Hardcoded for now, because we dont intend to change the locations in SUTD often
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

}

