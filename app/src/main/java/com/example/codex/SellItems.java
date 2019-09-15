package com.example.codex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Random;

public class SellItems extends AppCompatActivity {
    String LOG = "SellItems";
    private Button upload;
    private ImageView imgView;
    private final int PICK_IMAGE_REQUEST = 111;
    private Uri filePath;
    private boolean imageflag = false;
    private String downloadUri="xxx";
    private ProgressBar pb;
    private EditText verifyemail;
    private EditText priceInput;
    private String loginemail;

    private EditText nameInput;
    private EditText descinput;
    private EditText phinput;
    private EditText emailinput;
    private String title;
    private String category;
    private String desc;
    private String price;
    private String phone;

    private Spinner spinner;

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_items);

        nameInput = (EditText)findViewById(R.id.title);
        descinput = (EditText)findViewById(R.id.desc);
        phinput = (EditText)findViewById(R.id.phone);
        pb = (ProgressBar)findViewById(R.id.progress);
        priceInput = findViewById(R.id.price);
        upload = (Button) findViewById(R.id.sell);
        imgView = (ImageView) findViewById(R.id.image);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                    generateKeyAndUpload();
                else
                    Toast.makeText(SellItems.this, "No network", Toast.LENGTH_SHORT).show();
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });

        /*
        eventdateinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opencalender();
            }
        });
         */
    }

    /*
    private void opencalender() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        eventdateinput.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
                imageflag = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(SellItems.this,BuyItems.class));
    }

    private void generateKeyAndUpload()
    {

        final SharedPreferences sharedPreferences = this.getSharedPreferences("RootUser",MODE_PRIVATE);
        pb.setVisibility(View.VISIBLE);
        title = nameInput.getText().toString();
        desc = descinput.getText().toString();
        phone = phinput.getText().toString();
        price = priceInput.getText().toString();

        if(price.equalsIgnoreCase(null))
            price = "0";

        if(title.length()>5){
                if(desc.length()>19){
                    if(phone.length() == 10){
                        String eventidcreate = title.substring(0,6);
                        // final String date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(new Date());
                        //eventidcreate=eventidcreate.concat(date);

                        Random random =new Random();
                        int x = random.nextInt(900) + 100;

                        eventidcreate = eventidcreate.concat(x+"");
                        final String key = eventidcreate;
                        if(imageflag)
                        {
                            if (filePath != null) {
                                StorageReference childRef = storageRef.child(key+".jpg");

                                //uploading the image
                                UploadTask uploadTask = childRef.putFile(filePath);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //Toast.makeText(AddEvent.this, "Upload of image successfull successful", Toast.LENGTH_SHORT).show();
                                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                        downloadUri = downloadUrl.toString();
                                        HashMap<String,String> eventchild = new HashMap<>();
                                        //Toast.makeText(AddEvent.this, dUri.toString(), Toast.LENGTH_SHORT).show();
                                        eventchild.put("description",desc);
                                        eventchild.put("itemId",key);
                                        eventchild.put("name",title);
                                        eventchild.put("price", price);
                                        eventchild.put("image",downloadUri);
                                        eventchild.put("phone",phone);
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items");
                                        databaseReference.child(key).setValue(eventchild);
                                        Toast.makeText(SellItems.this, "Success!!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.v(LOG, "Failed!!!!!");
                                        Toast.makeText(SellItems.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(SellItems.this, "Select an image", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            HashMap<String,String> eventchild = new HashMap<>();
                            eventchild.put("description",desc);
                            eventchild.put("itemId",key);
                            eventchild.put("name",title);
                            eventchild.put("image",downloadUri);
                            eventchild.put("price", price);
                            eventchild.put("phone",phone);
                            eventchild.put("poster",sharedPreferences.getString("name","undef"));
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("items");
                            databaseReference.child(key).setValue(eventchild);
                        }
                        pb.setVisibility(View.INVISIBLE);
                        FirebaseAuth.getInstance().signOut();
                        Intent i = new Intent(SellItems.this, BuyItems.class);
                        Toast.makeText(SellItems.this, "Item successfully added", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    }
                    else
                        Toast.makeText(SellItems.this, "Error in Phone number", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(SellItems.this, "Description too short", Toast.LENGTH_SHORT).show();
            }
        else
            Toast.makeText(SellItems.this, "Name too short", Toast.LENGTH_SHORT).show();
        pb.setVisibility(View.INVISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

/*
    public void opencalender() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AddEvent.this);
        LayoutInflater inflater = (LayoutInflater)AddEvent.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View theView = inflater.inflate(R.layout.calender_view, null);

        final String return_date = "";
        final NumberPicker npy = (NumberPicker) theView.findViewById(R.id.npy);
        final NumberPicker npm = (NumberPicker) theView.findViewById(R.id.npm);
        final NumberPicker npd = (NumberPicker) theView.findViewById(R.id.npd);

        npy.setMinValue(Calendar.getInstance().get(Calendar.YEAR));
        npy.setMaxValue(Calendar.getInstance().get(Calendar.YEAR)+1);
        npy.setValue(Calendar.getInstance().get(Calendar.YEAR));

        npd.setMinValue(1);
        npd.setMaxValue(31);
        npd.setValue(15);

        npm.setMinValue(1);
        npm.setMaxValue(12);
        npm.setValue(5);
        builder.setCancelable(false);
        builder.setTitle("Choose Event date");
        builder.setView(theView)
                .setPositiveButton("Set",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = npy.getValue();
                        int month = npm.getValue();
                        int day = npd.getValue();
                        if(month<10&&day<10)
                            eventdate = "0"+day+"-0"+month+"-"+year;
                        else if(day<10)
                            eventdate = "0"+day+"-"+month+"-"+year;
                        else if(month<10)
                            eventdate = day+"-0"+month+"-"+year;
                        else
                            eventdate = day+"-"+month+"-"+year;

                        eventdateinput.setText(eventdate);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventdate = "";
                    }
        });
        builder.show();

    }
*/