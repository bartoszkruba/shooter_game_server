package util

import settings.ZONE_SIZE

class ZoneUtils {
    companion object {
        fun getZonesForBounds(bounds: dynamic): ArrayList<String> {
            val zones = ArrayList<String>()

            val minX: Int = (bounds.bounds.min.x / ZONE_SIZE) as Int
            val maxX: Int = (bounds.bounds.max.x / ZONE_SIZE) as Int
            val minY: Int = (bounds.bounds.min.y / ZONE_SIZE) as Int
            val maxY: Int = (bounds.bounds.max.y / ZONE_SIZE) as Int

            for (i in (minX / 1)..(maxX / 1)) {
                for (j in (minY / 1)..(maxY / 1)) {
                    zones.add("_${i}_${j}")
                }
            }

            return zones
        }

        fun getZonesForBounds(minX: Int, maxX: Int, minY: Int, maxY: Int): ArrayList<String> {
            val zones = ArrayList<String>()

            for (i in (minX / 1)..(maxX / 1)) {
                for (j in (minY / 1)..(maxY / 1)) {
                    zones.add("_${i}_${j}")
                }
            }
            return zones
        }
    }
}
