package com.scarabcoder.srbuilder.ship

enum class ShipSize(readableName: String, val size: Pair<Int, Int>) {

    SMALL("Small", Pair(15, 15)), MEDIUM("Medium", Pair(30,30)), LARGE("Large", Pair(60,60));

}