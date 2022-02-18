package com.simcoder.uber.Driver

/*import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;*/
import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import com.lorentzos.flingswipe.SwipeFlingAdapterView.onFlingListener
import com.ncorti.slidetoact.SlideToActView
import com.ncorti.slidetoact.SlideToActView.OnSlideCompleteListener
import com.simcoder.uber.*
import com.simcoder.uber.Adapters.CardRequestAdapter
import com.simcoder.uber.History.HistoryActivity
import com.simcoder.uber.Login.LauncherActivity
import com.simcoder.uber.Objects.DriverObject
import com.simcoder.uber.Objects.RideObject
import com.simcoder.uber.Payment.PayoutActivity
import com.simcoder.uber.R
import com.simcoder.uber.agora.driver.presentation.viewmodel.DriverHomeViewModel
import timber.log.Timber


/**
 * Main Activity displayed to the driver
 */
class DriverMapActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    OnMapReadyCallback, DirectionCallback {
    var MAX_SEARCH_DISTANCE = 20
    private var mMap: GoogleMap? = null
    var mLastLocation: Location? = null
    var mLocationRequest: LocationRequest? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    var path: ArrayList<LatLng> = ArrayList()
    private var mRideStatus: SlideToActView? = null
    private var mRideStatusTitle: TextView? = null
    private var mWorkingSwitch: Switch? = null
    private var mCustomerInfo: LinearLayout? = null
    private var mBringUpBottomLayout: LinearLayout? = null
    private var mCustomerName: TextView? = null

    //  DatabaseReference mUser;
    var mCurrentRide = RideObject.getRide()
    var pickupMarker: Marker? = null
    var destinationMarker: Marker? = null

    // DriverObject mDriver = new DriverObject();
    var mUsername: TextView? = null
    var mLogout: TextView? = null
    private var busMarker: Marker? = null
    private var isCameraUpdatedBefore = false

