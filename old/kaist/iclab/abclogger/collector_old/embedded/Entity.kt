package old.kaist.iclab.abclogger.collector_old.embedded

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity
import old.kaist.iclab.abclogger.commons.StringListConverter
import old.kaist.iclab.abclogger.commons.StringMapConverter


@Entity
data class EmbeddedSensorEntity(
        var valueType: String = "",
        @Convert(converter = StringMapConverter::class, dbType = String::class)
        var status: Map<String, String> = mapOf(),
        var valueFormat: String = "",
        var valueUnit: String = "",
        @Convert(converter = StringListConverter::class, dbType = String::class)
        var values: List<String> = listOf()
) : AbstractEntity()
