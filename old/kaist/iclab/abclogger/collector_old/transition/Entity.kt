package old.kaist.iclab.abclogger.collector_old.transition

import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity

@Entity
data class PhysicalActivityTransitionEntity(
        var type: String = "",
        var isEntered: Boolean = false
) : AbstractEntity()