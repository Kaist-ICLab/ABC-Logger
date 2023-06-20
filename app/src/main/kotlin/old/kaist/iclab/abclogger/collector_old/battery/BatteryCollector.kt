package old.kaist.iclab.abclogger.collector_old.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import old.kaist.iclab.abclogger.core_old.collector.AbstractCollector
import old.kaist.iclab.abclogger.commons.*
import old.kaist.iclab.abclogger.collector_old.stringifyBatteryStatus
import old.kaist.iclab.abclogger.collector_old.stringifyBatteryHealth
import old.kaist.iclab.abclogger.collector_old.stringifyBatteryPlugType
import old.kaist.iclab.abclogger.core_old.DataRepository
import old.kaist.iclab.abclogger.core_old.collector.Description

class BatteryCollector(
    context: Context,
    qualifiedName: String,
    name: String,
    description: String,
    dataRepository: DataRepository
) : AbstractCollector<BatteryEntity>(
    context,
    qualifiedName,
    name,
    description,
    dataRepository
) {
    override val permissions: List<String> = listOf()

    override val setupIntent: Intent? = null

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_BATTERY_CHANGED) return
            handleBatteryRetrieval(intent)
        }
    }

    override fun isAvailable(): Boolean = true

    override fun getDescription(): Array<Description> = arrayOf()

    override suspend fun onStart() {
        context.safeRegisterReceiver(receiver, IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        })
    }

    override suspend fun onStop() {
        context.safeUnregisterReceiver(receiver)
    }

    override suspend fun count(): Long = dataRepository.count<BatteryEntity>()

    override suspend fun flush(entities: Collection<BatteryEntity>) {
        dataRepository.remove(entities)
        recordsUploaded += entities.size
    }

    override suspend fun list(limit: Long): Collection<BatteryEntity> = dataRepository.find(0, limit)

    private fun handleBatteryRetrieval(intent: Intent) = launch {
        val timestamp = System.currentTimeMillis()
        val manager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val entity = BatteryEntity(
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1),
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1),
                health = stringifyBatteryHealth(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)),
                pluggedType = stringifyBatteryPlugType(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)),
                status = stringifyBatteryStatus(intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)),
                capacity = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY),
                chargeCounter = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER),
                currentAverage = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE),
                currentNow = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW),
                energyCounter = manager.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER),
                technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: ""
        ).apply {
            this.timestamp = timestamp
        }
        put(entity)
    }
}