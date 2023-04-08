package com.example.just_hungry.account;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.just_hungry.R;
import com.example.just_hungry.Utils;
import com.example.just_hungry.models.AssetModel;
import com.example.just_hungry.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AccountManagementFragment extends Fragment {

    // Add the necessary instance variables
    private ImageView profileImage;
    private TextView currentName;
    private EditText nameInput;
    private ImageButton editNameButton;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private String currentUserId;
    private static final int PICK_IMAGE_REQUEST = 1;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<String> mGetContent;

    private ImageButton saveNameButton;
    private ImageButton editEmailButton;
    private ImageButton editPasswordButton;
    private ImageButton saveEmailButton;
    private ImageButton savePasswordButton;
    private TextView currentEmail;
    private TextView currentPassword;
    private EditText emailInput;
    private EditText passwordInput;

    Context context = getContext();

    UserModel currentUserObject = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_management, container, false);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        // Initialize UI elements
        profileImage = view.findViewById(R.id.profile_image);
        currentName = view.findViewById(R.id.current_name);
        nameInput = view.findViewById(R.id.name_input);
        editNameButton = view.findViewById(R.id.edit_name_button);
        saveNameButton = view.findViewById(R.id.save_name_button);

        currentPassword = view.findViewById(R.id.current_password);
        currentEmail = view.findViewById(R.id.current_email);
        passwordInput = view.findViewById(R.id.password_input);
        emailInput = view.findViewById(R.id.email_input);
        editEmailButton = view.findViewById(R.id.edit_email_button);
        editPasswordButton = view.findViewById(R.id.edit_password_button);
        saveEmailButton = view.findViewById(R.id.save_email_button);
        savePasswordButton = view.findViewById(R.id.save_password_button);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String userId = sharedPreferences.getString("userId", "");
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        // Set the current fields and set to the input to gone
        currentName.setText(name);
        currentEmail.setText(email);
        currentPassword.setText("**********");
        nameInput.setVisibility(View.GONE);
        saveNameButton.setVisibility(View.GONE);
        emailInput.setVisibility(View.GONE);
        passwordInput.setVisibility(View.GONE);
        saveEmailButton.setVisibility(View.GONE);
        savePasswordButton.setVisibility(View.GONE);

        // Set the onClickListener for editNameButton
        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameInput.setVisibility(View.VISIBLE);
                nameInput.setEnabled(true);
                currentName.setVisibility(View.GONE);
                saveNameButton.setVisibility(View.VISIBLE);
                editNameButton.setVisibility(View.GONE);
//                editEmailButton.setEnabled(false);
//                editPasswordButton.setEnabled(false);
            }
        });

        // Add other input fields (email and password) and the submit button here
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the new name to Firebase and update SharedPreferences
                String newName = nameInput.getText().toString().trim();
                if (!newName.isEmpty()) {
                    // Update Firestore and SharedPreferences
                    db.collection("users")
                            .document(userId)
                            .update("name", newName)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("name", newName);
                                    editor.apply();

                                    // Switch back to the display mode
                                    currentName.setText(newName);
                                    currentName.setVisibility(View.VISIBLE);
                                    nameInput.setVisibility(View.GONE);
                                    saveNameButton.setVisibility(View.GONE);
                                    editNameButton.setVisibility(View.VISIBLE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to update name", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // Switch back to the display mode
                    currentName.setText(newName);
                    currentName.setVisibility(View.VISIBLE);
                    nameInput.setVisibility(View.GONE);
                    saveNameButton.setVisibility(View.GONE);
                    editNameButton.setVisibility(View.VISIBLE);
//                    editEmailButton.setEnabled(true);
//                    editPasswordButton.setEnabled(true);
                    Toast.makeText(getContext(), "Name successfully updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Utils.getUserById(userId, poster -> {
            if (poster == null) {
                return;
            }
            currentUserObject = poster;
            currentUserId = currentUserObject.getUserId();
            String userProfileUrl = currentUserObject.getProfilePictureUrl().getAssetUrl();
            System.out.println("userProfileUrl: " + userProfileUrl );
            if (currentUserObject != null && !userProfileUrl.equalsIgnoreCase("")) {
                if (Utils.isNetworkAvailable(view.getContext())) {
                    Glide.with(view.getContext())
                            .load(userProfileUrl)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                            .into(profileImage);
                }
            }
        });

        // Add other input fields (email and password) and the submit button here
        // Edit email button
        editEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentEmail.setVisibility(View.GONE);
                emailInput.setVisibility(View.VISIBLE);
                saveEmailButton.setVisibility(View.VISIBLE);
                editEmailButton.setVisibility(View.GONE);
            }
        });

