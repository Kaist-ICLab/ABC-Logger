package old.kaist.iclab.abclogger.collector_old.appusage

import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity

@Entity
data class AppUsageEventEntity(
        var name: String = "",
        var packageName: String = "",
        var type: String = "",
        var isSystemApp: Boolean = false,
        var isUpdatedSystemApp: Boolean = false
) : AbstractEntity()