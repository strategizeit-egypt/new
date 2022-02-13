package com.simcoder.uber.History;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.paypal.android.sdk.payments.PayPalService;
import com.simcoder.uber.Objects.CustomerObject;
import com.simcoder.uber.Objects.DriverObject;
import com.simcoder.uber.Objects.RideObject;
import com.simcoder.uber.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays a single previous ride in detail.
 *
 * If you are a customer then it allows you to both rate the driver
 * and pay for the ride.
 */
public class HistorySingleActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionCallback {
    private String rideId;

    private TextView mPickup;
    private TextView mDestination;
    private TextView mPrice;
    private TextView mCar;
    private TextView mDate;
    private TextView userName;
    private TextView userPhone;

    private ImageView userImage;

    private RatingBar mRatingBar;

    private DatabaseReference historyRideInfoDb;

    private GoogleMap mMap;

    LinearLayout mRatingBarContainer;

    RideObject mRide = RideObject.getRide();
    ArrayList<LatLng> path = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);

        path.add(new LatLng(29.990755
                ,31.151274999999998));
       /* path.add(new LatLng(29.979233341903686, 31.153377257287502));
        path.add(new LatLng(29.979877197373916, 31.15271240472794));
        path.add(new LatLng(29.980549218884274, 31.152932345867157));
        path.add(new LatLng(29.981874952393586, 31.15452256053686));*/
     /*   path.add(new LatLng(29.983403952745523, 31.15271408110857));
        path.add(new LatLng(29.98525149972834, 31.151796765625473));
        path.add(new LatLng(29.98806370110867, 31.148895621299747));*/
        path.add(new LatLng(29.9884319181926, 31.149982586503032));
        path.add(new LatLng(29.98914627867216, 31.148247867822644));
        path.add(new LatLng(29.990676618984686, 31.147549487650398));
        path.add(new LatLng(29.993810113180338, 31.145635731518272));
        path.add(new LatLng(29.998298930429115, 31.143226772546768));
        path.add(new LatLng(29.99978818913346, 31.14007785916328));
        polyline = new ArrayList<>();

        rideId = getIntent().getExtras().getString("rideId");

        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mMapFragment != null) {
            mMapFragment.getMapAsync(this);
        }


        mDestination = findViewById(R.id.destination_location);
        mPickup = findViewById(R.id.pickup_location);
        mCar = findViewById(R.id.car);
        mDate = findViewById(R.id.time);
        mPrice = findViewById(R.id.price);


        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        TextView userMail = findViewById(R.id.email);
        userImage = findViewById(R.id.userImage);

        mRatingBar = findViewById(R.id.ratingBar);
        mRatingBarContainer = findViewById(R.id.ratingBar_container);

      //  historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("ride_info").child(rideId);
        getRideInformation();
        setupToolbar();
    }

    /**
     * Sets up toolbar with custom text and a listener
     * to go back to the previous activity
     */
    private void setupToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.your_trips));
        myToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        myToolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Fetches the info on the current ride and populates the design elements
     */
    private void getRideInformation() {
        mRide = RideObject.getRide();
       // if(AppSharedRepository.isDriver(this)){
          //  getUserInformation("Customers", mRide.getCustomer().getId());
            userName.setText(CustomerObject.getCustomer().getName());
            //   }
            //  if(map.get("phone") != null){
            userPhone.setText(CustomerObject.getCustomer().getPhone());
            mRatingBarContainer.setVisibility(View.GONE);
/*    }else

    {
        userName.setText(DriverObject.getDriver().getName());
        //   }
        //  if(map.get("phone") != null){
        userPhone.setText(DriverObject.getDriver().getPhone());
        //     getUserInformation("Drivers", mRide.getDriver().getId());
        displayCustomerRelatedObjects();
    }*/
        mDate.setText(mRide.getDate());
        mPrice.setText(mRide.getPriceString() + " €");
        mDestination.setText(mRide.getDestination().getName());
        mPickup.setText(mRide.getPickup().getName());
        mCar.setText(mRide.getCar());
        mRatingBar.setRating(mRide.getRating());
       // getRouteToMarker();

/*
        historyRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){return;}
                mRide.parseData(dataSnapshot);
                if(mRide.getDriver().getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    getUserInformation("Customers", mRide.getCustomer().getId());
                    mRatingBarContainer.setVisibility(View.GONE);
                }else{
                    getUserInformation("Drivers", mRide.getDriver().getId());
                    displayCustomerRelatedObjects();
                }


                mDate.setText(mRide.getDate());
                mPrice.setText(mRide.getPriceString() + " €");
                mDestination.setText(mRide.getDestination().getName());
                mPickup.setText(mRide.getPickup().getName());
                mCar.setText(mRide.getCar());
                mRatingBar.setRating(mRide.getRating());



                getRouteToMarker();
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });
*/
    }

    /**
     * Displays the elements that are only available to the customer:
     *  - Rating bar
     *  - pay button
     */
    private void displayCustomerRelatedObjects() {
        mRatingBarContainer.setVisibility(View.VISIBLE);
        mRatingBar.setRating(DriverObject.getDriver().getRatingsAvg());
        mRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
         //   historyRideInfoDb.child("rating").setValue(rating);
          //  DatabaseReference mDriverRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(mRide.getDriver().getId()).child("rating");
      //      mDriverRatingDb.child(rideId).setValue(rating);
        });
    }


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }


    /**
     * Fetches other user information and populates the relevant design elements
     * @param otherUserDriverOrCustomer - String "customer" or "driver"
     * @param otherUserId - id of the user whom we want to fetch the info of
     */
    private void getUserInformation(String otherUserDriverOrCustomer, String otherUserId) {
    //    if (map.get("name") != null) {
            userName.setText(CustomerObject.getCustomer().getName());
     //   }
      //  if(map.get("phone") != null){
            userPhone.setText(CustomerObject.getCustomer().getPhone());
   //     }
/*        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserDriverOrCustomer).child(otherUserId);
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(map == null){
                        return;
                    }
                    if (map.get("name") != null) {
                        userName.setText(map.get("name").toString());
                    }
                    if(map.get("phone") != null){
                        userPhone.setText(map.get("phone").toString());
                    }
                    if(map.get("profileImageUrl") != null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).apply(RequestOptions.circleCropTransform()).into(userImage);
                    }
                }

            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        });*/
    }


    /**
     * Get Route from pickup to destination, showing the route to the user
     */
    private void getRouteToMarker() {
        renderRoute();
/*        String serverKey = getResources().getString(R.string.google_maps_key);
        if (mRide.getPickup() != null && mRide.getDestination() != null){
            GoogleDirection.withServerKey(serverKey)
                    .from(mRide.getDestination().getCoordinates())
                    .to(mRide.getPickup().getCoordinates())
                    .transportMode(TransportMode.DRIVING)
                    .execute(this);*//*

            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_finish)).position(mRide.getDestination().getCoordinates()).title("destination"));
            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start)).position(mRide.getPickup().getCoordinates()).title("pickup"));
        }*/
    }
    private void renderRoute() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < path.size(); i++) {
            builder.include(path.get(i));

        }
        builder.include(path.get(0));
        builder.include(path.get(path.size() - 1));

        PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(10);
        mMap.addPolyline(opts);
        mMap.addMarker(
                new MarkerOptions().position(path.get(path.size() - 1))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 120));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));
getRouteToMarker();
    }


    private List<Polyline> polyline;
    /**
     * Show map within the pickup and destination marker,
     * This will make sure everything is displayed to the user
     * @param route - route between pickup and destination
     */
    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    /**
     * Checks if route where fetched successfully, if yes then
     * add them to the map
     * @param direction - Direction object
     * @param rawBody - data of the route
     */
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            Route route = direction.getRouteList().get(0);

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            Polyline polyline = mMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.BLACK));
            this.polyline.add(polyline);
            setCameraWithCoordinationBounds(route);

        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
    }


}