// Save email button
        saveEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the new email to Firebase and update SharedPreferences
                String newEmail = emailInput.getText().toString().trim();
                if (!newEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    // Update Firestore
                    db.collection("users")
                            .document(userId)
                            .update("email", newEmail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("email", newEmail);
                                    editor.apply();

                                    //Update firestore
                                    db.collection("users")
                                            .document(userId)
                                            .update("email", newEmail)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Update SharedPreferences
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("email", newEmail);
                                                    editor.apply();

                                                    // Switch back to the display mode
                                                    currentEmail.setText(newEmail);
                                                    currentEmail.setVisibility(View.VISIBLE);
                                                    emailInput.setVisibility(View.GONE);
                                                    saveEmailButton.setVisibility(View.GONE);
                                                    editEmailButton.setVisibility(View.VISIBLE);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Failed to update email", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    // Switch back to the display mode
                                    currentEmail.setText(newEmail);
                                    currentEmail.setVisibility(View.VISIBLE);
                                    emailInput.setVisibility(View.GONE);
                                    saveEmailButton.setVisibility(View.GONE);
                                    editEmailButton.setVisibility(View.VISIBLE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed to update email", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                }
            }
        });

// Edit password button
        editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPassword.setVisibility(View.GONE);
                passwordInput.setVisibility(View.VISIBLE);
                savePasswordButton.setVisibility(View.VISIBLE);
                editPasswordButton.setVisibility(View.GONE);
            }
        });

// Save password button
        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the new password to Firebase and update SharedPreferences
                String newPassword = passwordInput.getText().toString().trim();
                if (!newPassword.isEmpty() && newPassword.length() >= 6) {
                    // Update Firestore
                    db.collection("users")
                            .document(userId)
                            .update("password", newPassword)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("password", newPassword);
                                    editor.apply();

                                    //Update firestore
                                    db.collection("users")
                                            .document(userId)
                                            .update("password", newPassword)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Update SharedPreferences
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("password", newPassword);
                                                    editor.apply();

                                                    // Switch back to the display mode
                                                    currentPassword.setText("********");
                                                    currentPassword.setVisibility(View.VISIBLE);
                                                    passwordInput.setVisibility(View.GONE);
                                                    savePasswordButton.setVisibility(View.GONE);
                                                    editPasswordButton.setVisibility(View.VISIBLE);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    // Switch back to the display mode
                                    currentPassword.setText("********");
                                    currentPassword.setVisibility(View.VISIBLE);
                                    passwordInput.setVisibility(View.GONE);
                                    savePasswordButton.setVisibility(View.GONE);
                                    editPasswordButton.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                        Toast.makeText(getContext(), "Invalid password. Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    }
                }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri imageUri) {
                        if (imageUri != null) {
                            uploadImage(imageUri);
                        }
                    }
                });
    }

    private void openFileChooser() {
        mGetContent.launch("image/*");
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            Toast.makeText(requireActivity(), "Uploading...", Toast.LENGTH_LONG).show();
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            AssetModel asset = new AssetModel("Profile Picture", imageUrl);
                                            System.out.println("asset: " + asset.getHashMapForFirestore());
                                            db.collection("users")
                                                    .document(currentUserId)
                                                    .update("profilePictureUrl", asset.getHashMapForFirestore())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Glide.with(requireActivity())
                                                                    .load(imageUrl)
                                                                    .into(profileImage);
                                                            Toast.makeText(requireActivity(), "Profile picture successfully changed!", Toast.LENGTH_SHORT).show();
                                                        }

                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
