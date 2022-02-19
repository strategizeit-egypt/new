package com.simcoder.uber

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*


private fun LatLng.isCoordinatesValid(): Boolean {
    return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
}

fun GoogleMap.animateCameraLocation(latLng: LatLng, zoomRatio: Float = 16f) {
    this.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomRatio))
}
fun GoogleMap.moveCameraLocation(latLng: LatLng, zoomRatio: Float = 16f) {
    this.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomRatio))

}

fun GoogleMap.addLine(context: Context, list :List<Pair<LatLng, Int>>, withMarkers :Boolean){
    val builder: LatLngBounds.Builder = LatLngBounds.Builder()
    list.forEach {
        builder.include(it.first)
    }
    /*      builder.include(path.first())
          builder.include(path.last())*/

    val polylineOptions = PolylineOptions()
    with(polylineOptions) {
        addAll(list.map { it.first })
        color(ContextCompat.getColor(context, R.color.com_facebook_blue))
        width(11f)
        geodesic(true)
        addPolyline(this)
        /*      map.addMarker(
                  MarkerOptions().position( path.last()).title("finish")
                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
              )*/
        moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 120))
    }
    if (withMarkers)
        addMarkers(context, list)

}
fun GoogleMap.addLine(context: Context, list :List<LatLng>){
    if (list.isNotEmpty()){
    val builder: LatLngBounds.Builder = LatLngBounds.Builder()
    list.forEach {
        builder.include(it)
    }
    /*      builder.include(path.first())
          builder.include(path.last())*/

    val polylineOptions = PolylineOptions()
    with(polylineOptions) {
        addAll(list)
        color(ContextCompat.getColor(context, R.color.com_facebook_blue))
        width(11f)
        geodesic(true)
        addPolyline(this)
        /*      map.addMarker(
                  MarkerOptions().position( path.last()).title("finish")
                      .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
              )*/
        moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 120))
    }
    }

}

fun GoogleMap.addMarkers(context: Context, list: List<Pair<LatLng, Int>>){
    list.forEachIndexed { index, item ->
        addMarker(
            MarkerOptions()
                .anchor(0.5f, 1f)
                .position(item.first)
                .icon(bitmapDescriptorFromVector(context,item.second))
        )

    }
}


fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes res: Int
): BitmapDescriptor {
    val background = ContextCompat.getDrawable(context, res)!!
    //  background.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
    background.setBounds(0, 0, 120, 120)
    val bitmap = background.intrinsicHeight.let {
        Bitmap.createBitmap(
            120,
            120,
            Bitmap.Config.ARGB_8888
        )
    }

    val canvas = Canvas(bitmap)
    background.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/*fun GoogleMap.addTerliveMapMarker(
    activity:Activity,
    latLng: LatLng,
    title: String,
    id: Int,
    imageUrl: String
) {
    val marker: View =
        LayoutInflater.from(activity).inflate(
            R.layout.layout_map_marker, null
        )
    val image: CircleImageView = marker.findViewById(R.id.imgMarker)
    Glide.with(activity).asBitmap()
        .load(imageUrl)
        .into(object : SimpleTarget<Bitmap?>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap?>?
            ) {
                image.setImageBitmap(resource)
                //   image.background  = ContextCompat.getDrawable(this@StoresMapActivity , R.drawable.bg_circular_orang_stork)
                val bitmap = createDrawableFromView(activity, image)
                addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .icon(
                            BitmapDescriptorFactory
                                .fromBitmap(bitmap)
                        )
                    //  .icon(bitmapDescriptorFromVector(this, R.drawable.ic_map_marker))

                ).apply { tag = id }

            }
        })
}*/

// Convert a view to bitmap
fun createDrawableFromView(
    context: Context,
    view: View
): Bitmap {
/*        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)*/
    view.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    view.measure(view.measuredWidth, view.measuredWidth)
    view.layout(0, 0, 160, 160)
    view.buildDrawingCache()
    val bitmap = Bitmap.createBitmap(
        160,
        160,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}


