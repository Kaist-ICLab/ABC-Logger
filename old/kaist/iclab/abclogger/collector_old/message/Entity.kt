package old.kaist.iclab.abclogger.collector_old.message

import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity

@Entity
data class MessageEntity(
        var number: String = "",
        var messageClass: String = "",
        var messageBox: String = "",
        var contactType: String = "",
        var isStarred: Boolean = false,
        var isPinned: Boolean = false
) : AbstractEntity()
