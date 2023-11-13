package ru.alt.tasksdistribution.ui.map

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.BuildConfig
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import ru.alt.tasksdistribution.databinding.FragmentMapBinding
import ru.alt.tasksdistribution.helpers.Storage
import ru.alt.tasksdistribution.ui.tasks.data.Task
import kotlin.concurrent.thread


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    private lateinit var mapView: MapView

    private val mapPolylineColors = listOf<Int>(
        Color.BLUE,
        Color.GREEN,
        Color.RED,
        Color.MAGENTA
    )

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        mapView = binding.mapView

        mapView = binding.mapView
        mapView.setZoomRounding(false)
        mapView.setMultiTouchControls(true)


        // compass
        CompassOverlay(
            context,
            InternalCompassOrientationProvider(context),
            mapView
        ).apply {
            enableCompass()
            mapView.overlays.add(this)
        }

        // scale bar
        ScaleBarOverlay(mapView).apply {
            setCentred(true)
            setScaleBarOffset(requireContext().resources.displayMetrics.widthPixels / 2, 10)
            mapView.overlays.add(this)
        }

        // get user location

//        MyLocationNewOverlay(
//            GpsMyLocationProvider(context), mapView
//        ).apply {
//            enableMyLocation()
//            mapView.overlays.add(this)
//            runOnFirstFix {
//                try {
//                    val latitude = this.myLocation.latitude
//                    val longitude = this.myLocation.longitude
//                    requireActivity().runOnUiThread {
//                        val mapController: IMapController = mapView.controller
//                        mapController.setZoom(11.5)
//                        val point = GeoPoint(latitude, longitude)
//                        mapController.setCenter(point)
//                    }
//                } catch (exc: Exception) {
//                    Log.e("osm", "Error ${exc.message}")
//                }
//            }
//        }


        // for tests
        val startPosition = GeoPoint(45.035469, 38.975309)
        val mapController: IMapController = mapView.controller
        mapController.setZoom(12.0)
        mapController.setCenter(startPosition)


        // draw paths
        val positionList: List<GeoPoint> = getPositions(startPosition, Storage.taskList)

        positionList.forEach {
            Marker(mapView).apply {
                position = it
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(this)
            }
        }


        for (i in 0 until positionList.size - 1) {
            OSRMRoadManager(context).apply {
                thread {
                    val road = this.getRoad(arrayListOf(positionList[i], positionList[i + 1]))
                    requireActivity().runOnUiThread {
                        val roadOverlay = RoadManager.buildRoadOverlay(road)
                        mapView.overlays.add(roadOverlay)
                        mapView.invalidate()
                    }
                }
            }
        }
        return root
    }

    private fun getPositions(startPosition: GeoPoint, taskList: List<Task>): List<GeoPoint> {
        return arrayListOf(startPosition).apply {
            taskList.forEach { this.add(GeoPoint(it.latitude, it.longitude)) }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}