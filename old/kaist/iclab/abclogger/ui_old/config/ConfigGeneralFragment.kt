package old.kaist.iclab.abclogger.ui_old.config

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import kaist.iclab.abclogger.R
import old.kaist.iclab.abclogger.structure.config.Config

import kotlinx.coroutines.flow.Flow
import org.koin.androidx.viewmodel.ext.android.stateSharedViewModel


class ConfigGeneralFragment : ConfigFragment() {
    override val config: Flow<Config> by lazy { viewModel.getConfig() }
    override val viewModel: ConfigViewModel by stateSharedViewModel()

    companion object {
        fun pendingIntent(context: Context): PendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main)
            .setDestination(R.id.config)
            .createPendingIntent()
    }
}