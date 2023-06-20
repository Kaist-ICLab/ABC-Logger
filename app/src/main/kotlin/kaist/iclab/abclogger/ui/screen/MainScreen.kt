package kaist.iclab.abclogger.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainScreen(
    startCollect: () -> Unit,
    stopCollect: () -> Unit){
    Column(modifier = Modifier.fillMaxSize()){
        Text(text = "Hello, World!")

        Button(onClick = {startCollect()}) {
            Text("START")
        }
        Button(onClick = {stopCollect()}){
            Text("STOP")
        }
    }
}