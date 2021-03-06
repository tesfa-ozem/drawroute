DrawRoute

[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-drawroute-green.svg?style=flat )]( https://android-arsenal.com/details/1/8158 )
[![](https://jitpack.io/v/malikdawar/drawroute.svg)](https://jitpack.io/#malikdawar/drawroute)
![GitHub pull requests](https://img.shields.io/github/issues-pr/malikdawar/drawroute)


DrawRoute wraps Google Directions API (https://developers.google.com/maps/documentation/directions/) using RxJava for Android so developers can download, parse and draw path on the map in very fast and flexible way. 
Note: You need to generate an API key at Google cloud platform also don't forget to enable Directions API. Enjoy!


The library contains two main parts.
 - **RouteAPI**
    is responsible for sending request to **Google's Direction API** and handling the response
 - **DrawerAPI**
    is responsible for drawing the path on the map
    
    
    
read more on [medium](https://medium.com/better-programming/introducing-drawroute-a-kotlin-library-for-drawing-routes-on-google-maps-for-android-5e6cc99d58f6)




How to add (gradle)
===========
If you are using gradle:
Step1: Add it in your root build.gradle at the end of repositories

```xml
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

Step2: Add the dependency
```xml
dependencies {
	implementation 'com.github.malikdawar:drawroute:1.3'
	implementation 'io.reactivex.rxjava3:rxandroid:3.0.0' // add the support for RX
}
```
Otherwise you have to use library directly in your project.

USAGE
===========
First we have to download the path. For this we need to provide two points (start and end) and travel mode.


```Kotlin
	val source = LatLng(31.490127, 74.316971) //starting point (LatLng)
        val destination = LatLng(31.474316, 74.316112) // ending point (LatLng)

        googleMap?.run {
            moveCameraOnMap(latLng = source) // if you want to zoom the map to any point

            //Called the drawRouteOnMap extension to draw the polyline/route on google maps
           drawRouteOnMap(
                getString(R.string.google_map_api_key), //your API key
                source = source, // Source from where you want to draw path
                destination = destination, // destination to where you want to draw path
                context = context!! //Activity context
            )
        }

```

If you want more control

```Kotlin
	fun GoogleMap.drawRouteOnMap(
	    mapsApiKey: String,  // YOur API key
	    context: Context, //App context
	    source: LatLng, //Source, from where you want to draw path
	    destination: LatLng,  //Destination, to where you want to draw path
	    color: Int = context.getColorCompat(R.color.pathColor),  //color, path/route/polygon color, specify the color if you want some other color other then default one
	    markers: Boolean = true, //If you want markers on source and destination, by default it is true
	    boundMarkers: Boolean = true, //If you want to bound the markers(start and end points) in screen with padding, by default it is true 
	    polygonWidth: Int = 13, // route/path width, by default it is 13
	    travelMode: TravelMode = TravelMode.DRIVING //Travel mode, by default it is DRIVING
	)

```

```Kotlin
enum class TravelMode {
    DRIVING, WALKING, BICYCLING, TRANSIT
}

```

And taking all together:

(Kotlin)

```Kotlin

class RouteFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var disposable: Disposable?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map_route, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initialized google maps
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        this.googleMap = p0

        val source = LatLng(31.490127, 74.316971) //starting point (LatLng)
        val destination = LatLng(31.474316, 74.316112) // ending point (LatLng)

        googleMap?.run {
            moveCameraOnMap(latLng = source)

            //Called the drawRouteOnMap extension to draw the polyline/route on google maps
           disposable =  drawRouteOnMap(
                getString(R.string.google_map_api_key),
                source = source,
                destination = destination,
                context = context!!
            )
        }
    }

    override fun onDestroy() {
        disposable?.dispose() // dispose the disposable on destroy to stop using the phone resources in bakground
        super.onDestroy()
    }
}

```

(Java)
```Java

private final OnMapReadyCallback callback = googleMap -> {

        LatLng source = new LatLng(31.490127, 74.316971); //starting point (LatLng)
        LatLng destination = new LatLng(31.474316, 74.316112); // ending point (LatLng)

        //zoom/move cam on map ready
        MapExtensionKt.moveCameraOnMap(googleMap, 16, true, source);

        //draw route on map
       MapExtensionKt.drawRouteOnMap(googleMap,
                getString(R.string.google_map_api_key),
                getContext(),
                source,
                destination,
                getActivity().getColor(R.color.pathColor),
                true, true, 13, TravelMode.DRIVING);
    };


```

Route Estimations
=================

(Kotlin)
```kotlin
// Implement this interface in your Activity/Fragment where you want to get the Estimated time of arrival or the Distance between the points
interface EstimationsCallBack{
    fun routeEstimations(legs: Legs?)
}
```

In your Activity/Fragmant

```kotlin 

class YourFragment : Fragment(), OnMapReadyCallback, EstimationsCallBack {
-
-
-
-
//if you only want the Estimates (Distance & Time of arrival) for ETA we dont need maps only API Key, locations and interface ref are required.
        getTravelEstimations(
            mapsApiKey = getString(R.string.google_map_api_key),
            source = source,
            destination = destination,
            estimationsCallBack = this
        )


//if you only want to draw a route on maps and also need the ETAs then implement the EstimationsCallBack and pass the ref like this
disposable = drawRouteOnMap(
                getString(R.string.google_map_api_key),
                source = source,
                destination = destination,
                context = context!!,
                estimationsCallBack = this@RouteFragment
            )
	
	
 // Only need to Override this method when you need estimated time of arrival or the distance between two locations
    override fun routeEstimations(legs: Legs?) {
        //Estimated time of arrival
        Log.d("estimatedTimeOfArrival", "${legs?.duration?.text}")
        Log.d("estimatedTimeOfArrival", "${legs?.duration?.value}")

        //Google suggested path distance
        Log.d("GoogleSuggestedDistance", "${legs?.distance?.text}")
        Log.d("GoogleSuggestedDistance", "${legs?.distance?.text}")
    }
    
    For more please refer to the RouteFragment in sample app. Enjoy :)
  ```  

Screen Shot
![image](screenshot/map.jpeg)

Developed By
------------
Malik Dawar - malikdawar332@gmail.com

[Mobin Munir](https://github.com/mmobin789)
