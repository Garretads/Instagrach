package ru.myandroid.garred.instagrach;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GalleryFeedActivity extends AppCompatActivity {
private StorageReference mStorageRef;
private FirebaseStorage mStorage;
private DatabaseReference mDatabase;
private RecyclerView recyclerView;
private RecyclerView.Adapter recycleAdapter;
private ProgressDialog progressDialog;
private List<Upload> uploadsList;

public static final String STORAGE_PATH_UPLOADS = "uploads/";
public static final String DATABASE_PATH_UPLOADS = "uploads";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_feed);

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_UPLOADS);
        uploadsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recycleAdapter = new FeedListAdapter(getApplicationContext(),uploadsList);
        recyclerView.setAdapter(recycleAdapter);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploadsList.add(upload);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Please wait...");
        progressDialog.show();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
        //getPhotoFeed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {

        }

        return super.onOptionsItemSelected(item);
    }

    public void addPicture(View view) {
        getPhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            uploadFile(selectedImage);
            recycleAdapter.notifyDataSetChanged();
            //imageView.setImageBitmap(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent.createChooser(intent, "Select Picture"),1);
    }

   /* protected void getPhotoFeed() {
        ParseQuery<ParseObject> feedQuery = ParseQuery.getQuery("Image").whereEqualTo("username",ParseUser.getCurrentUser().getObjectId().toString());
        List<ParseObject> objects = null;
        ParseFile imageFile;
        Bitmap image;
        String creationDate;
        feedItemsList = new ArrayList<>();
        try {
            for (ParseObject object: objects) {
                imageFile = (ParseFile)object.get("image");
                image = BitmapFactory.decodeByteArray(imageFile.getData(),0,imageFile.getData().length);
                Date date = object.getCreatedAt();
                DateFormat df = new SimpleDateFormat("HH:mm,dd-MM");
                creationDate = df.format(date);
                feedItemsList.add(new feedItem(creationDate,image,MainActivity.mAuth.getCurrentUser().getEmail()));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    } */

    private void uploadFile(Uri imageUri) {
        //checking if file is available
        if (imageUri != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            final StorageReference sRef = mStorageRef.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(imageUri));

            //adding the file to reference

            sRef.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //displaying the upload progress
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return sRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        progressDialog.dismiss();

                        //displaying success toast
                        Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                        Uri downloadedUri = task.getResult();
                        Date date = Calendar.getInstance().getTime();
                        DateFormat df = new SimpleDateFormat("HH:mm,dd-MM");
                        String creationDate = df.format(date);
                        //creating the upload object to store uploaded image details
                        Upload upload = new Upload("Test description", MainActivity.mAuth.getCurrentUser().getEmail(),creationDate, downloadedUri.toString());

                        //adding an upload to firebase database
                        String uploadId = mDatabase.push().getKey();
                        mDatabase.child(uploadId).setValue(upload);
                        recycleAdapter.notifyDataSetChanged();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            //display an error if no file is selected
        }
    }


    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
