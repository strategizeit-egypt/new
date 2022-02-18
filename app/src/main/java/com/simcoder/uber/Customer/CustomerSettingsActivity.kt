package com.simcoder.uber.Customer

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import com.simcoder.uber.Objects.CustomerObject
import android.os.Bundle
import com.simcoder.uber.R
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import java.io.IOException

/*import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;*/ /**
 * Activity that displays the settings to the customer
 */
class CustomerSettingsActivity : AppCompatActivity() {
    private var mNameField: EditText? = null
    private var mPhoneField: EditText? = null
    private var mProfileImage: ImageView? = null

    //  private DatabaseReference mCustomerDatabase;
    private val userID: String? = null
    private var resultUri: Uri? = null
    var mCustomer = CustomerObject.getCustomer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_settings)
        mNameField = findViewById(R.id.name)
        mPhoneField = findViewById(R.id.phone)
        mProfileImage = findViewById(R.id.profileImage)
        val mConfirm = findViewById<Button>(R.id.confirm)

        //  FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //  userID = mAuth.getCurrentUser().getUid();
        //   mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        //  mCustomer = new CustomerObject(userID);
        userInfo
        mProfileImage?.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        })

        //  mConfirm.setOnClickListener(v -> /*saveUserInformation()*/);
        setupToolbar()
    }

    /**
     * Sets up toolbar with custom text and a listener
     * to go back to the previous activity
     */
    private fun setupToolbar() {
        val myToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(myToolbar)
        supportActionBar!!.title = getString(R.string.settings)
        myToolbar.setTitleTextColor(resources.getColor(R.color.white))
        val ab = supportActionBar!!
        ab.setDisplayHomeAsUpEnabled(true)
        myToolbar.setNavigationOnClickListener { v: View? -> finish() }
    }// mCustomer.parseData(dataSnapshot);

/*
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}

                mCustomer.parseData(dataSnapshot);

                mNameField.setText(mCustomer.getName());
                mPhoneField.setText(mCustomer.getPhone());


                if(!mCustomer.getProfileImage().equals("default"))
                    Glide.with(getApplication()).load(mCustomer.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/
    /**
     * Fetches current user's info and populates the design elements
     */
    private val userInfo: Unit
        private get() {
            // mCustomer.parseData(dataSnapshot);
            mNameField!!.setText(mCustomer.name)
            mPhoneField!!.setText(mCustomer.phone)
            if (mCustomer.profileImage != "default") Glide.with(application)
                .load(mCustomer.profileImage).apply(RequestOptions.circleCropTransform()).into(
                mProfileImage!!
            )

/*
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}

                mCustomer.parseData(dataSnapshot);

                mNameField.setText(mCustomer.getName());
                mPhoneField.setText(mCustomer.getPhone());


                if(!mCustomer.getProfileImage().equals("default"))
                    Glide.with(getApplication()).load(mCustomer.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/
        }

    /**
     * Saves current user 's info to the database.
     * If the resultUri is not null that means the profile image has been changed
     * and we need to upload it to the storage system and update the database with the new url
     */
    /*
    private void saveUserInformation() {
        String mName = mNameField.getText().toString();
        String mPhone = mPhoneField.getText().toString();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", mName);
        userInfo.put("phone", mPhone);
        mCustomerDatabase.updateChildren(userInfo);

        if(resultUri != null) {

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            UploadTask uploadTask = filePath.putFile(resultUri);

            uploadTask.addOnFailureListener(e -> {
                finish();
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                mCustomerDatabase.updateChildren(newImage);

                finish();
            }).addOnFailureListener(exception -> {
                finish();
            }));
        }else{
            finish();
        }

    }
*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            resultUri = data!!.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, resultUri)
                Glide.with(application)
                    .load(bitmap) // Uri of the picture
                    .apply(RequestOptions.circleCropTransform())
                    .into(mProfileImage!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}