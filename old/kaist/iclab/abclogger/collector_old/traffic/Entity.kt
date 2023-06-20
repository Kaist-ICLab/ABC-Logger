package old.kaist.iclab.abclogger.collector_old.traffic

import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity

@Entity
data class DataTrafficEntity(
        var fromTime: Long = Long.MIN_VALUE,
        var toTime: Long = Long.MIN_VALUE,
        var rxBytes: Long = Long.MIN_VALUE,
        var txBytes: Long = Long.MIN_VALUE,
        var mobileRxBytes: Long = Long.MIN_VALUE,
        var mobileTxBytes: Long = Long.MIN_VALUE
) : AbstractEntity()
