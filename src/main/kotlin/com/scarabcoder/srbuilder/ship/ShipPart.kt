package com.scarabcoder.srbuilder.ship

import com.boydti.fawe.`object`.schematic.Schematic
import java.util.*

interface ShipPart {

    val id: Int
    val size: ShipSize
    var schematic: Schematic
    val ownerID: UUID
    val name: String?

}