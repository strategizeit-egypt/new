package com.simcoder.uber.Customer

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.ncorti.slidetoact.SlideToActView
import com.ncorti.slidetoact.SlideToActView.OnSlideCompleteListener
import com.simcoder.uber.Adapters.TypeAdapter
import com.simcoder.uber.History.HistoryActivity
import com.simcoder.uber.Login.LauncherActivity
import com.simcoder.uber.Objects.*
import com.simcoder.uber.Payment.PaymentActivity
import com.simcoder.uber.R
import com.simcoder.uber.Utils.Utils
import com.simcoder.uber.agora.driver.presentation.viewmodel.DriverHomeViewModel
import com.simcoder.uber.bitmapDescriptorFromVector
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/*import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/ /**
 * Main Activity displayed to the customer
 */
class CustomerMapActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback, DirectionCallback {
    var TIMEOUT_MILLISECONDS = 20000
    var CANCEL_OPTION_MILLISECONDS = 10000
    private var mMap: GoogleMap? = null
    var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mRequest: SlideToActView? = null
    private var pickupLocation: LocationObject? = null
    private var currentLocation: LocationObject? = null
    private var destinationLocation: LocationObject? = null
    private var requestBol = false
    var bottomSheetStatus = 1
    private var destinationMarker: Marker? = null
    private var pickupMarker: Marker? = null
    private var mDriverInfo: LinearLayout? = null
    private var mRadioLayout: LinearLayout? = null
    private var mLocation: LinearLayout? = null
    private var mLooking: LinearLayout? = null
    private var mTimeout: LinearLayout? = null
    private var mDriverProfileImage: ImageView? = null
    private var mDriverName: TextView? = null
    private var mDriverCar: TextView? = null
    private var mDriverLicense: TextView? = null
    private var mRatingText: TextView? = null
    private var autocompleteFragmentTo: EditText? = null
    private var autocompleteFragmentFrom: EditText? = null
    var autocompleteFragmentFromContainer: CardView? = null
    var mContainer: CardView? = null
    var mCallDriver: FloatingActionButton? = null
    var mCancel: FloatingActionButton? = null
    var mCancelTimeout: FloatingActionButton? = null
    var mCurrentLocation: FloatingActionButton? = null
    var drawer: DrawerLayout? = null
    var mCurrentRide = RideObject.getRide()
    private var mAdapter: TypeAdapter? = null
    var typeArrayList = ArrayList<TypeObject>()
    private var driverFound = false
    var currentMarker :Marker?= null

