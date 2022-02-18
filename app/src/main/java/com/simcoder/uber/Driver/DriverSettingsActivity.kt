package com.simcoder.uber.Driver

import androidx.appcompat.app.AppCompatActivity
import com.simcoder.uber.Objects.TypeObject
import android.os.Bundle
import com.simcoder.uber.R
import android.content.Intent
import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.EditText
import com.simcoder.uber.Objects.DriverObject
import com.simcoder.uber.Driver.DriverChooseTypeActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.simcoder.uber.Utils.Utils
import java.io.IOException
import java.util.HashMap

/*import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;*/ /**
 * Activity that displays the settings to the Driver
 */
class DriverSettingsActivity : AppCompatActivity() {
    private var mNameField: EditText? = null
    private var mPhoneField: EditText? = null
    private var mCarField: EditText? = null
    private var mLicense: EditText? = null
    private var mService: EditText? = null
    private var mProfileImage: ImageView? = null

    // private DatabaseReference mDriverDatabase;
    private val userID: String? = null
    private var resultUri: Uri? = null
    var mDriver = DriverObject.getDriver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_settings)
        mNameField = findViewById(R.id.name)
        mPhoneField = findViewById(R.id.phone)
        mCarField = findViewById(R.id.car)
        mLicense = findViewById(R.id.license)
        mProfileImage = findViewById(R.id.profileImage)
        mService = findViewById(R.id.service)
        val mConfirm = findViewById<Button>(R.id.confirm)

        //  FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //   userID = mAuth.getCurrentUser().getUid();
        //mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userID);
        userInfo
        mProfileImage?.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        })
        mService?.setOnClickListener(View.OnClickListener { view: View? ->
            val i = Intent(this@DriverSettingsActivity, DriverChooseTypeActivity::class.java)
            i.putExtra("service", mDriver.service)
            startActivityForResult(i, 2)
        })
        mConfirm.setOnClickListener { v: View? -> saveUserInformation() }
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
    }//  mService.setText(Utils.getTypeById(DriverSettingsActivity.this, mDriver.getService()).getName());
    /*
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                mDriver.parseData(dataSnapshot);
                mNameField.setText(mDriver.getName());
                mPhoneField.setText(mDriver.getPhone());
                mCarField.setText(mDriver.getCar());
                mLicense.setText(mDriver.getLicense());
                mService.setText(Utils.getTypeById(DriverSettingsActivity.this, mDriver.getService()).getName());

                if (!mDriver.getProfileImage().equals("default"))
                    Glide.with(getApplication()).load(mDriver.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });*/
    /**
     * Fetches current user's info and populates the design elements
     */
    private val userInfo: Unit
        private get() {
            mNameField!!.setText(mDriver.name)
            mPhoneField!!.setText(mDriver.phone)
            mCarField!!.setText(mDriver.car)
            mLicense!!.setText(mDriver.license)
            //  mService.setText(Utils.getTypeById(DriverSettingsActivity.this, mDriver.getService()).getName());
            if (mDriver.profileImage != "default") Glide.with(application)
                .load(mDriver.profileImage).apply(RequestOptions.circleCropTransform()).into(
                mProfileImage!!
            )
            /*
                mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }
                        mDriver.parseData(dataSnapshot);
                        mNameField.setText(mDriver.getName());
                        mPhoneField.setText(mDriver.getPhone());
                        mCarField.setText(mDriver.getCar());
                        mLicense.setText(mDriver.getLicense());
                        mService.setText(Utils.getTypeById(DriverSettingsActivity.this, mDriver.getService()).getName());
        
                        if (!mDriver.getProfileImage().equals("default"))
                            Glide.with(getApplication()).load(mDriver.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                    }
        
                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });*/
        }

    /**
     * Saves current user 's info to the database.
     * If the resultUri is not null that means the profile image has been changed
     * and we need to upload it to the storage system and update the database with the new url
     */
    private fun saveUserInformation() {
        val name = mNameField!!.text.toString()
        val phone = mPhoneField!!.text.toString()
        val car = mCarField!!.text.toString()
        val license = mLicense!!.text.toString()
        val service = mDriver.service
        val userInfo: MutableMap<String, Any> = HashMap()
        userInfo["name"] = name
        userInfo["phone"] = phone
        userInfo["car"] = car
        userInfo["license"] = license
        userInfo["service"] = service
        /*        mDriverDatabase.updateChildren(userInfo);

        if(resultUri != null) {

            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);

            UploadTask uploadTask = filePath.putFile(resultUri);
            uploadTask.addOnFailureListener(e -> {
                finish();
            });
            uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                Map newImage = new HashMap();
                newImage.put("profileImageUrl", uri.toString());
                mDriverDatabase.updateChildren(newImage);

                finish();
            }).addOnFailureListener(exception -> {
                finish();
            }));
        }else{
            finish();
        }*/
    }

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
        if (requestCode == 2 && resultCode == RESULT_OK) {
            val result = data!!.getStringExtra("result")
            mDriver.service = result
            mService!!.setText(Utils.getTypeById(this@DriverSettingsActivity, result).name)
        }
    }
}