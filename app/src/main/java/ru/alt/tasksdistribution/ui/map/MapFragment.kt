package ru.alt.tasksdistribution.ui.map

import android.os.Bundle
import android.util.Log
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
import ru.alt.tasksdistribution.ui.tasks.TasksService
import ru.alt.tasksdistribution.ui.tasks.TasksViewModel

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        MapKitFactory.setApiKey("fa5b23ab-d690-4a12-a49f-d546d6aefc56")
        MapKitFactory.initialize(context)

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
                        (routeStartPosition!!.latitude) / 2,
                        (routeStartPosition!!.longitude) / 2
                    )
                    mapView.map.move(CameraPosition(screenCenter, 10.0f, 0.0f, 0.0f))
                    submitRequest()
                }
            }

            override fun onLocationStatusUpdated(locationStatus: LocationStatus) {}
        }
        return root
    }

    private fun submitRequest() {
        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()

        val tasksViewModel: TasksViewModel = ViewModelProvider(this)[TasksViewModel::class.java]

        // alternative routes count
        drivingOptions.routesCount = 1

        val taskList = TasksService(tasksViewModel.userId.value.toString()).getTasks()


            val requestPoints = ArrayList<RequestPoint>()

            for (task in taskList) {
                val taskPoint = Point(task.latitude, task.longitude)

                // set route points
                requestPoints.add(RequestPoint(
                    taskPoint,
                    RequestPointType.WAYPOINT,
                    task.priority.priorityName,
                    task.taskId.toString()
                ))

                // create marker
                mapView.map.mapObjects.addPlacemark(taskPoint).apply {
                    addTapListener { mapObject, point ->
                        // onPlacemarkTap handler
                        Toast.makeText(context, task.taskId.toString(), Toast.LENGTH_SHORT).show()
                        true
                    }
                }
            }

            // create server request
            drivingSession = drivingRouter!!
                .requestRoutes(requestPoints, drivingOptions, vehicleOptions, this@MapFragment)



    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()

        locationManager.subscribeForLocationUpdates(0.0, 1000, 1.0, false, FilteringMode.OFF, locationListener)
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

    override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
        for (route in drivingRoutes) {
            // add route to the map
            mapObjects!!.addPolyline(route.geometry).setStrokeColor(0xA200FF)
        }
    }

    override fun onDrivingRoutesError(error: Error) {
        val errorMessage = when (error) {
            is RemoteError -> "Remote error"
            is NetworkError -> "Network error"
            else -> error.javaClass.name
        }

        Log.d("RoutesError", errorMessage)
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }
}