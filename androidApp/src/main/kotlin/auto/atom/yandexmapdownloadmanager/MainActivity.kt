package auto.atom.yandexmapdownloadmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yandex.mapkit.MapKitFactory
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.setLocale("ru_RU")
        MapKitFactory.initialize(this)
        MapKitFactory.getInstance().onStart()

        val viewModel = AndroidViewModel()
        viewModel.loadRegions()

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

