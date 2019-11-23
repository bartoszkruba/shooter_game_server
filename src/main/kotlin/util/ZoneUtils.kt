package util

import settings.ZONE_SIZE

class ZoneUtils {
    companion object {
        fun getZonesForBounds(bounds: dynamic): ArrayList<String> {
            val zones = ArrayList<String>()

            for (i in (bounds.bounds.min.x / ZONE_SIZE).rangeTo((bounds.bounds.max.x / ZONE_SIZE))) {
                for (j in (bounds.bounds.min.y / ZONE_SIZE).rangeTo((bounds.bounds.max.y / ZONE_SIZE))) {
                    zones.add("_${i}_${j}")
                }
            }

            return zones
        }
    }
}
