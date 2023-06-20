package old.kaist.iclab.abclogger.collector_old.install

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import old.kaist.iclab.abclogger.core_old.collector.AbstractEntity
import old.kaist.iclab.abclogger.commons.JsonConverter

@Entity
data class InstalledAppEntity(
        @Convert(converter = InstalledAppsConverter::class, dbType = String::class)
        val apps: List<App> = listOf()
) : AbstractEntity() {
    data class App(
            var name: String = "",
            var packageName: String = "",
            var isSystemApp: Boolean = false,
            var isUpdatedSystemApp: Boolean = false,
            var firstInstallTime: Long = Long.MIN_VALUE,
            var lastUpdateTime: Long = Long.MIN_VALUE
    )
}

class InstalledAppsConverter : JsonConverter<List<InstalledAppEntity.App>>(
        adapter = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
                .adapter(Types.newParameterizedType(List::class.java, InstalledAppEntity.App::class.java)),
        default = listOf()
)