    // private ValueEventListener driveHasEndedRefListener;
    var cancelHandler: Handler? = null
    var timeoutHandler: Handler? = null
    var path: ArrayList<LatLng?> = ArrayList()
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[DriverHomeViewModel::class.java]
    }
    /* LatLng(30.04295651892869, 31.237683780491352),
             LatLng(30.043503604507773, 31.238172613084313),
             LatLng(30.043918052473565, 31.238405629992485),
             LatLng(30.047092817295372, 31.23982384800911),
             LatLng(30.047467490295887, 31.238607130944732),
             LatLng(30.04782010534185, 31.237630136311058),
             LatLng(30.048222055941384, 31.236408054828644),
             LatLng(30.048952237839277, 31.236872076988224)
         )*/
    @SuppressLint("RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_customer)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        checkForTokens()
        viewModel.toastEvent.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }
        path.add(
            LatLng(
                29.990755, 31.151274999999998
            )
        )
        /* path.add(new LatLng(29.979233341903686, 31.153377257287502));
        path.add(new LatLng(29.979877197373916, 31.15271240472794));
        path.add(new LatLng(29.980549218884274, 31.152932345867157));
        path.add(new LatLng(29.981874952393586, 31.15452256053686));*/
        /*   path.add(new LatLng(29.983403952745523, 31.15271408110857));
        path.add(new LatLng(29.98525149972834, 31.151796765625473));
        path.add(new LatLng(29.98806370110867, 31.148895621299747));*/path.add(
            LatLng(
                29.9884319181926,
                31.149982586503032
            )
        )
        path.add(LatLng(29.98914627867216, 31.148247867822644))
        path.add(LatLng(29.990676618984686, 31.147549487650398))
        path.add(LatLng(29.993810113180338, 31.145635731518272))
        path.add(LatLng(29.998298930429115, 31.143226772546768))
        path.add(LatLng(29.99978818913346, 31.14007785916328))

        //  mCurrentRide = new RideObject(CustomerMapActivity.this, null);
        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer?.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        userData
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mDriverInfo = findViewById(R.id.driverInfo)
        mRadioLayout = findViewById(R.id.radioLayout)
        mDriverProfileImage = findViewById(R.id.driverProfileImage)
        mDriverName = findViewById(R.id.driverName)
        mDriverCar = findViewById(R.id.driverCar)
        mDriverLicense = findViewById(R.id.driverPlate)
        mCallDriver = findViewById(R.id.phone)
        mRatingText = findViewById(R.id.ratingText)
        mContainer = findViewById(R.id.container_card)
        autocompleteFragmentTo = findViewById(R.id.place_to)
        autocompleteFragmentFrom = findViewById(R.id.place_from)
        autocompleteFragmentFromContainer = findViewById(R.id.place_from_container)
        mCurrentLocation = findViewById(R.id.current_location)
        mLocation = findViewById(R.id.location_layout)
        mLooking = findViewById(R.id.looking_layout)
        mTimeout = findViewById(R.id.timeout_layout)
        val mLogout = findViewById<TextView>(R.id.logout)
        mRequest = findViewById(R.id.request)
        mCancel = findViewById(R.id.cancel)
        mCancelTimeout = findViewById(R.id.cancel_looking)
        mLogout.setOnClickListener { v: View? -> logOut() }
        mCancelTimeout?.setOnClickListener(View.OnClickListener { v: View? ->
            bottomSheetStatus = 0
            mCurrentRide!!.cancelRide()
            endRide()
        })
        mRequest?.onSlideCompleteListener =
            object : OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    startRideRequest()
                }
            }
        mCancel?.setOnClickListener(View.OnClickListener { v: View? ->
            bottomSheetStatus = 0
            mCurrentRide!!.cancelRide()
            endRide()
        })
        mCallDriver?.setOnClickListener(View.OnClickListener { view: View? ->
            if (mCurrentRide == null) {
                Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    getString(R.string.driver_no_phone),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent =
                    Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCurrentRide!!.driver.phone))
                startActivity(intent)
            } else {
                Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    getString(R.string.no_phone_call_permissions),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
        val mDrawerButton = findViewById<ImageView>(R.id.drawerButton)
        mDrawerButton.setOnClickListener { v: View? -> drawer?.openDrawer(Gravity.LEFT) }
        mCurrentLocation?.setOnClickListener(View.OnClickListener { view: View? ->
            autocompleteFragmentFrom?.setText(getString(R.string.current_location))
            mCurrentLocation?.setImageDrawable(resources.getDrawable(R.drawable.ic_location_on_primary_24dp))
            pickupLocation = currentLocation
            if (pickupLocation == null) {
               return@OnClickListener
            }
            fetchLocationName()
            mMap!!.clear()
            pickupMarker = mMap!!.addMarker(
                MarkerOptions().position(mCurrentRide!!.current.coordinates).title("Pickup").icon(
                    BitmapDescriptorFactory.fromBitmap(
                        generateBitmap(this@CustomerMapActivity, pickupLocation!!.name, null)
                    )
                )
            )
            //     mCurrentRide.setPickup(pickupLocation);
            autocompleteFragmentFrom?.setText(pickupLocation!!.name)
            if (destinationLocation != null) {
                destinationMarker = mMap!!.addMarker(
                    MarkerOptions().position(mCurrentRide!!.destination.coordinates)
                        .title("Destination").icon(
                        BitmapDescriptorFactory.fromBitmap(
                            generateBitmap(
                                this@CustomerMapActivity,
                                destinationLocation!!.name,
                                null
                            )
                        )
                    )
                )
                bringBottomSheetDown()
            }
            MapAnimator()
            routeToMarker
            mRequest?.text = getString(R.string.call_uber)
        })
        bringBottomSheetUp()
        initPlacesAutocomplete()
        initRecyclerView()

        viewModel.tripLocation.observe(this){pair ->
            mMap?.let {
                currentMarker?.remove()
                destinationMarker?.remove()
                currentMarker = mMap!!.addMarker(
                    MarkerOptions()
                        .position(LatLng(pair.first.lat,pair.first.lng ))
                        .anchor(0.5f, 1f)
                        .icon(bitmapDescriptorFromVector(this, R.drawable.ic_car))
                ).apply { tag = "curren" }
                destinationMarker = mMap!!.addMarker(
                    MarkerOptions()
                        .position(LatLng(pair.second.lat,pair.second.lng ))
                        .anchor(0.5f, 1f)
                        .icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_on_grey_24dp))
                ).apply { tag = "des" }
            }
        }
        viewModel.currentLocationUpdates.observe(this){param ->
            mMap?.let {
                currentMarker?.remove()
                currentMarker = mMap!!.addMarker(
                    MarkerOptions()
                        .position(LatLng(param.lat,param.lng ))
                        .anchor(0.5f, 1f)
                        .icon(bitmapDescriptorFromVector(this, R.drawable.ic_car))
                ).apply { tag = "curren" }
            }
        }
        viewModel.endingEvent.observe(this){
            mMap?.let {
                it.clear()
            }
        }
        //   isRequestInProgress();
    }


    private fun checkForTokens() {
        val remoteConfig = getFireBaseConfigs()
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                try {
                    if (task.isSuccessful) {
                        viewModel.loginAndObserveONLocation(remoteConfig.getString("passenger_token"))
                        Toast.makeText(this, remoteConfig.getString("passenger_token"), Toast.LENGTH_SHORT).show()
                        //  Toast.makeText(this, remoteConfig.getString("passenger_token"), Toast.LENGTH_SHORT).show()
                    }
                } catch (exception: Exception) {
                }


            }
            .addOnFailureListener {}
    }

    private fun getFireBaseConfigs(): FirebaseRemoteConfig {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings
            .Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        // remoteConfig.setDefaultsAsync(R.xml.remote_config)

        return remoteConfig

    }
    /**
     * Handles stating the ride request.
     * Starts up two timers. The first will show up after CANCEL_OPTION_MILLISECONDS
     * and it will display a layout with a button for the user to be able to cancel the ride..
     * The second will cancel the ride automatically if the TIMEOUT_MILLISECONDS is reached.
     */
    private fun startRideRequest() {
/*        cancelHandler = new Handler();
        cancelHandler.postDelayed(() -> {
            if (mCurrentRide == null) {
                return;
            }
            if (mCurrentRide.getDriver() == null) {
                runOnUiThread(() -> {
                    mTimeout.setVisibility(View.VISIBLE);
                });
            }
        }, CANCEL_OPTION_MILLISECONDS);

        timeoutHandler = new Handler();
        cancelHandler.postDelayed(() -> {
            if (mCurrentRide == null) {
                return;
            }
            if (mCurrentRide.getDriver() == null) {
                runOnUiThread(() -> {
                    bottomSheetStatus = 0;
                    mCurrentRide.cancelRide();
                    endRide();
                    new AlertDialog.Builder(CustomerMapActivity.this)
                            .setTitle(getResources().getString(R.string.no_drivers_around))
                            .setMessage(getResources().getString(R.string.no_driver_found))
                            .setPositiveButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                            .setIcon(R.drawable.ic_cancel_black_24dp)
                            .show();

                });
            }
        }, TIMEOUT_MILLISECONDS);*/
        bringBottomSheetDown()
        //   if (!requestBol) {
/*
            mCurrentRide.setDestination(destinationLocation);
            mCurrentRide.setPickup(pickupLocation);
            mCurrentRide.setRequestService(mAdapter.getSelectedItem().getId());
            mCurrentRide.setDistance(routeData.get(0));
            mCurrentRide.setDuration(routeData.get(1));
*/
/*
            if (mCurrentRide.checkRide() == -1) {
                return;
            }

            requestBol = true;*/mRequest!!.text = resources.getString(R.string.getting_driver)

        //  mCurrentRide.postRideInfo();
        requestListener()
        //   }
    }

    /**
     * Initializes the recyclerview that shows the costumer the
     * available car types
     */
    private fun initRecyclerView() {
        typeArrayList = Utils.getTypeList(this@CustomerMapActivity)
        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@CustomerMapActivity)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = TypeAdapter(typeArrayList, this@CustomerMapActivity, routeData)
        mRecyclerView.adapter = mAdapter
    }

    /**
     * Handles showing the bottom sheet with animation.
     */
    private fun bringBottomSheetUp() {
        val slideUp = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_up
        )
        mContainer!!.startAnimation(slideUp)
        mContainer!!.visibility = View.VISIBLE
    }

    /**
     * Handles hiding the bottom sheet with animation.
     * Also takes care of hiding or showing the elements in it
     * depending on the current state of the request.
     */
    private fun bringBottomSheetDown() {
        val slideDown = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.slide_down
        )
        slideDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                when (bottomSheetStatus) {
                    0 -> {
                        bottomSheetStatus = 1
                        destinationLocation = null
                        pickupLocation = null
                        mCurrentRide!!.current = null
                        mCurrentRide!!.destination = null
                        /*        autocompleteFragmentFrom.setText(getString(R.string.from));
                        autocompleteFragmentTo.setText(getString(R.string.to));*/mCurrentLocation!!.setImageDrawable(
                            resources.getDrawable(R.drawable.ic_location_on_grey_24dp)
                        )
                        mMap!!.clear()
                        MapAnimator()
                        erasePolylines()
                        mRadioLayout!!.visibility = View.GONE
                        mLocation!!.visibility = View.VISIBLE
                        mLooking!!.visibility = View.GONE
                        mDriverInfo!!.visibility = View.GONE
                    }
                    1 -> {
                        bottomSheetStatus = 2
                        mRequest!!.resetSlider()
                        mRadioLayout!!.visibility = View.VISIBLE
                        mLocation!!.visibility = View.GONE
                        mLooking!!.visibility = View.GONE
                        mDriverInfo!!.visibility = View.GONE
                        mTimeout!!.visibility = View.GONE
                    }
                    2 -> {
                        bottomSheetStatus = 3
                        mLocation!!.visibility = View.GONE
                        mRadioLayout!!.visibility = View.GONE
                        mLooking!!.visibility = View.VISIBLE
                        mDriverInfo!!.visibility = View.GONE
                    }
                    3 -> {
                        bottomSheetStatus = 0
                        mLocation!!.visibility = View.GONE
                        mRadioLayout!!.visibility = View.GONE
                        mLooking!!.visibility = View.GONE
                        mDriverInfo!!.visibility = View.VISIBLE
                    }
                }
                bringBottomSheetUp()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        mContainer!!.startAnimation(slideDown)
    }

    /**
     * Init Places according the updated google api and
     * listen for user inputs, when a user chooses a place change the values
     * of destination and destinationLocation so that the user can call a driver
     */
    fun initPlacesAutocomplete() {
        autocompleteFragmentFrom!!.setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!autocompleteFragmentFrom!!.text.toString().isEmpty()
                    && !autocompleteFragmentTo!!.text.toString().isEmpty()
                ) {
                    currentDrivers
                }
                return@setOnKeyListener true
            }
            false
        }
        autocompleteFragmentTo!!.setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (!autocompleteFragmentFrom!!.text.toString().isEmpty()
                    && !autocompleteFragmentTo!!.text.toString().isEmpty()
                ) {
                    currentDrivers
                }
                return@setOnKeyListener true
            }
            false
        }
        /*        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        autocompleteFragmentTo.setOnClickListener(v -> {
*/
        /*            if (requestBol) {
                return;
            }*/
        /*
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                    .build(getApplicationContext());
            startActivityForResult(intent, 1);
        });

        autocompleteFragmentFrom.setOnClickListener(v -> {
        */
        /*    if (requestBol) {
                return;
            }*/
        /*
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                    .build(getApplicationContext());
            startActivityForResult(intent, 2);
        });*/
    }/* DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View header = navigationView.getHeaderView(0);

                    CustomerObject mCustomer = new CustomerObject();
                    mCustomer.parseData(dataSnapshot);

                    TextView mUsername = header.findViewById(R.id.usernameDrawer);
                    ImageView mProfileImage = header.findViewById(R.id.imageViewDrawer);

                    mUsername.setText(mCustomer.getName());

                    if (!mCustomer.getProfileImage().equals("default"))
                        Glide.with(getApplication()).load(mCustomer.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })*/

    /**
     * Fetches current user's info and populates the design elements
     */
    private val userData: Unit
        private get() {
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            val header = navigationView.getHeaderView(0)
            val mUsername = header.findViewById<TextView>(R.id.usernameDrawer)
            val mProfileImage = header.findViewById<ImageView>(R.id.imageViewDrawer)
            mUsername.text = CustomerObject.getCustomer().name
            if (CustomerObject.getCustomer().profileImage != "default") Glide.with(application)
                .load(CustomerObject.getCustomer().profileImage)
                .apply(RequestOptions.circleCropTransform()).into(mProfileImage)
            /* DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View header = navigationView.getHeaderView(0);

                    CustomerObject mCustomer = new CustomerObject();
                    mCustomer.parseData(dataSnapshot);

                    TextView mUsername = header.findViewById(R.id.usernameDrawer);
                    ImageView mProfileImage = header.findViewById(R.id.imageViewDrawer);

                    mUsername.setText(mCustomer.getName());

                    if (!mCustomer.getProfileImage().equals("default"))
                        Glide.with(getApplication()).load(mCustomer.getProfileImage()).apply(RequestOptions.circleCropTransform()).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        })*/
        }/*
        FirebaseDatabase.getInstance().getReference().child("ride_info").orderByChild("customerId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                for (DataSnapshot mData : dataSnapshot.getChildren()) {
                    mCurrentRide = new RideObject();
                    mCurrentRide.parseData(mData);

                    if (mCurrentRide.getCancelled() || mCurrentRide.getEnded()) {
                        mCurrentRide = new RideObject();
                        return;
                    }

                    if (mCurrentRide.getDriver() == null) {
                        mTimeout.setVisibility(View.VISIBLE);
                        bottomSheetStatus = 2;
                    } else {
                        bottomSheetStatus = 3;
                    }
                    bringBottomSheetDown();
                    requestListener();
                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/

    /**
     * Checks if request is in progress by looking at the last ride_info child that the
     * current customer was a part of and if that last ride is still ongoing then
     * start all of the relevant variables up, with that ride info.
     */
    private val isRequestInProgress: Unit
        private get() {
/*
        FirebaseDatabase.getInstance().getReference().child("ride_info").orderByChild("customerId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }

                for (DataSnapshot mData : dataSnapshot.getChildren()) {
                    mCurrentRide = new RideObject();
                    mCurrentRide.parseData(mData);

                    if (mCurrentRide.getCancelled() || mCurrentRide.getEnded()) {
                        mCurrentRide = new RideObject();
                        return;
                    }

                    if (mCurrentRide.getDriver() == null) {
                        mTimeout.setVisibility(View.VISIBLE);
                        bottomSheetStatus = 2;
                    } else {
                        bottomSheetStatus = 3;
                    }
                    bringBottomSheetDown();
                    requestListener();
                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/
        }

    /**
     * Listener for the current request.
     */
    private fun requestListener() {
        /*       if (mCurrentRide == null) {
            return;
        }*/
        driverInfo
        driverLocation
   //     renderRoute()

/*
        driveHasEndedRefListener = mCurrentRide.getRideRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                RideObject mRide = new RideObject();
                mRide.parseData(dataSnapshot);

                if (mRide.getCancelled() || mRide.getEnded()) {
                    if (!mCurrentRide.getEnded() && mRide.getEnded()) {
                        mCurrentRide.showDialog(CustomerMapActivity.this);
                    }
                    cancelHandler.removeCallbacksAndMessages(null);
                    timeoutHandler.removeCallbacksAndMessages(null);
                    bottomSheetStatus = 0;
                    endRide();

                    if (mRide.getCancelledType() == 11) {
                        new AlertDialog.Builder(CustomerMapActivity.this)
                                .setTitle(getResources().getString(R.string.no_default_payment))
                                .setMessage(getResources().getString(R.string.no_payment_available_message))
                                .setPositiveButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                                .setIcon(R.drawable.ic_cancel_black_24dp)
                                .show();
                    }
                    return;
                }

                if (mCurrentRide.getDriver() == null && mRide.getDriver() != null) {
                    try {
                        mCurrentRide = (RideObject) mRide.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    cancelHandler.removeCallbacksAndMessages(null);
                    timeoutHandler.removeCallbacksAndMessages(null);

                    getDriverInfo();
                    getDriverLocation();
                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/
    }

    /**
     * Get's most updated driver location and it's always checking for movements.
     * Even though we used geofire to push the location of the driver we can use a normal
     * Listener to get it's location with no problem.
     * 0 -> Latitude
     * 1 -> Longitudde
     */
    private val mDriverMarker: Marker? = null/*        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(LocationObject.getDriverStartLocation().getCoordinates());
        builder.include(RideObject.getRide().getCurrent().getCoordinates());
        //   builder.include(new LatLng(0,0));
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);*/

    /*        if (mCurrentRide.getDriver().getId() == null) {
            return;
        }*/
    /*      driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(mCurrentRide.getDriver().getId()).child("l");
          driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.exists() && requestBol) {
                      List<Object> map = (List<Object>) dataSnapshot.getValue();
  
                      if(map == null){
                          return;
                      }
                      double locationLat = 0;
                      double locationLng = 0;
                      if (map.get(0) != null) {
                          locationLat = Double.parseDouble(map.get(0).toString());
                      }
                      if (map.get(1) != null) {
                          locationLng = Double.parseDouble(map.get(1).toString());
                      }
                      LocationObject mDriverLocation = new LocationObject(new LatLng(locationLat, locationLng), "");
                      if (mDriverMarker != null) {
                          mDriverMarker.remove();
                      }
                      Location loc1 = new Location("");
                      loc1.setLatitude(pickupLocation.getCoordinates().latitude);
                      loc1.setLongitude(pickupLocation.getCoordinates().longitude);
  
                      Location loc2 = new Location("");
                      loc2.setLatitude(mDriverLocation.getCoordinates().latitude);
                      loc2.setLongitude(mDriverLocation.getCoordinates().longitude);
  
                      float distance = loc1.distanceTo(loc2);
  
                      if (distance < 100) {
                          mRequest.setText(getResources().getString(R.string.driver_here));
                      } else {
                          mRequest.setText(getResources().getString(R.string.driver_found));
                      }
  
                      mCurrentRide.getDriver().setLocation(mDriverLocation);
  
  
                      mDriverMarker = mMap.addMarker(new MarkerOptions().position(mCurrentRide.getDriver().getLocation().getCoordinates()).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                  }
  
              }
  
              @Override
              public void onCancelled(@NotNull DatabaseError databaseError) {
              }
          });
  */
    /* private DatabaseReference driverLocationRef;
    private ValueEventListener driverLocationRefListener;*/
    private val driverLocation: Unit
        private get() {
            val mDriverMarker = mMap!!.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                    .position(
                        LatLng(
                            29.990755, 31.151274999999998
                        )
                    )
                    .title(DriverObject.getDriver().name)
            )
            mDriverMarker.tag = "car"
            /*        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(LocationObject.getDriverStartLocation().getCoordinates());
        builder.include(RideObject.getRide().getCurrent().getCoordinates());
        //   builder.include(new LatLng(0,0));
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);*/
/*        if (mCurrentRide.getDriver().getId() == null) {
            return;
        }*/
            /*      driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(mCurrentRide.getDriver().getId()).child("l");
        driverLocationRefListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();

                    if(map == null){
                        return;
                    }
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LocationObject mDriverLocation = new LocationObject(new LatLng(locationLat, locationLng), "");
                    if (mDriverMarker != null) {
                        mDriverMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickupLocation.getCoordinates().latitude);
                    loc1.setLongitude(pickupLocation.getCoordinates().longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(mDriverLocation.getCoordinates().latitude);
                    loc2.setLongitude(mDriverLocation.getCoordinates().longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance < 100) {
                        mRequest.setText(getResources().getString(R.string.driver_here));
                    } else {
                        mRequest.setText(getResources().getString(R.string.driver_found));
                    }

                    mCurrentRide.getDriver().setLocation(mDriverLocation);


                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(mCurrentRide.getDriver().getLocation().getCoordinates()).title("your driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)));
                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/
        }//     new SendNotification("You have a customer waiting", "New Ride", mCurrentRide.getDriver().getNotificationKey());
    /*
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mCurrentRide.getDriver().getId());
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    mCurrentRide.getDriver().parseData(dataSnapshot);

                    mDriverName.setText(mCurrentRide.getDriver().getNameDash());
                    mDriverCar.setText(mCurrentRide.getDriver().getCarDash());
                    mDriverLicense.setText(mCurrentRide.getDriver().getLicenseDash());
                    if (mCurrentRide.getDriver().getProfileImage().equals("default")) {
                        mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
                    } else {
                        Glide.with(getApplication())
                                .load(mCurrentRide.getDriver().getProfileImage())
                                .apply(RequestOptions.circleCropTransform())
                                .into(mDriverProfileImage);
                    }


                    mRatingText.setText(String.valueOf(mCurrentRide.getDriver().getDriverRatingString()));

                    bringBottomSheetDown();

                    new SendNotification("You have a customer waiting", "New Ride", mCurrentRide.getDriver().getNotificationKey());
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });*/
    /**
     * Get all the user information that we can get from the user's database.
     */
    private val driverInfo: Unit
        private get() {
            mCurrentRide = RideObject.getRide()
            Handler().postDelayed({
                mDriverName!!.text = mCurrentRide.driver.name
                mDriverCar!!.text = mCurrentRide.driver.carDash
                mDriverLicense!!.text = mCurrentRide.driver.licenseDash
                if (mCurrentRide.driver.profileImage == "default") {
                    mDriverProfileImage!!.setImageResource(R.mipmap.ic_default_user)
                } else {
                    Glide.with(application)
                        .load(mCurrentRide.driver.profileImage)
                        .apply(RequestOptions.circleCropTransform())
                        .into(mDriverProfileImage!!)
                }
                mRatingText!!.text = mCurrentRide.driver.driverRatingString.toString()
                bringBottomSheetDown()

                //     new SendNotification("You have a customer waiting", "New Ride", mCurrentRide.getDriver().getNotificationKey());
            }, 2000)
            /*
                DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mCurrentRide.getDriver().getId());
                mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
        
                            mCurrentRide.getDriver().parseData(dataSnapshot);
        
                            mDriverName.setText(mCurrentRide.getDriver().getNameDash());
                            mDriverCar.setText(mCurrentRide.getDriver().getCarDash());
                            mDriverLicense.setText(mCurrentRide.getDriver().getLicenseDash());
                            if (mCurrentRide.getDriver().getProfileImage().equals("default")) {
                                mDriverProfileImage.setImageResource(R.mipmap.ic_default_user);
                            } else {
                                Glide.with(getApplication())
                                        .load(mCurrentRide.getDriver().getProfileImage())
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(mDriverProfileImage);
                            }
        
        
                            mRatingText.setText(String.valueOf(mCurrentRide.getDriver().getDriverRatingString()));
        
                            bringBottomSheetDown();
        
                            new SendNotification("You have a customer waiting", "New Ride", mCurrentRide.getDriver().getNotificationKey());
                        }
                    }
        
                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });*/
        }

    /**
     * End Ride by removing all of the active listeners,
     * returning all of the values to the default state
     * and clearing the map from markers
     */
    private fun endRide() {
        if (cancelHandler != null) {
            cancelHandler!!.removeCallbacksAndMessages(null)
        }
        if (timeoutHandler != null) {
            timeoutHandler!!.removeCallbacksAndMessages(null)
        }
        requestBol = false
        /*        if (driverLocationRefListener != null)
            driverLocationRef.removeEventListener(driverLocationRefListener);

*/
        /*        if (driveHasEndedRefListener != null && mCurrentRide.getRideRef() != null)
            mCurrentRide.getRideRef().removeEventListener(driveHasEndedRefListener);*/
        /*

        if (mCurrentRide != null && driverFound) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mCurrentRide.getDriver().getId()).child("customerRequest");
            driverRef.removeValue();
        }*/pickupLocation = null
        destinationLocation = null
        driverFound = false
        //  String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

/*        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, (key, error) -> {
        });*/if (destinationMarker != null) {
            destinationMarker!!.remove()
        }
        if (pickupMarker != null) {
            pickupMarker!!.remove()
        }
        mDriverMarker?.remove()
        mMap!!.clear()
        mRequest!!.text = getString(R.string.call_uber)
        mDriverName!!.text = ""
        mDriverCar!!.text = getString(R.string.destination)
        mDriverProfileImage!!.setImageResource(R.mipmap.ic_default_user)
        autocompleteFragmentTo!!.setText(getString(R.string.to))
        autocompleteFragmentFrom!!.setText(getString(R.string.from))
        mCurrentLocation!!.setImageDrawable(resources.getDrawable(R.drawable.ic_location_on_grey_24dp))
        mCurrentRide = RideObject(this@CustomerMapActivity, null)
        driversAround
        bringBottomSheetDown()
        zoomUpdated = false
        mAdapter!!.selectedItem = typeArrayList[0]
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * Find and update user's location.
     * The update interval is set to 1000Ms and the accuracy is set to PRIORITY_HIGH_ACCURACY,
     * If you're having trouble with battery draining too fast then change these to lower values
     *
     * @param googleMap - Map object
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.setMapStyle(
            MapStyleOptions(
                resources
                    .getString(R.string.style_json)
            )
        )
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mMap!!.isMyLocationEnabled = true
            } else {
                checkLocationPermission()
            }
        }
        mMap!!.setOnMarkerClickListener { marker: Marker ->
            if (marker.tag === "car") {
                startRideRequest()
            }
            true
        }
        //    renderRoute();
    }

    private fun renderRoute() {
        path.clear()
        path.add(
            LatLng(
                29.990755, 31.151274999999998
            )
        )
        /* path.add(new LatLng(29.979233341903686, 31.153377257287502));
        path.add(new LatLng(29.979877197373916, 31.15271240472794));
        path.add(new LatLng(29.980549218884274, 31.152932345867157));
        path.add(new LatLng(29.981874952393586, 31.15452256053686));*/
        /*   path.add(new LatLng(29.983403952745523, 31.15271408110857));
        path.add(new LatLng(29.98525149972834, 31.151796765625473));
        path.add(new LatLng(29.98806370110867, 31.148895621299747));*/path.add(
            LatLng(
                29.9884319181926,
                31.149982586503032
            )
        )
        path.add(LatLng(29.98914627867216, 31.148247867822644))
        path.add(LatLng(29.990676618984686, 31.147549487650398))
        path.add(LatLng(29.993810113180338, 31.145635731518272))
        path.add(LatLng(29.998298930429115, 31.143226772546768))
        path.add(LatLng(29.99978818913346, 31.14007785916328))
        val builder = LatLngBounds.Builder()
        for (i in path.indices) {
            builder.include(path[i])
        }
        builder.include(path[0])
        builder.include(path[path.size - 1])
        val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(10f)
        mMap!!.addPolyline(opts)
        mMap!!.addMarker(
            MarkerOptions().position(path[path.size - 1])
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 120))
    }

    var zoomUpdated = false
    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (application != null) {
                    currentLocation =
                        LocationObject(LatLng(location.latitude, location.longitude), "")
                    mCurrentRide!!.current = currentLocation
                    val latLng = LatLng(location.latitude, location.longitude)
                    if (!zoomUpdated) {
                        val zoomLevel = 17.0f //This goes up to 21
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel))
                        zoomUpdated = true
                    }
                    if (!getDriversAroundStarted) driversAround
                }
            }
        }
    }

    /**
     * This function returns the name of location given the coordinates
     * of said location
     */
    private fun fetchLocationName() {
        if (pickupLocation == null) {
            return
        }
        try {
            val geo = Geocoder(this.applicationContext, Locale.getDefault())
            val addresses = geo.getFromLocation(
                currentLocation!!.coordinates.latitude, currentLocation!!.coordinates.longitude, 1
            )
            if (addresses.isEmpty()) {
                autocompleteFragmentFrom!!.setText(R.string.waiting_for_location)
            } else {
                addresses.size
                if (addresses[0].thoroughfare == null) {
                    pickupLocation!!.name = addresses[0].locality
                } else if (addresses[0].locality == null) {
                    pickupLocation!!.name = "Unknown Location"
                } else {
                    pickupLocation!!.name = addresses[0].locality + ", " + addresses[0].thoroughfare
                }
                autocompleteFragmentFrom!!.setText(pickupLocation!!.name)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Get permissions for our app if they didn't previously exist.
     * requestCode -> the number assigned to the request that we've made.
     * Each request has it's own unique request code.
     */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) &&
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CALL_PHONE
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("give permission")
                    .setMessage("give permission message")
                    .setPositiveButton("OK") { dialogInterface: DialogInterface?, i: Int ->
                        ActivityCompat.requestPermissions(
                            this@CustomerMapActivity, arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CALL_PHONE
                            ), 1
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this@CustomerMapActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE
                    ),
                    1
                )
            }
        }
    }

    fun parseJson(jObject: JSONObject): ArrayList<Double>? {
        val routes: List<List<HashMap<String, String>>> = ArrayList()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        var jSteps: JSONArray
        var jDistance: JSONObject? = null
        var jDuration: JSONObject? = null
        var totalDistance: Long = 0
        var totalSeconds = 0
        try {
            jRoutes = jObject.getJSONArray("routes")

            /* Traversing all routes */for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")

                /* Traversing all legs */for (j in 0 until jLegs.length()) {
                    jDistance = (jLegs[j] as JSONObject).getJSONObject("distance")
                    totalDistance = totalDistance + jDistance.getString("value").toLong()
                    /** Getting duration from the json data  */
                    jDuration = (jLegs[j] as JSONObject).getJSONObject("duration")
                    totalSeconds = totalSeconds + jDuration.getString("value").toInt()
                }
            }
            val dist = totalDistance / 1000.0
            Log.d("distance", "Calculated distance:$dist")
            val days = totalSeconds / 86400
            val hours = (totalSeconds - days * 86400) / 3600
            val minutes = (totalSeconds - days * 86400 - hours * 3600) / 60
            val seconds = totalSeconds - days * 86400 - hours * 3600 - minutes * 60
            Log.d("duration", "$days days $hours hours $minutes mins$seconds seconds")
            val list = ArrayList<Double>()
            list.add(dist)
            list.add(totalSeconds.toDouble())
            return list
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mFusedLocationClient!!.requestLocationUpdates(
                        mLocationRequest,
                        mLocationCallback,
                        Looper.myLooper()
                    )
                    mMap!!.isMyLocationEnabled = true
                }
            } else {
                Toast.makeText(application, "Please provide the permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    var getDriversAroundStarted = false
    var markerList: List<Marker> = ArrayList()//  mDriverMarker.setTag(key);
/*        if (currentLocation == null) {
            return;
        }
        getDriversAroundStarted = true;*/
    //     DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child(("driversWorking"));


    //  GeoFire geoFire = new GeoFire(LocationObject.getDriverStartLocation());
/*        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getCoordinates().latitude, currentLocation.getCoordinates().longitude), 10000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (mCurrentRide != null) {
                    if (mCurrentRide.getDriver() != null) {
                        return;
                    }
                }
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag() == null || key == null) {
                        continue;
                    }
                    if (markerIt.getTag().equals(key))
                        return;
                }


                checkDriverLastUpdated(key);
                LatLng driverLocation = new LatLng(location.latitude, location.longitude);


                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)).position(driverLocation).title(key));
                mDriverMarker.setTag(key);

                markerList.add(mDriverMarker);

            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag() == null || key == null) {
                        continue;
                    }
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }

                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag() == null || key == null) {
                        continue;
                    }
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                        return;
                    }
                }

                checkDriverLastUpdated(key);
                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)).position(driverLocation).title(key));
                mDriverMarker.setTag(key);

                markerList.add(mDriverMarker);
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });*/
    /**
     * Displays drivers around the user's current
     * location and updates them in real time.
     */
    private val driversAround: Unit
        private get() {
/*            val mDriverMarker = mMap!!.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                    .position(
                        LatLng(
                            29.990755, 31.151274999999998
                        )
                    )
                    .title(DriverObject.getDriver().name)
            )
            mDriverMarker.tag = "car"*/
            //  mDriverMarker.setTag(key);
/*        if (currentLocation == null) {
            return;
        }
        getDriversAroundStarted = true;*/
            //     DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child(("driversWorking"));


            //  GeoFire geoFire = new GeoFire(LocationObject.getDriverStartLocation());
/*        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(currentLocation.getCoordinates().latitude, currentLocation.getCoordinates().longitude), 10000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (mCurrentRide != null) {
                    if (mCurrentRide.getDriver() != null) {
                        return;
                    }
                }
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag() == null || key == null) {
                        continue;
                    }
                    if (markerIt.getTag().equals(key))
                        return;
                }


                checkDriverLastUpdated(key);
                LatLng driverLocation = new LatLng(location.latitude, location.longitude);


                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)).position(driverLocation).title(key));
                mDriverMarker.setTag(key);

                markerList.add(mDriverMarker);

            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag() == null || key == null) {
                        continue;
                    }
                    if (markerIt.getTag().equals(key)) {
                        markerIt.remove();
                        markerList.remove(markerIt);
                        return;
                    }

                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt : markerList) {
                    if (markerIt.getTag() == null || key == null) {
                        continue;
                    }
                    if (markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                        return;
                    }
                }

                checkDriverLastUpdated(key);
                LatLng driverLocation = new LatLng(location.latitude, location.longitude);

                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car)).position(driverLocation).title(key));
                mDriverMarker.setTag(key);

                markerList.add(mDriverMarker);
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });*/
        }

    /**
     * Checks if driver has not been updated in a while, if it has been more than x time
     * since the driver location was last updated then remove it from the database.
     *
     *
     * // * @param key - id of the driver
     */
    /*    private void checkDriverLastUpdated(String key) {
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child("Drivers")
                .child(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            return;
                        }

                        if (dataSnapshot.child("last_updated").getValue() != null) {
                            long lastUpdated = Long.parseLong(dataSnapshot.child("last_updated").getValue().toString());
                            long currentTimestamp = System.currentTimeMillis();

                            if (currentTimestamp - lastUpdated > 10000) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversWorking");
                                GeoFire geoFire = new GeoFire(ref);
                                geoFire.removeLocation(dataSnapshot.getKey(), (key1, error) -> {
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });
    }*/
    private fun logOut() {
        //  FirebaseAuth.getInstance().signOut();
        //  AppSharedRepository.logOut(this);
        val intent = Intent(this@CustomerMapActivity, LauncherActivity::class.java)
        startActivity(intent)
        finish()
    }/*        String serverKey = getResources().getString(R.string.google_maps_key);
        if (mCurrentRide.getDestination() != null && mCurrentRide.getPickup() != null) {
            GoogleDirection.withServerKey(serverKey)
                    .from(mCurrentRide.getDestination().getCoordinates())
                    .to(mCurrentRide.getPickup().getCoordinates())
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
        }*/

    /**
     * Get Route from pickup to destination, showing the route to the user
     */
    private val routeToMarker: Unit
        private get() {


/*        String serverKey = getResources().getString(R.string.google_maps_key);
        if (mCurrentRide.getDestination() != null && mCurrentRide.getPickup() != null) {
            GoogleDirection.withServerKey(serverKey)
                    .from(mCurrentRide.getDestination().getCoordinates())
                    .to(mCurrentRide.getPickup().getCoordinates())
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);
        }*/
        }
    private val polylines: MutableList<Polyline>? = ArrayList()

    /**
     * Remove route polylines from the map
     */
    private fun erasePolylines() {
        if (polylines == null) {
            return
        }
        for (line in polylines) {
            line.remove()
        }
        polylines.clear()
    }

    /**
     * Show map within the pickup and destination marker,
     * This will make sure everything is displayed to the user
     *
     * @param route - route between pickup and destination
     */
    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    /**
     * Checks if route where fetched successfully, if yes then
     * add them to the map
     *
     * @param direction - direction object to the destination
     * @param rawBody   - data of the route
     */
    var routeData: ArrayList<Double>? = null

    /**
     * Checks if route where fetched successfully, if yes then
     * add them to the map
     *
     * @param direction - direction object to the destination
     * @param rawBody   - data of the route
     */
    override fun onDirectionSuccess(direction: Direction, rawBody: String) {
        if (direction.isOK) {
            val route = direction.routeList[0]
            try {
                val obj = JSONObject(rawBody)
                routeData = parseJson(obj)
                mAdapter!!.setData(routeData)
                mAdapter!!.notifyDataSetChanged()
                Log.d("My App", obj.toString())
            } catch (ignored: Throwable) {
            }
            destinationMarker = mMap!!.addMarker(
                MarkerOptions().position(destinationLocation!!.coordinates).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        generateBitmap(
                            this@CustomerMapActivity,
                            destinationLocation!!.name,
                            route.legList[0].duration.text
                        )
                    )
                )
            )
            val directionPositionList: List<LatLng> = route.legList[0].directionPoint

            //  MapAnimator.getInstance().animateRoute(mMap, directionPositionList);
            setCameraWithCoordinationBounds(route)
        }
    }

    /**
     * Remove route polylines from the map
     */
    private fun MapAnimator() {
        if (polylines == null) {
            return
        }
        for (line in polylines) {
            line.remove()
        }
        polylines.clear()
    }

    override fun onDirectionFailure(t: Throwable) {}

    /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example it's place name and place ID).
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val mLocation: LocationObject
            if (currentLocation == null) {
                Snackbar.make(
                    findViewById(R.id.drawer_layout),
                    "First Activate GPS",
                    Snackbar.LENGTH_LONG
                ).show()
                return
            }
            val place = Autocomplete.getPlaceFromIntent(
                data!!
            )
            mLocation = LocationObject(place.latLng, place.name)
            currentLocation = LocationObject(
                LatLng(
                    currentLocation!!.coordinates.latitude,
                    currentLocation!!.coordinates.longitude
                ), ""
            )
            if (requestCode == 1) {
                mMap!!.clear()
                destinationLocation = mLocation
                destinationMarker = mMap!!.addMarker(
                    MarkerOptions().icon(
                        BitmapDescriptorFactory.fromBitmap(
                            generateBitmap(
                                this@CustomerMapActivity,
                                destinationLocation!!.name,
                                null
                            )
                        )
                    ).position(
                        destinationLocation!!.coordinates
                    )
                )
                mCurrentRide!!.destination = destinationLocation
                autocompleteFragmentTo!!.setText(destinationLocation!!.name)
                if (pickupLocation != null) {
                    pickupMarker = mMap!!.addMarker(
                        MarkerOptions().position(pickupLocation!!.coordinates).icon(
                            BitmapDescriptorFactory.fromBitmap(
                                generateBitmap(
                                    this@CustomerMapActivity,
                                    pickupLocation!!.name,
                                    null
                                )
                            )
                        )
                    )
                    bringBottomSheetDown()
                }
            } else if (requestCode == 2) {
                mMap!!.clear()
                pickupLocation = mLocation
                pickupMarker = mMap!!.addMarker(
                    MarkerOptions().position(pickupLocation!!.coordinates).icon(
                        BitmapDescriptorFactory.fromBitmap(
                            generateBitmap(this@CustomerMapActivity, pickupLocation!!.name, null)
                        )
                    )
                )
                mCurrentRide!!.pickup = pickupLocation
                autocompleteFragmentFrom!!.setText(pickupLocation!!.name)
                if (destinationLocation != null) {
                    destinationMarker = mMap!!.addMarker(
                        MarkerOptions().position(
                            destinationLocation!!.coordinates
                        ).icon(
                            BitmapDescriptorFactory.fromBitmap(
                                generateBitmap(
                                    this@CustomerMapActivity,
                                    destinationLocation!!.name,
                                    null
                                )
                            )
                        )
                    )
                    bringBottomSheetDown()
                }
            }
            currentDrivers
            MapAnimator()
            routeToMarker
            driversAround
            mRequest!!.text = "Booking Trip..." /*getString(R.string.call_uber)*/
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            val status = Autocomplete.getStatusFromIntent(data!!)
            assert(status.statusMessage != null)
            Log.i("PLACE_AUTOCOMPLETE", status.statusMessage?:"")
        } else if (resultCode == RESULT_CANCELED) {
            initPlacesAutocomplete()
        }
        initPlacesAutocomplete()
    }

    private val currentDrivers: Unit
        private get() {
            bringBottomSheetDown()
            MapAnimator()
            routeToMarker
            driversAround
            mRequest!!.text = getString(R.string.call_uber)
        }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (bottomSheetStatus == 2) {
                bottomSheetStatus = 0
                bringBottomSheetDown()
            } else {
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.history) {
            val intent = Intent(this@CustomerMapActivity, HistoryActivity::class.java)
            intent.putExtra("customerOrDriver", "Customers")
            startActivity(intent)
        } else if (id == R.id.settings) {
            val intent = Intent(this@CustomerMapActivity, CustomerSettingsActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.payment) {
            val intent = Intent(this@CustomerMapActivity, PaymentActivity::class.java)
            startActivity(intent)
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        fun generateBitmap(context: Context, location: String?, duration: String?): Bitmap? {
            var bitmap: Bitmap? = null
            val mInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = RelativeLayout(context)
            try {
                mInflater.inflate(R.layout.item_marker, view, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val locationTextView = view.findViewById<View>(R.id.location) as TextView
            val durationTextView = view.findViewById<View>(R.id.duration) as TextView
            locationTextView.text = location
            if (duration != null) {
                durationTextView.text = duration
            } else {
                durationTextView.visibility = View.GONE
            }
            view.layoutParams = ViewGroup.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            bitmap = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val c = Canvas(bitmap)
            view.draw(c)
            return bitmap
        }
    }
}