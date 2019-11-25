package models

import util.ShortId

abstract class IdObject {
    val id = ShortId.generate() as String
}