    //  private ValueEventListener driveHasEndedRefListener;
    private var cardRequestAdapter: CardRequestAdapter? = null
    var requestList: MutableList<RideObject> = ArrayList()
    var mBottomSheet: View? = null
    var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null
  //  var geoQuery: GeoQuery? = null
    var started = false
    var zoomUpdated = false
   //  val viewModel : DriverHomeViewModel by viewModels()
    private val viewModel by lazy {
       ViewModelProvider(
           this,
           ViewModelProvider.AndroidViewModelFactory.getInstance(application)
       )[DriverHomeViewModel::class.java]
    }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_map_driver)
            val toolbar = findViewById<Toolbar>(R.id.toolbar)


            polylines = ArrayList()
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            navigationView.setNavigationItemSelectedListener(this)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            val mapFragment =
                (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
            mapFragment.getMapAsync(this)

            //    mUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(FirebaseAuth.getInstance().getUid());
            mCustomerInfo = findViewById(R.id.customerInfo)
            mBringUpBottomLayout = findViewById(R.id.bringUpBottomLayout)
            mCustomerName = findViewById(R.id.name)
            mUsername = navigationView.getHeaderView(0).findViewById(R.id.usernameDrawer)
            val mMaps = findViewById<FloatingActionButton>(R.id.openMaps)
            val mCall = findViewById<FloatingActionButton>(R.id.phone)
            val mCancel = findViewById<ImageView>(R.id.cancel)
            mRideStatus = findViewById(R.id.rideStatus)
            mRideStatusTitle = findViewById(R.id.tv_status_title)
            mLogout = findViewById(R.id.logout)
            val vto = mBringUpBottomLayout?.getViewTreeObserver()
            vto?.addOnGlobalLayoutListener { initializeBottomLayout() }
            mWorkingSwitch = findViewById(R.id.workingSwitch)
            mLogout?.setOnClickListener(View.OnClickListener { v: View? -> logOut() })
            mWorkingSwitch?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
/*            if (!mDriver.getActive()) {
                Toast.makeText(DriverMapActivity.this, R.string.not_approved, Toast.LENGTH_LONG).show();
                mWorkingSwitch.setChecked(false);
                return;
            }*/if (isChecked) {
                connectDriver()
            } else {
                disconnectDriver()
            }
            })

            mRideStatus?.onSlideCompleteListener = object : OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    if (mCurrentRide!!.state == 1) mCurrentRide!!.setStatus(2) else mCurrentRide!!.setStatus(
                        3
                    )
                    checkRequestState()
                }
            }
            mMaps.setOnClickListener { view: View? ->
                if (mCurrentRide!!.state == 1) {
                    openMaps(
                        mCurrentRide!!.pickup.coordinates.latitude,
                        mCurrentRide!!.pickup.coordinates.longitude
                    )
                } else {
                    openMaps(
                        mCurrentRide!!.destination.coordinates.latitude,
                        mCurrentRide!!.destination.coordinates.longitude
                    )
                }
            }
            mCall.setOnClickListener { view: View? ->
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
                    val intent = Intent(
                        Intent.ACTION_CALL,
                        Uri.parse("tel:" + mCurrentRide!!.customer.phone)
                    )
                    startActivity(intent)
                } else {
                    Snackbar.make(
                        findViewById(R.id.drawer_layout),
                        getString(R.string.no_phone_call_permissions),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            mCancel.setOnClickListener { v: View? ->
                mCurrentRide!!.cancelRide()
                endRide()
            }
            val mDrawerButton = findViewById<ImageView>(R.id.drawerButton)
            mDrawerButton.setOnClickListener { v: View? ->
                drawer.openDrawer(
                    Gravity.LEFT
                )
            }
            mBringUpBottomLayout = findViewById(R.id.bringUpBottomLayout)
            mBringUpBottomLayout?.setOnClickListener(View.OnClickListener { v: View? ->
                if (mBottomSheetBehavior!!.state != BottomSheetBehavior.STATE_EXPANDED) mBottomSheetBehavior!!.setState(
                    BottomSheetBehavior.STATE_EXPANDED
                ) else mBottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_COLLAPSED)
                if (mCurrentRide == null) {
                    mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                }
            })

            //     getUserData();
            //TODO getAssignedCustomer();
            initializeRequestCardSwipe()


        //    viewModel.loginAndSendLocation(remoteConfig.getString("driver_token"))
            viewModel.toastEvent.observe(this){
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }

        }

        /**
         * Open a maps application to show the driver the route between the pickup point and destination.
         * It tries to first open waze, if it fails it will try to open up maps
         *
         * @param latitude  - destination's latitude
         * @param longitude - destination's longitude
         */
        private fun openMaps(latitude: Double, longitude: Double) {
            try {
                val url = "https://waze.com/ul?ll=$latitude,$longitude&navigate=yes"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longitude")
                )
                startActivity(intent)
            }
        }

        /**
         * Initialize swipe cards and add listeners that will control events fo it:
         * - Left swipe: Dismiss the ride request
         * - right swipe: accept ride request
         */
        private fun initializeRequestCardSwipe() {
            cardRequestAdapter =
                CardRequestAdapter(applicationContext, R.layout.item__card_request, requestList)
            val flingContainer = findViewById<SwipeFlingAdapterView>(R.id.frame)
            flingContainer.adapter = cardRequestAdapter

            //Handling swipe of cards
            flingContainer.setFlingListener(object : onFlingListener {
                override fun removeFirstObjectInAdapter() {
                    requestList.removeAt(0)
                    cardRequestAdapter!!.notifyDataSetChanged()
                }

                override fun onLeftCardExit(dataObject: Any) {
                    ///   RideObject mRide = (RideObject) dataObject;
                    //   requestList.remove(mRide);
                    //  cardRequestAdapter.notifyDataSetChanged();
                }

                override fun onRightCardExit(dataObject: Any) {
                    /*         RideObject mRide = (RideObject) dataObject;

                    if (mRide.getDriver() == null) {

                        try {
                            mCurrentRide = (RideObject) mRide.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        mCurrentRide.confirmDriver();*/
                    //     requestListener();
                    //   }
                }

                override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}
                override fun onScroll(scrollProgressPercent: Float) {}
            })
        }

        /**
         * Listener for the bottom popup. This will control
         * when it is shown and when it isn't according to the actions of the users
         * of pulling on it or just clicking on it.
         */
        private fun initializeBottomLayout() {
            mBottomSheet = findViewById(R.id.bottomSheet)
            mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet!!)
            mBottomSheetBehavior!!.isHideable = true
            mBottomSheetBehavior!!.peekHeight = mBringUpBottomLayout!!.height
            mBottomSheetBehavior!!.setBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (mCurrentRide == null) {
                        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }/*        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId);
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                   // mDriver.parseData(dataSnapshot);

                    mUsername.setText("Driver Name"mDriver.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

/*        FirebaseDatabase.getInstance().getReference("driversWorking").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    connectDriver();
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    disconnectDriver();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        /**
         * Fetches current user's info and populates the design element.
         * Also checks if the user was working before closing the app and, if so,
         * connect the driver and set the radio button to "working"
         */
        private val userData: Unit
        private get() {
            mUsername!!.text = DriverObject.getDriver().name
            connectDriver()
            /*        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
             DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId);
             assignedCustomerRef.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists()) {
                        // mDriver.parseData(dataSnapshot);

                         mUsername.setText("Driver Name"mDriver.getName());
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });*/

            /*        FirebaseDatabase.getInstance().getReference("driversWorking").child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists()) {
                         mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                         connectDriver();
                     } else {
                         mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                         disconnectDriver();
                     }
                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });*/
        }/*
        FirebaseDatabase.getInstance().getReference().child("ride_info").orderByChild("driverId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }

                for(DataSnapshot mData : dataSnapshot.getChildren()){
                    mCurrentRide = new RideObject();
                    mCurrentRide.parseData(mData);

                    if (mCurrentRide.getCancelled() || mCurrentRide.getEnded()) {
                        endRide();
                        return;
                    }

                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
         * current driver was a part of and if that last ride is still ongoing then
         * start all of the relevant variables up, with that ride info.
         */
        private val isRequestInProgress: Unit
        private get() {
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            requestListener()
            /*
             FirebaseDatabase.getInstance().getReference().child("ride_info").orderByChild("driverId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                     if (!dataSnapshot.exists()) {
                         mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                     }

                     for(DataSnapshot mData : dataSnapshot.getChildren()){
                         mCurrentRide = new RideObject();
                         mCurrentRide.parseData(mData);

                         if (mCurrentRide.getCancelled() || mCurrentRide.getEnded()) {
                             endRide();
                             return;
                         }

                         mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
         * Use the mCurrentRide variable to check which is the current state of it and do all
         * The necessary work according to that state.
         */
        private fun checkRequestState() {
            //   mCurrentRide = RideObject.getRide();
            when (mCurrentRide!!.state) {
                1 -> {
                    destinationMarker = mMap!!.addMarker(
                        MarkerOptions().position(mCurrentRide!!.destination.coordinates)
                            .title("Destination")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio_filled))
                    )
                    pickupMarker = mMap!!.addMarker(
                        MarkerOptions().position(mCurrentRide!!.pickup.coordinates).title("Pickup")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_radio))
                    )
                    mRideStatus!!.text = resources.getString(R.string.picked_customer)
                    mRideStatusTitle!!.text = resources.getString(R.string.picked_customer)
                    mRideStatus!!.resetSlider()
                    mCustomerName!!.text = mCurrentRide!!.destination.name
                    assignedCustomerInfo
                    requestList.clear()
                    cardRequestAdapter!!.notifyDataSetChanged()
                }
                2 -> {
                    erasePolylines()
                    //   if (mCurrentRide.getDestination().getCoordinates().latitude != 0.0 && mCurrentRide.getDestination().getCoordinates().longitude != 0.0) {
                    getRouteToMarker(mCurrentRide!!.destination.coordinates)
                    //    }
                    mRideStatusTitle!!.text = resources.getString(R.string.ride_in_progress)
                    mRideStatus!!.text = resources.getString(R.string.drive_complete)
                    mRideStatus!!.resetSlider()
                }
                else -> endRide()
            }
        }/*
        if (mLastLocation == null) {
            return;
        }
*/

        /* DatabaseReference requestLocation = FirebaseDatabase.getInstance().getReference().child("customer_requests");

          GeoFire geoFire = new GeoFire(requestLocation);
          geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), MAX_SEARCH_DISTANCE);
          geoQuery.removeAllListeners();

          geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
              @Override
              public void onKeyEntered(String key, GeoLocation location) {
                  if(!mWorkingSwitch.isChecked()){
                      return;
                  }

                  if (mCurrentRide == null) {
                      for (RideObject mRideIt : requestList) {
                          if (mRideIt.getId().equals(key)) {
                              return;
                          }
                      }

                      getRequestInfo(key);

                  }else{
                      requestList.clear();
                  }
              }

              @Override
              public void onKeyExited(String key) {
              }

              @Override
              public void onKeyMoved(String key, GeoLocation location) {
              }

              @Override
              public void onGeoQueryReady() {
              }

              @Override
              public void onGeoQueryError(DatabaseError error) {
              }
          });*/
        /**
         * Get Closest Rider by getting all the requests available
         * within a radius of MAX_SEARCH_DISTANCE and around the driver current location.
         * If a request is found and the driver is not attributed to a request at the moment
         * then call getRequestInfo(key), key being the id of the request.
         */
        private val requestsAround: Unit
        private get() {
            getRequestInfo("key")
            /*
             if (mLastLocation == null) {
                 return;
             }
     */

            /* DatabaseReference requestLocation = FirebaseDatabase.getInstance().getReference().child("customer_requests");

             GeoFire geoFire = new GeoFire(requestLocation);
             geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), MAX_SEARCH_DISTANCE);
             geoQuery.removeAllListeners();

             geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                 @Override
                 public void onKeyEntered(String key, GeoLocation location) {
                     if(!mWorkingSwitch.isChecked()){
                         return;
                     }

                     if (mCurrentRide == null) {
                         for (RideObject mRideIt : requestList) {
                             if (mRideIt.getId().equals(key)) {
                                 return;
                             }
                         }

                         getRequestInfo(key);

                     }else{
                         requestList.clear();
                     }
                 }

                 @Override
                 public void onKeyExited(String key) {
                 }

                 @Override
                 public void onKeyMoved(String key, GeoLocation location) {
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
         * Get info of a request and if it has not ended or been cancelled then add it to the
         * requestList which will push a card of the request to the driver screen.
         *
         * @param key - id of the request to fetch the info of
         */
        private fun getRequestInfo(key: String) {
            requestList.add(RideObject.getRide())
            /*    requestList.add(RideObject.getRide());
            requestList.add(RideObject.getRide());
            requestList.add(RideObject.getRide());*/cardRequestAdapter!!.notifyDataSetChanged()
            makeSound()
            /*
            FirebaseDatabase.getInstance().getReference().child("ride_info").child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        return;
                    }

                    if (mCurrentRide != null) {
                        return;
                    }


                    RideObject mRide = new RideObject();
                    mRide.parseData(dataSnapshot);


                    if(!mRide.getRequestService().equals(mDriver.getService())){
                        return;
                    }


                    for (RideObject mRideIt : requestList) {
                        if (mRideIt.getId().equals(mRide.getId())) {
                            if (mRide.getCancelled() || mRide.getEnded() || mRide.getDriver() != null) {
                                requestList.remove(mRideIt);
                                cardRequestAdapter.notifyDataSetChanged();
                            }
                            return;
                        }
                    }

                    if (!mRide.getCancelled() && !mRide.getEnded() && mRide.getDriver() == null && mRide.getState() == 0) {
                        requestList.add(mRide);
                        cardRequestAdapter.notifyDataSetChanged();
                        makeSound();

                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("timestamp_last_driver_read", ServerValue.TIMESTAMP);
                        FirebaseDatabase.getInstance().getReference().child("ride_info").child(key).updateChildren(map);
                    }
                }

                @Override
                public void onCancelled(@NotNull DatabaseError databaseError) {
                }
            });
    */
        }

        /**
         * Issues a notification sound to the driver.
         */
        private fun makeSound() {
            val mp = MediaPlayer.create(this, R.raw.driver_notification)
            mp.start()
        }

        /**
         * Listener for the request the driver is currently assigned to.
         */
        private fun requestListener() {
            /*     if (mCurrentRide == null) {
                return;
            }*/
            mCurrentRide = RideObject.getRide()
            checkRequestState()


/*
        driveHasEndedRefListener = mCurrentRide.getRideRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    return;
                }
                mCurrentRide.parseData(dataSnapshot);

                //if drive has ended or been cancelled then call endRide to retrieve all variables to their default state
                if (mCurrentRide.getCancelled() || mCurrentRide.getEnded()) {
                    endRide();
                    return;
                }

                checkRequestState();
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/
        }

        /**
         * Get Route from pickup to destination, showing the route to the user
         * @param destination - LatLng of the location to go to
         */
        private fun getRouteToMarker(destination: LatLng) {
            renderRoute()
            /*        String serverKey = getResources().getString(R.string.google_maps_key);
            if (destination != null && mLastLocation != null) {
                GoogleDirection.withServerKey(serverKey)
                        .from(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                        .to(destination)
                        .transportMode(TransportMode.DRIVING)
                        .execute(this);
            }*/
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
        }// mCurrentRide.getCustomer().parseData(dataSnapshot);
        /*        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(mCurrentRide.getCustomer().getId());
             mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                     if (!dataSnapshot.exists()) {
                         return;
                     }

                     if (mCurrentRide != null) {
                         mCurrentRide.getCustomer().parseData(dataSnapshot);

                         mCustomerName.setText(mCurrentRide.getCustomer().getName());
                     }

                     mCustomerInfo.setVisibility(View.VISIBLE);
                     mBottomSheetBehavior.setHideable(false);
                     mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                 }

                 @Override
                 public void onCancelled(@NotNull DatabaseError databaseError) {
                 }
             });*//*     if (mCurrentRide.getCustomer().getId() == null) {
            return;
        }*/
        /**
         * Fetch assigned customer's info and display it in the Bottom sheet
         */
        private val assignedCustomerInfo: Unit
        private get() {
            /*     if (mCurrentRide.getCustomer().getId() == null) {
                      return;
                  }*/
            if (mCurrentRide != null) {
                // mCurrentRide.getCustomer().parseData(dataSnapshot);
                mCustomerName!!.text = mCurrentRide!!.customer.name
            }
            mCustomerInfo!!.visibility = View.VISIBLE
            mBottomSheetBehavior!!.isHideable = false
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            /*        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(mCurrentRide.getCustomer().getId());
                 mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                         if (!dataSnapshot.exists()) {
                             return;
                         }

                         if (mCurrentRide != null) {
                             mCurrentRide.getCustomer().parseData(dataSnapshot);

                             mCustomerName.setText(mCurrentRide.getCustomer().getName());
                         }

                         mCustomerInfo.setVisibility(View.VISIBLE);
                         mBottomSheetBehavior.setHideable(false);
                         mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
            if (mCurrentRide == null) {
                return
            }

            /*     if (driveHasEndedRefListener != null) {
                mCurrentRide.getRideRef().removeEventListener(driveHasEndedRefListener);
            }*/mRideStatus!!.text = getString(R.string.picked_customer)
            erasePolylines()

            /*String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("customerRequest");
            driverRef.removeValue();

            //Remove the request from the geofire child so that other drivers don't have to check this request in the future
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.removeLocation(mCurrentRide.getId(), (key, error) -> {
            });*/

            // mCurrentRide = null;
            if (pickupMarker != null) {
                pickupMarker!!.remove()
            }
            if (destinationMarker != null) {
                destinationMarker!!.remove()
            }
            mCurrentRide = RideObject.getRide()
            // mBottomSheetBehavior.setHideable(true);
            mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
            mCustomerName!!.text = ""
            mMap!!.clear()
            requestsAround
            // checkRequestState();

            //This will allow the map to re-zoom on the current location
            zoomUpdated = false
        }

        override fun onMapReady(googleMap: GoogleMap) {
            mMap = googleMap
            mMap!!.setOnMapClickListener {
              Timber.d("GGGGG $it")
              viewModel.sendM(it.toString())
            }
            googleMap.setMapStyle(MapStyleOptions(resources.getString(R.string.style_json)))
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
            userData
            requestsAround
            isRequestInProgress
            viewModel.currentLocationUpdates.observe(this) { param ->
                Toast.makeText(this, "CurrentFromDriver $param", Toast.LENGTH_SHORT).show()

                val latLng = LatLng(param.lat, param.lng)
                busMarker?.let {
                    busMarker?.remove()
                  //  val user = AppSharedRepository.getUser()
                    val userLocation = LatLng(27.962065, 34.362704)
                      //  LatLng(user.latitude ?: param.lat, user.longitude ?: param.lng)

                    mMap!!.addLine(
                        this,
                        listOf(
                            Pair(userLocation, R.drawable.ic_person_black_24dp),
                            Pair(latLng, R.drawable.ic_location_on_primary_24dp)
                        ), false
                    )
                    if (!isCameraUpdatedBefore)
                        mMap!!.animateCameraLocation(latLng, 10f)
                }
                Toast.makeText(
                    this,
                    " Bus Marker added , ${param.lat} : ${param.lng}",
                    Toast.LENGTH_LONG
                ).show()

                busMarker = mMap!!.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .anchor(0.5f, 1f)
                        .icon(bitmapDescriptorFromVector(this, R.drawable.ic_arrow))
                ).apply { tag = "bus" }
             //   mMap!!.moveCameraLocation(latLng)
                if (!isCameraUpdatedBefore)
                    mMap!!.moveCameraLocation(latLng)
                isCameraUpdatedBefore = true


            }

        }

        /**
         * Gets location of the current user and in the case of the driver updates
         * the database with the most current location so that customers can get
         * info on the drivers around them.
         */
        var mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (application != null) {
                        /*       currentLocation = new LocationObject(new LatLng(location.getLatitude(), location.getLongitude()), "");
                        mCurrentRide.setCurrent(currentLocation);*/
                        val latLng = LatLng(location.latitude, location.longitude)
                        if (!zoomUpdated) {
                            val zoomLevel = 17.0f //This goes up to 21
                            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel))
                            zoomUpdated = true
                        }
                        /*
                        if (!getDriversAroundStarted)
                            getDriversAround();*/
                    }
                }

                /*     for (Location location : locationResult.getLocations()) {
                    if (getApplicationContext() != null) {

                     */
                /*   String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                        GeoFire geoFireWorking = new GeoFire(refWorking);

                        if (!mWorkingSwitch.isChecked()) {
                            geoFireWorking.removeLocation(userId, (key, error) -> {
                            });
                            return;
                        }
    */
                /*

                  */
                /*      geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()), (key, error) -> {
                        });*/
                /*

                        if (mCurrentRide != null && mLastLocation != null) {
                            mCurrentRide.setRideDistance(mCurrentRide.getRideDistance() + mLastLocation.distanceTo(location) / 1000);
                        }

                        mLastLocation = location;

                        if (!started) {
                            getRequestsAround();
                            started = true;
                        }

                        Map<String, Object> newUserMap = new HashMap<>();
                     //   newUserMap.put("last_updated", ServerValue.TIMESTAMP);
                    //    mUser.updateChildren(newUserMap);

                        if (!zoomUpdated) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                            zoomUpdated = true;
                        }
                    }
                }*/
            }
        }

        /**
         * Get permissions for our app if they didn't previously exist.
         * requestCode: the number assigned to the request that we've made. Each
         * |                request has it's own unique request code.
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
                    ) && ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton(
                            "OK"
                        ) { dialogInterface: DialogInterface?, i: Int ->
                            ActivityCompat.requestPermissions(
                                this@DriverMapActivity,
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ),
                                1
                            )
                        }
                        .create()
                        .show()
                } else {
                    ActivityCompat.requestPermissions(
                        this@DriverMapActivity,
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
                    Toast.makeText(
                        applicationContext,
                        "Please provide the permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        private fun logOut() {
            //   AppSharedRepository.logOut(this);
            val intent = Intent(this@DriverMapActivity, LauncherActivity::class.java)
            startActivity(intent)
            finish()
            //   disconnectDriver();

/*      //  FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(DriverMapActivity.this, LauncherActivity.class);
        startActivity(intent);
        finish();*/
        }

        /**
         * Connects driver, waking up the code that fetches current location
         */
        private fun connectDriver() {
            mWorkingSwitch!!.isChecked = true
            checkLocationPermission()
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
            if (mMap != null) {
                mMap!!.isMyLocationEnabled = true
            }
        }

        /**
         * Disconnects driver, putting to sleep the code that fetches current location
         */
        private fun disconnectDriver() {
            mWorkingSwitch!!.isChecked = false
            if (mFusedLocationClient != null) {
                mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
            }
            /*        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driversAvailable").child(userId);
            ref.removeValue();*/
        }

        private var polylines: MutableList<Polyline>? = null

        /**
         * Remove route polylines from the map
         */
        private fun erasePolylines() {
            for (line in polylines!!) {
                line.remove()
            }
            polylines!!.clear()
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
         * @param direction - Direction object
         * @param rawBody   - data of the route
         */
        override fun onDirectionSuccess(direction: Direction, rawBody: String) {
            if (direction.isOK) {
                val route = direction.routeList[0]
                // todo render map
                val directionPositionList = route.legList[0].directionPoint
                val polyline = mMap!!.addPolyline(
                    DirectionConverter.createPolyline(
                        this,
                        directionPositionList,
                        5,
                        Color.BLACK
                    )
                )
                polylines!!.add(polyline)
                setCameraWithCoordinationBounds(route)
            }
        }

        override fun onDirectionFailure(t: Throwable) {}
        override fun onBackPressed() {
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                super.onBackPressed()
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
                val intent = Intent(this@DriverMapActivity, HistoryActivity::class.java)
                intent.putExtra("customerOrDriver", "Drivers")
                startActivity(intent)
            } else if (id == R.id.settings) {
                val intent = Intent(this@DriverMapActivity, DriverSettingsActivity::class.java)
                startActivity(intent)
            } else if (id == R.id.payout) {
                val intent = Intent(this@DriverMapActivity, PayoutActivity::class.java)
                startActivity(intent)
            } else if (id == R.id.logout) {
                logOut()
            }
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)
            return true
        }
    }
