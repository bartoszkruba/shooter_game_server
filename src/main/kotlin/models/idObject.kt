package models

import util.ShortId

abstract class idObject {
    val id = ShortId.generate() as String
}