package com.mentalmachines.droidcon_boston.data

import com.mentalmachines.droidcon_boston.data.Schedule.ScheduleDetail
import com.mentalmachines.droidcon_boston.data.Schedule.ScheduleRow
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


open class FirebaseDatabase {

    class ScheduleEvent() {

        var primarySpeakerName: String = ""
        var startTime: String = ""
        var name: String = ""

        var speakerNames: HashMap<String, Boolean>? = null
        var speakerNameToPhotoUrl: HashMap<String, String>? = null
        var roomNames: HashMap<String, Boolean>? = null
        var speakerIds: HashMap<String, Boolean>? = null
        var roomIds: HashMap<String, Boolean>? = null
        var description: String? = null
        var photo: String? = null
        var endTime: String? = null
        var trackSortOrder: Int? = 0

        fun toScheduleRow(): ScheduleRow {
            val row = ScheduleRow()
            val startDateTime = ZonedDateTime.parse(startTime).withZoneSameInstant(ZoneId.systemDefault())
            row.utcStartTimeString = startTime

            if (startDateTime != null) {
                val dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                val timeFormat = DateTimeFormatter.ofPattern("h:mm a")
                row.date = dateFormat.format(startDateTime)
                row.startTime = timeFormat.format(startDateTime).toLowerCase()
            }

            val endDateTime = ZonedDateTime.parse(endTime).withZoneSameInstant(ZoneId.systemDefault())
            if (endDateTime != null) {
                val timeFormat = DateTimeFormatter.ofPattern("h:mm a")
                row.endTime = timeFormat.format(endDateTime).toLowerCase()
            }

            row.room = roomNames!!.keys.first()
            row.trackSortOrder = trackSortOrder
            row.primarySpeakerName = primarySpeakerName
            row.speakerNames = speakerNames!!.keys.toList()
            row.speakerCount = speakerNames!!.size
            row.talkDescription = description
            row.talkTitle = name
            row.photo = speakerNameToPhotoUrl!!.get(row.primarySpeakerName)
            return row
        }
    }

    class ScheduleEventDetail {
        val socialProfiles: HashMap<String, String>? = null
        var bio: String? = null
        var title: String? = null
        var org: String? = null
        var name: String? = null

        fun toScheduleDetail(listRow: ScheduleRow): ScheduleDetail {
            val detail = ScheduleDetail(listRow)
            if (socialProfiles == null) {
                detail.facebook = ""
                detail.linkedIn = ""
                detail.twitter = ""
            } else {
                detail.facebook = socialProfiles.get("facebook")
                detail.linkedIn = socialProfiles.get("linkedIn")
                detail.twitter = socialProfiles.get("twitter")
            }
            detail.speakerBio = bio
            return detail
        }
    }

    class FaqEvent() {

        class Answer() {
            var answer: String = ""

            var photoLink: String? = null
            var mapLink: String? = null
            var otherLink: String? = null
        }

        var answers: List<Answer> = ArrayList()
        var question: String = ""
    }
}

