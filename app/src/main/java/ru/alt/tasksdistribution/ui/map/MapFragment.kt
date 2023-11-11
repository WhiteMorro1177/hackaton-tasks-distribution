package ru.alt.tasksdistribution.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.alt.tasksdistribution.R
import ru.alt.tasksdistribution.databinding.FragmentMapBinding
import ru.alt.tasksdistribution.ui.tasks.data.Task
import ru.alt.tasksdistribution.ui.tasks.data.TaskStatus
import kotlin.concurrent.thread


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    private lateinit var mapView: MapView

    private lateinit var mapViewModel: MapViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapViewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]

        mapView = binding.mapView
        mapView.setZoomRounding(false)
        mapView.setMultiTouchControls(true)

        // for tests
        /*MyLocationNewOverlay(
            GpsMyLocationProvider(context), mapView
        ).apply {
            enableMyLocation()
            mapView.overlays.add(this)
            runOnFirstFix {
                try {
                    val mapController: IMapController = mapView.controller
                    mapController.setZoom(1.0)
                    val point = GeoPoint(45.035829, 38.975504)
                    mapController.setCenter(point)
                } catch (exc: Exception) {
                    Log.e("osm", "Error ${exc.message}")
                }
            }
        }*/

        MyLocationNewOverlay(
            GpsMyLocationProvider(context), mapView
        ).apply {
            enableMyLocation()
            mapView.overlays.add(this)
            runOnFirstFix {
                try {
                    val latitude = this.myLocation.latitude
                    val longitude = this.myLocation.longitude
                    requireActivity().runOnUiThread {
                        val mapController: IMapController = mapView.controller
                        mapController.setZoom(1.0)
                        val point = GeoPoint(latitude, longitude)
                        mapController.setCenter(point)
                    }
                } catch (exc: Exception) {
                    Log.e("osm", "Error ${exc.message}")
                }
            }
        }

        CompassOverlay(
            context,
            InternalCompassOrientationProvider(context),
            mapView
        ).apply {
            enableCompass()
            mapView.overlays.add(this)
        }

        ScaleBarOverlay(mapView).apply {
            setCentred(true)
            setScaleBarOffset(requireContext().resources.displayMetrics.widthPixels / 2, 10)
            mapView.overlays.add(this)
        }


        var taskList = listOf<Task>()

        mapViewModel.taskList.observe(requireActivity()) {
            taskList = it
        }

        for (task in taskList) {
            if (task.status != TaskStatus.DONE) {
                OSRMRoadManager(context).apply {
                    thread {
                        val road = this.getRoad(arrayListOf(GeoPoint(mapView.mapCenter), GeoPoint(task.latitude, task.longitude)))
                        requireActivity().runOnUiThread {
                            val roadOverlay = RoadManager.buildRoadOverlay(road)
                            mapView.overlays.add(roadOverlay)
                            mapView.invalidate()
                        }
                    }
                }
            }
        }
        return root
    }

    /*


        var taskList = arrayListOf<Task>()

        mapViewModel.taskList.observe(requireActivity()) {
            val requestPoints = arrayListOf<RequestPoint>()

            for (task in it) {
                if (task.status != TaskStatus.DONE) {

                    val taskPoint = Point(task.latitude, task.longitude)

                    // set route points
                    requestPoints.add(RequestPoint(
                        taskPoint,
                        RequestPointType.WAYPOINT,
                        task.priorityName,
                        task.taskId.toString()
                    ))

                    // create marker
                    mapView.map.mapObjects.addPlacemark(taskPoint).apply {
                        addTapListener { _, _ ->
                            // onPlacemarkTap handler
                            Toast.makeText(context, task.taskId.toString(), Toast.LENGTH_SHORT).show()
                            true
                        }
                    }
                }


                // create server request
                drivingSession = drivingRouter!!
                    .requestRoutes(requestPoints, drivingOptions, vehicleOptions, this@MapFragment)

                requestPoints.clear()
            }
        }
    }*/

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}