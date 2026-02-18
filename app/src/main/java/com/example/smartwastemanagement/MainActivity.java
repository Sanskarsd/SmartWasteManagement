package com.example.smartwastemanagement;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    GridView gridView;
    ImageButton imageButton;
    TextView titleName;
    String[] name={"View Map","Marketplace","History","Event Request","Waste alert","About points"};
    int[] images={R.drawable.m_1,R.drawable.m_2,R.drawable.m_3,R.drawable.m_4,R.drawable.m_5,R.drawable.m_1,};
    SharedPreferences sharedPreferences,sharedPreferencesUserProfile;
    DatabaseReference userInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        titleName=findViewById(R.id.TitleName);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String userID = sharedPreferences.getString("userID","userID");
        String user_employeeId = sharedPreferences.getString("user_employeeId","user_employeeId");
        String user_gender = sharedPreferences.getString("user_gender","user_gender");
        String user_fname = sharedPreferences.getString("user_fname","user_fname");

        titleName.setText("Hey , "+user_fname+"!");
        // Optionally, use these values (e.g., display in a Toast or log)
        if (userID != null && user_employeeId != null) {
            Toast.makeText(this, "User ID: " + userID , Toast.LENGTH_SHORT).show();
            Toast.makeText(this, " User employee ID: " + user_employeeId, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, " User Gnder  " + user_gender, Toast.LENGTH_SHORT).show();
        }
        imageButton=findViewById(R.id.userProfileIcon);

            if(user_gender.equals("Male"))
            {
                imageButton.setBackgroundResource(R.drawable.profile);

            } else if (user_gender.equals("Female"))
            {
                imageButton.setBackgroundResource(R.drawable.profile2);

            }

        // Check if user_employeeId matches a unique ID in Firebase
        checkEmployeeId(user_employeeId);
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String user_managmentId = sharedPreferences.getString("user_managmentId","user_managmentId");
        Toast.makeText(this, "Hello, User Manage ID: " + user_managmentId, Toast.LENGTH_SHORT).show();


        userInfo = FirebaseDatabase.getInstance().getReference("PrimaryData").child("UserData").child(userID);
        // Fetch data from Firebase
        userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the employee data exists
                if (dataSnapshot.exists()) {
                    // Populate fields with Firebase data
                    String fnameValue = dataSnapshot.child("fName").getValue(String.class);
                    String lnameValue = dataSnapshot.child("lName").getValue(String.class);
                    String mobileValue = dataSnapshot.child("mobileNo").getValue(String.class);
                    String emailValue = dataSnapshot.child("email").getValue(String.class);
                    String dobValue = dataSnapshot.child("dob").getValue(String.class);
                    String genderValue = dataSnapshot.child("gender").getValue(String.class);
                    String locationValue = dataSnapshot.child("location").getValue(String.class);
                    String passwordValue = dataSnapshot.child("password").getValue(String.class);

                    // Logging values to Logcat
                    Log.d("UserData", "First Name: " + fnameValue);
                    Log.d("UserData", "Last Name: " + lnameValue);
                    Log.d("UserData", "Mobile: " + mobileValue);
                    Log.d("UserData", "Email: " + emailValue);
                    Log.d("UserData", "DOB: " + dobValue);
                    Log.d("UserData", "Gender: " + genderValue);
                    Log.d("UserData", "Location: " + locationValue);
                    Log.d("UserData", "Password: " + passwordValue);
                    saveUserProfile(fnameValue,lnameValue,mobileValue,emailValue,dobValue,genderValue,locationValue,passwordValue);

                } else {
                    // If the employee data doesn't exist
                    Toast.makeText(MainActivity.this, "No data found for the User.", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(MainActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);
        Adapter1 adapter1 = new Adapter1(getApplicationContext(), images, name);
        gridView.setAdapter(adapter1);

        // Set the OnItemClickListener for the GridView
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: // "View Map" is at index 0
                    openMap(); // Call the function for "View Map"
                    break;
                case 1: //
                    openMarketplace();// marketplace
                    break;
                case 4: //  (5th item, index 4)
                    wasteAlert();
                    break;

                case 2: //  (4th item, index 3)
                    openUserHistory();
                    break;

                case 3: // (4th item, index 3) - Event Request
                    openEventRequest();
                    break;

                case 5: // "About points" is at index 5
                    openAboutPoints();// Call the function for "About points"
                    break;

                // Add cases for other items if needed
            }
        });

    }

    private  void saveUserProfile(String fname, String lname,String mobile,String email,String dob,String gender,String location,String password){

        sharedPreferencesUserProfile = getSharedPreferences("UserProfileInfo", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferencesUserProfile.edit();
        editor.putString("fname", fname);
        editor.putString("lname", lname);
        editor.putString("mobile", mobile);
        editor.putString("email", email);
        editor.putString("dob", dob);
        editor.putString("gender", gender);
        editor.putString("location", location);
        editor.putString("password", password);
        editor.apply();

    }

    private void checkEmployeeId(String user_employeeId) {
        // Reference to Firebase
        DatabaseReference allEmployeeRef = FirebaseDatabase.getInstance().getReference("AllEmployee");

        // Query Firebase to check if any employee's unique ID matches the user_employeeId
        allEmployeeRef.orderByKey().equalTo(user_employeeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                // If the user_employeeId exists in Firebase
                DataSnapshot snapshot = task.getResult().child(user_employeeId);

                // Retrieve the employeeManagmentId from the matched entry
                String user_managmentId = snapshot.child("employeeManagmentId").getValue(String.class);

                // Display a Toast with the employeeManagmentId
                if (user_managmentId != null)
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_managmentId", user_managmentId);
                    editor.apply();  // Apply the changes
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Management ID not found!", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If the user_employeeId does not match any Firebase ID
                Toast.makeText(MainActivity.this, "Employee ID does not match", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(MainActivity.this, "Error checking employee ID", Toast.LENGTH_SHORT).show();
        });
    }

    public void checkPdf(){
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        // Retrieve the userID and user_employeeId from the Intent
        String user_managmentId = sharedPreferences.getString("user_managmentId","user_managmentId");
        Log.d("FirebaseData", "user_managmentId: " + user_managmentId);


        DatabaseReference certificateRef = FirebaseDatabase.getInstance().getReference("Certificate").child(user_managmentId);

        certificateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();

                if (dataSnapshot.exists()) {
                    Boolean flag = dataSnapshot.child("flag").getValue(Boolean.class);
                    String imgname = dataSnapshot.child("Imgname").getValue(String.class);

                    Log.d("FirebaseData", "Flag: " + flag);
                    Log.d("FirebaseData", "Imgname: " + imgname);
                    if(flag.equals(true)){
                        savePdfToDownloads();
                    }else
                    {
                        Toast.makeText(MainActivity.this, "You cannot download pdf.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("FirebaseData", "No data found for user_managmentId: " + user_managmentId);
                }
            } else {
                Log.d("FirebaseData", "Error getting data: " + task.getException());
            }
        });
    }

    public void savePdfToDownloads() {
        try {


            sharedPreferencesUserProfile = getSharedPreferences("UserProfileInfo", MODE_PRIVATE);
            String firstName = sharedPreferencesUserProfile.getString("fname", "Default Name");
            String lastName = sharedPreferencesUserProfile.getString("lname", "Default Last Name");
            String dob = sharedPreferencesUserProfile.getString("dob", "Default DOB");

            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            String orgName = "Karad Management";

            // Prepare ContentResolver and ContentValues to save PDF to Downloads
            ContentResolver resolver = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "certificate_" + firstName + ".pdf");  // PDF file name
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/");  // Save in Downloads folder

            // Insert into MediaStore and get the Uri
            Uri uri = resolver.insert(
                    MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL),
                    values
            );

            if (uri != null) {
                // Get OutputStream from the Uri
                OutputStream outputStream = resolver.openOutputStream(uri);

                if (outputStream != null) {
                    // Create the PDF using iText
                    PdfWriter writer = new PdfWriter(outputStream);
                    PdfDocument pdf = new PdfDocument(writer);

                    // Load the image from drawable folder using ContextCompat
                    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.cc2); // Get drawable from drawable folder
                    Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) drawable).getBitmap(); // Convert to Bitmap

                    // Get image dimensions (3604 x 2504)
                    float imageWidth = bitmap.getWidth();
                    float imageHeight = bitmap.getHeight();

                    // Set the PDF page size to match the image size (3604 x 2504)
                    pdf.setDefaultPageSize(new com.itextpdf.kernel.geom.PageSize(imageWidth, imageHeight));

                    // Convert Bitmap to ByteArrayOutputStream
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();

                    // Create ImageData from ByteArray
                    ImageData imageData = ImageDataFactory.create(byteArray);

                    // Create an Image object from ImageData
                    Image image = new Image(imageData);

                    // Set the image position to cover the entire page
                    image.setFixedPosition(0, 0); // Position the image at the top-left corner
                    image.scaleToFit(imageWidth, imageHeight); // Scale image to page size

                    // Create the document with the specified page size
                    Document document = new Document(pdf);

                    // Add the image to the document as the background
                    document.add(image);

                    // Now add the text content over the image
                    float margin = 100;  // Adjust this for text spacing from edges
                    float fontSize = 100f;

                    // Set starting y position
                    float yPosition = imageHeight / 2;
                    float textWidth = imageWidth - 2 * margin;

                    // Add text content positioned on the background image with font size 100f
                    document.add(new Paragraph("Smart Waste")
                            .setFixedPosition(margin, yPosition + 700, textWidth)
                            .setFontSize(200f)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("This is to certify that,")
                            .setFixedPosition(margin, yPosition + 400, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph(firstName + " " + lastName)
                            .setFixedPosition(margin, yPosition + 180, textWidth)
                            .setFontSize(130f)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("of " + orgName)
                            .setFixedPosition(margin, yPosition + 10, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("has successfully completed the Waste Submission Program and")
                            .setFixedPosition(margin, yPosition - 120, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("is hereby recognized as a valued member for the year " + currentYear + ".")
                            .setFixedPosition(margin, yPosition - 240, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("Date of Birth: " + dob)
                            .setFixedPosition(margin, yPosition - 360, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("We appreciate your commitment to environmental sustainability ")
                            .setFixedPosition(margin, yPosition - 480, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("and waste management.")
                            .setFixedPosition(margin, yPosition - 600, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    document.add(new Paragraph("Issued on: " + currentYear)
                            .setFixedPosition(margin, yPosition - 720, textWidth)
                            .setFontSize(fontSize)
                            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

                    // Now add the image icon below the "Issued on" paragraph
                    Drawable imageDrawable = ContextCompat.getDrawable(this, R.drawable.applogo); // Load the image icon
                    Bitmap imageBitmap = ((android.graphics.drawable.BitmapDrawable) imageDrawable).getBitmap(); // Convert to Bitmap

                    ByteArrayOutputStream imageByteArrayOutputStream = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageByteArrayOutputStream);
                    byte[] imageByteArray = imageByteArrayOutputStream.toByteArray();

                    ImageData imageIconData = ImageDataFactory.create(imageByteArray);

                    Image imageIcon = new Image(imageIconData);

                    imageIcon.scaleToFit(350, 350);

                    float imageYPosition = yPosition - 1100; // Adjust this value to position it below your text
                    imageIcon.setFixedPosition(margin + (imageWidth - 350) / 2, imageYPosition); // Center the image horizontally

                    document.add(imageIcon);

                    document.close();

                    Toast.makeText(MainActivity.this, "Save to downloads", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity", "Failed to open OutputStream from the Uri.");
                    Toast.makeText(MainActivity.this, "Failed to open OutputStream from the Uri.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("MainActivity", "Failed to obtain Uri from MediaStore.");
                Toast.makeText(MainActivity.this, "Failed to obtain Uri from MediaStore.", Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity", "Error saving PDF to Downloads folder: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error saving PDF to Downloads folder: ", Toast.LENGTH_SHORT).show();
        }
    }

    public void openMarketplace() {
        Intent intent = new Intent(MainActivity.this, MarketPlace.class);
        startActivity(intent);
    }
    public void openDustbin(View view) {
        Intent intent = new Intent(MainActivity.this, UserShowDustbinActivity.class);
        startActivity(intent);
    }
    public void openEventRequest() {
        Intent intent = new Intent(MainActivity.this, User_sendEventRequestActivity.class);
        startActivity(intent);
    }


    // Method to open UserHistory activity
    public void openSignIn(View view) {
        // Code to handle sign-in action
    }

    public void openUserHistory() {
        Intent intent = new Intent(MainActivity.this, History.class);
        startActivity(intent);
    }

    public void wasteAlert() {
        Intent intent = new Intent(MainActivity.this, WasteAlertActivity.class);
        startActivity(intent);
    }



    public void openRegister1(View view) {

    }

    public void openEmployee(View view) {
        Intent i=new Intent(MainActivity.this,User_Leaderboard.class);
        startActivity(i);
    }

    public void openAdmin(View view) {
        Intent i=new Intent(MainActivity.this,AdminActivity.class);
        startActivity(i);
    }


    public void openUserProfile(View view) {
        Intent i=new Intent(MainActivity.this,UserProfileActivity.class);
        startActivity(i);
    }

    public void openMap() {
        Intent i=new Intent(MainActivity.this,UserCarLocationActivity.class);
        startActivity(i);
    }

    public void openSample(View view) {
        Intent i=new Intent(MainActivity.this,SampleActivity.class);
        startActivity(i);
    }

    public void openAboutPoints() {
        Intent i=new Intent(MainActivity.this,UserAboutPointsA.class);
        startActivity(i);
    }

    public void downloadCertificate(View view) {
        checkPdf();
    }
}

class Adapter1 extends BaseAdapter {
    Context context;
    int data[];
    LayoutInflater inflater;
    String dataname[];
    Adapter1(Context context,int[] data, String name[]){
        this.context=context;
        this.data=data;
        this.dataname=name;
        inflater=(LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return dataname.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.layoutgrid,null);

        ImageView imageView=(ImageView) convertView.findViewById(R.id.image);
        imageView.setImageResource(data[position]);
        TextView textView=(TextView) convertView.findViewById(R.id.item);
        textView.setText(dataname[position]);
        return convertView;
    }
}