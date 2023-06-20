package kaist.iclab.abclogger

import android.app.Application
import android.util.Log
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level


class ABCApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d(javaClass.name, "onCreate()")


        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@ABCApplication)
//            modules(listOf(collectorModules, repositoryModules, viewModelModules))
        }

//        GlobalScope.launch {
//            if (BuildConfig.GENERATE_DUMMY_ENTITY) {
//                /**
//                 * TODO: GENERATE DUMMY ENTITIES
//                 */
//            }
//        }

//        CollectorRepository.restart(applicationContext, System.currentTimeMillis())
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(javaClass.name, "onTerminate()")
    }
}
