package old.kaist.iclab.abclogger.collector_old.location

import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity

@Entity
data class LocationEntity(
        var latitude: Double = Double.MIN_VALUE,
        var longitude: Double = Double.MIN_VALUE,
        var altitude: Double = Double.MIN_VALUE,
        var accuracy: Float = Float.MIN_VALUE,
        var speed: Float = Float.MIN_VALUE
) : AbstractEntity()