package kaist.iclab.abclogger.collector

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.PowerManager
import android.provider.Telephony
import android.telephony.TelephonyManager
import kaist.iclab.abclogger.*
import kaist.iclab.abclogger.base.BaseCollector

class DeviceEventCollector(val context: Context) : BaseCollector {
    private val powerManager: PowerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }

    private val filter = arrayOf(Intent.ACTION_HEADSET_PLUG,
            Intent.ACTION_POWER_CONNECTED,
            Intent.ACTION_POWER_DISCONNECTED,
            Intent.ACTION_SHUTDOWN,
            PowerManager.ACTION_POWER_SAVE_MODE_CHANGED,
            PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED,
            Intent.ACTION_HEADSET_PLUG,
            Intent.ACTION_AIRPLANE_MODE_CHANGED,
            Intent.ACTION_CAMERA_BUTTON,
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_SCREEN_ON,
            Intent.ACTION_SCREEN_OFF,
            AudioManager.RINGER_MODE_CHANGED_ACTION,
            Intent.ACTION_BATTERY_LOW,
            Intent.ACTION_BATTERY_OKAY,
            Intent.ACTION_MEDIA_BUTTON,
            TelephonyManager.ACTION_PHONE_STATE_CHANGED,
            Telephony.Sms.Intents.SMS_DELIVER_ACTION,
            Telephony.Sms.Intents.SMS_RECEIVED_ACTION,
            BluetoothAdapter.ACTION_STATE_CHANGED
    ).let { intents ->
        IntentFilter().apply { intents.forEach { addAction(it) } }
    }

    private val receiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val timestamp = System.currentTimeMillis()

                when (intent?.action) {
                    Intent.ACTION_HEADSET_PLUG -> {
                        val isPlugged = intent.getIntExtra("state", 0) == 1
                        val hasMicrophone = intent.getIntExtra("microphone", 0) == 1

                        if (isPlugged && hasMicrophone) {
                            ABCEvent.HEADSET_PLUG_MICROPHONE
                        } else if (!isPlugged && hasMicrophone) {
                            ABCEvent.HEADSET_UNPLUG_MICROPHONE
                        } else if (isPlugged && !hasMicrophone) {
                            ABCEvent.HEADSET_PLUG
                        } else {
                            ABCEvent.HEADSET_UNPLUG
                        }
                    }
                    Intent.ACTION_POWER_CONNECTED -> ABCEvent.POWER_CONNECTED
                    Intent.ACTION_POWER_DISCONNECTED -> ABCEvent.POWER_DISCONNECTED
                    Intent.ACTION_SHUTDOWN -> ABCEvent.SHUTDOWN
                    PowerManager.ACTION_POWER_SAVE_MODE_CHANGED ->
                        if (powerManager.isPowerSaveMode) ABCEvent.POWER_SAVE_MODE_ACTIVATE
                        else ABCEvent.POWER_SAVE_MODE_DEACTIVATE
                    PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED ->
                        if (powerManager.isDeviceIdleMode) ABCEvent.DEVICE_IDLE_MODE_ACTIVATE
                        else ABCEvent.DEVICE_IDLE_MODE_DEACTIVATE
                    Intent.ACTION_AIRPLANE_MODE_CHANGED ->
                        if (intent.getBooleanExtra("state", false)) ABCEvent.AIRPLANE_MODE_ACTIVATE
                        else ABCEvent.AIRPLANE_MODE_DEACTIVATE
                    Intent.ACTION_CAMERA_BUTTON -> ABCEvent.CAMERA_BUTTON
                    Intent.ACTION_USER_PRESENT -> ABCEvent.USER_PRESENT
                    Intent.ACTION_SCREEN_ON -> ABCEvent.SCREEN_ON
                    Intent.ACTION_SCREEN_OFF -> ABCEvent.SCREEN_OFF
                    AudioManager.RINGER_MODE_CHANGED_ACTION ->
                        when (intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, AudioManager.RINGER_MODE_NORMAL)) {
                            AudioManager.RINGER_MODE_NORMAL -> ABCEvent.RINGER_MODE_NORMAL
                            AudioManager.RINGER_MODE_SILENT -> ABCEvent.RINGER_MODE_SILENT
                            AudioManager.RINGER_MODE_VIBRATE -> ABCEvent.RINGER_MODE_NORMAL
                            else -> null
                        }
                    Intent.ACTION_BATTERY_LOW -> ABCEvent.BATTERY_LOW
                    Intent.ACTION_BATTERY_OKAY -> ABCEvent.BATTERY_OKAY
                    Intent.ACTION_MEDIA_BUTTON -> ABCEvent.MEDIA_BUTTON
                    TelephonyManager.ACTION_PHONE_STATE_CHANGED ->
                        when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                            TelephonyManager.EXTRA_STATE_IDLE -> ABCEvent.PHONE_STATE_IDLE
                            TelephonyManager.EXTRA_STATE_OFFHOOK -> ABCEvent.PHONE_STATE_OFFHOOK
                            TelephonyManager.EXTRA_STATE_RINGING -> ABCEvent.PHONE_STATE_RINGING
                            else -> null
                        }
                    Telephony.Sms.Intents.SMS_DELIVER_ACTION -> ABCEvent.SMS_DELIVERED
                    Telephony.Sms.Intents.SMS_RECEIVED_ACTION -> ABCEvent.SMS_RECEIVED
                    BluetoothAdapter.ACTION_STATE_CHANGED ->
                        if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) == BluetoothAdapter.STATE_OFF) ABCEvent.BLUETOOTH_DEACTIVATE
                        else ABCEvent.BLUETOOTH_ACTIVATE
                    else -> null
                }?.let { event ->
                    ABCEvent.post(timestamp = timestamp, eventType = event)

                    DeviceEventEntity(
                            type = event.replace(BuildConfig.APPLICATION_ID, "")
                    ).fillBaseInfo(
                            timeMillis = timestamp
                    ).run {
                        putEntity(this)
                    }
                }
            }
        }
    }

    override fun start() {
        context.registerReceiver(receiver, filter)
    }

    override fun stop() {
        context.unregisterReceiver(receiver)
    }

    override fun checkAvailability(): Boolean = Utils.checkPermissionAtRuntime(context, requiredPermissions)

    override fun handleActivityResult(resultCode: Int, intent: Intent?) { }

    override val requiredPermissions: List<String>
        get() = listOf(
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.READ_PHONE_STATE,
                BluetoothAdapter.ACTION_STATE_CHANGED
        )

    override val newIntentForSetUp: Intent?
        get() = null

    override val nameRes: Int?
        get() = R.string.data_name_device_event

    override val descriptionRes: Int?
        get() = R.string.data_desc_device_event
}