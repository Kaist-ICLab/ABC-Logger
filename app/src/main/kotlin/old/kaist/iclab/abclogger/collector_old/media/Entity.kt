package old.kaist.iclab.abclogger.collector_old.media

import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity

@Entity
data class MediaEntity(
        var mimeType: String = ""
) : AbstractEntity()