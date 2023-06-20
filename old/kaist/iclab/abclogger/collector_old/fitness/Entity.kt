package old.kaist.iclab.abclogger.collector_old.fitness

import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity

@Entity
data class FitnessEntity(
        var type: String = "",
        var startTime: Long = 0,
        var endTime: Long = 0,
        var value: String = "",
        var fitnessDeviceModel: String = "",
        var fitnessDeviceManufacturer: String = "",
        var fitnessDeviceType: String = "",
        var dataSourceName: String = "",
        var dataSourcePackageName: String = ""
) : AbstractEntity()
