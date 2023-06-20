package old.kaist.iclab.abclogger.ui_old.survey.response

import android.os.Parcel
import android.os.Parcelable
import old.kaist.iclab.abclogger.collector_old.survey.InternalResponseEntity

data class CachedResponse(
    val id: Long,
    val triggerTime: Long,
    val reactionTime: Long,
    val title: String?,
    val message: String?,
    val responses: List<InternalResponseEntity>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(InternalResponseEntity) ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(triggerTime)
        parcel.writeLong(reactionTime)
        parcel.writeString(title)
        parcel.writeString(message)
        parcel.writeTypedList(responses)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CachedResponse> {
        override fun createFromParcel(parcel: Parcel): CachedResponse {
            return CachedResponse(parcel)
        }

        override fun newArray(size: Int): Array<CachedResponse?> {
            return arrayOfNulls(size)
        }
    }
}