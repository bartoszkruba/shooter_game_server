package models

import util.ShortId

abstract class BaseObject {
    val id = ShortId.generate() as String
}