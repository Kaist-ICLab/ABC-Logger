package old.kaist.iclab.abclogger.collector_old.event

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity
import old.kaist.iclab.abclogger.commons.StringMapConverter

@Entity
data class DeviceEventEntity(
        var eventType: String = "",
        @Convert(converter = StringMapConverter::class, dbType = String::class)
        var extras: Map<String, String> = mapOf()
) : AbstractEntity()