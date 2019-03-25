@file:JvmName("Gfsanl")

package net.notiocide.gfsanl

import java.net.URI
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.abs

fun locateGrib(date: LocalDate, hour: Int, precision: Int = 25): URI {
    if (abs(ChronoUnit.DAYS.between(LocalDate.now(ZoneOffset.UTC), date)) <= 7) {
        return URI.create(
            buildString {
                append("https://nomads.ncep.noaa.gov/pub/data/nccf/com/gfs/prod/")

                append("gfs.")
                append(date.year.toString().padStart(4, '0'))
                append(date.monthValue.toString().padStart(2, '0'))
                append(date.dayOfMonth.toString().padStart(2, '0'))
                append(hour.toString().padStart(2, '0'))
                append("/")

                append("gfs.")
                append("t")
                append(hour.toString().padStart(2, '0'))
                append("z.pgrb2.0p")
                append(precision)
                append(".anl")
            }
        )
    } else {
        return URI.create(
            buildString {
                append("https://nomads.ncdc.noaa.gov/data/gfsanl/")

                append(date.year.toString().padStart(4, '0'))
                append(date.monthValue.toString().padStart(2, '0'))
                append("/")

                append(date.year.toString().padStart(4, '0'))
                append(date.monthValue.toString().padStart(2, '0'))
                append(date.dayOfMonth.toString().padStart(2, '0'))
                append("/")

                fun appendArchived(version: Int, extension: String) {
                    append("gfsanl_")
                    append(version)
                    append("_")
                    append(date.year.toString().padStart(4, '0'))
                    append(date.monthValue.toString().padStart(2, '0'))
                    append(date.dayOfMonth.toString().padStart(2, '0'))
                    append("_")
                    append(hour.toString().padStart(2, '0'))
                    append("00_000.")
                    append(extension)
                }

                if (date.year >= 2007) appendArchived(4, "grb2")
                else appendArchived(3, "grb")
            }
        )
    }
}
