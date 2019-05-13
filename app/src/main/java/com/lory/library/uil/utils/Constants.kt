package com.lory.library.uil.utils

class Constants {
    companion object {
        const val CHANNEL_ID = "com.mkrworld.sketch"
        const val MAX_FAILED_COUNT = 3
        const val LIFELINE_INCREMENT = 2
    }

    /**
     * STORAGE LOCATION TYPE
     * [<OL><LI>INTERNAL : 1</LI><LI>EXTERNAL : 2</LI></OL>]
     */
    enum class STORAGE_TYPE {
        INTERNAL(1),
        EXTERNAL(2);

        val value: Int

        constructor(value: Int) {
            this.value = value
        }
    }

    /**
     * FLIP_TYPE TYPE
     * [<OL><LI>HORIZONTAL : 1</LI><LI>VERTICAL : 2</LI><LI>BOTH : 3</LI></OL>]
     */
    enum class FLIP_TYPE {
        NAN(0),
        HORIZONTAL(1),
        VERTICAL(2),
        BOTH(3);

        val value: Int

        constructor(value: Int) {
            this.value = value
        }
    }
}