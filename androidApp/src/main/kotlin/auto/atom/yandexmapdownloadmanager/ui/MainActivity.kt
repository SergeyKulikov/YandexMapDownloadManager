package auto.atom.yandexmapdownloadmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import auto.atom.yandexmapdownloadmanager.BuildConfig
import com.yandex.mapkit.MapKitFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY).apply {
            MapKitFactory.setLocale("ru_RU")
            MapKitFactory.initialize(this@MainActivity)
            MapKitFactory.getInstance().onStart()
        }

        // val viewModel = AndroidViewModel()
        // viewModel.loadRegions()



        setContent {
            AndroidApp()
        }
    }

    override fun onDestroy() {
        MapKitFactory.getInstance().onStop()
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    AndroidApp()
}

