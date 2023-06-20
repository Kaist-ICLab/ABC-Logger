package kaist.iclab.abclogger.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.iclab.abclogger.ui.theme.ABCLoggerTheme
import kaist.iclab.abclogger.domain.Permission


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Permission.hasAppUsageAccess(applicationContext)){
            initView()
        }else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        if(Permission.hasAppUsageAccess(applicationContext)){
            initView()
        }else{
            Toast.makeText(applicationContext, "Without App Usage Access, it is impossible", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initView(){
        setContent {
            ABCLoggerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ABCNavigation()
                }
            }
        }
    }
}


