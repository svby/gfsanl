@file:JvmName("Gfsanl")

package net.notiocide.gfsanl

import ucar.nc2.NetcdfFile
import java.io.File
import java.net.URI
import java.nio.file.Files
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

private fun parseGrib(string: String) = NetcdfFile.openInMemory(string)

fun parseGrib(file: File) = parseGrib(file.toString())

fun parseGrib(url: URI): NetcdfFile {
    val temp = File(System.getProperty("java.io.tmpdir"), "gfsanl-temp")
    temp.listFiles()?.forEach { it.deleteRecursively() } ?: temp.deleteRecursively()
    temp.mkdirs()

    val downloaded = File(temp, url.path.substringAfterLast("/"))
    Files.copy(url.toURL().openStream(), downloaded.toPath())

    return parseGrib(downloaded)
}
