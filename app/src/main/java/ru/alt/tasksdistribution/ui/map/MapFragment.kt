package ru.alt.tasksdistribution.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import ru.alt.tasksdistribution.databinding.FragmentMapBinding

class MapFragment : Fragment(), DrivingSession.DrivingRouteListener {

    private var _binding: FragmentMapBinding? = null

    private lateinit var mapView: MapView
    private var routeStartPosition: Point? = null

    private var mapObjects: MapObjectCollection? = null
    private var drivingRouter: DrivingRouter? = null
    private var drivingSession: DrivingSession? = null

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapKitFactory.setApiKey("fa5b23ab-d690-4a12-a49f-d546d6aefc56")

        MapKitFactory.initialize(context)

        // val mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView

        // create object for driver route
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        mapObjects = mapView.map.mapObjects.addCollection()

        // get location
        locationManager = MapKitFactory.getInstance().createLocationManager()
        locationListener = object : LocationListener {
            override fun onLocationUpdated(location: Location) {
                if (routeStartPosition == null) {
                    routeStartPosition = location.position

                    val screenCenter = Point(
                        (routeStartPosition!!.latitude + ROUTE_END_LOCATION.latitude) / 2,
                        (routeStartPosition!!.longitude + ROUTE_END_LOCATION.longitude) / 2
                    )
                    mapView.map.move(CameraPosition(screenCenter, 10.0f, 0.0f, 0.0f))
                    submitRequest()
                }
            }

            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {}
        }



        /*val textView: TextView = binding.textGallery
        mapViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/
        return root
    }

    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()

        // alternative routes count
        drivingOptions.routesCount = 4
        val requestPoints: ArrayList<RequestPoint> = ArrayList()

        // set route points
        requestPoints.apply {
            add(RequestPoint(routeStartPosition!!, RequestPointType.WAYPOINT, null))
            add(RequestPoint(ROUTE_END_LOCATION, RequestPointType.WAYPOINT, null))
        }
        // create server request
        drivingSession = drivingRouter!!.requestRoutes(
            requestPoints, drivingOptions,
            vehicleOptions, this
        )

        // create marker
        mapView.map.mapObjects.addPlacemark(
            ROUTE_END_LOCATION
        ).apply {
            addTapListener { mapObject, point ->
                Toast.makeText(context, endLocationDescription, Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()

        locationManager.subscribeForLocationUpdates(
            0.0,
            1000,
            1.0,
            false,
            FilteringMode.OFF,
            locationListener
        )
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDrivingRoutes(list: MutableList<DrivingRoute>) {
        var color: Int
        for (i in 0 until list.size) {
            // configure colors for routes
            color = COLORS[i]

            // add route to the map
            mapObjects!!.addPolyline(list[i].geometry).setStrokeColor(color)
        }
    }

    override fun onDrivingRoutesError(error: Error) {
        var errorMessage = "Unknown error"
        if (error is RemoteError) {
            errorMessage = "Remote error"
        } else if (error is NetworkError) {
            errorMessage = "Network error"
        }
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}