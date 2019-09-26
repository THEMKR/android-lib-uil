package com.lory.library.uil.utils

class Constants {
    companion object {
        const val CHANNEL_ID = "com.mkrworld.sketch"
        const val DEFAULT_ALBUM_ITEM_LOAD_SIZE = 0.25F
        const val DEFAULT_PIC_ITEM_LOAD_SIZE = 0.15F
        const val MAX_THREAD_COUNT: Int = 3
    }

    /**
     * STORAGE LOCATION TYPE
     * [<OL><LI>INTERNAL : 1</LI><LI>EXTERNAL : 2</LI><LI>ASSSETS : 3</LI><LI>URL : 4</LI></OL>]
     */
    enum class STORAGE_TYPE {
        INTERNAL(1),
        EXTERNAL(2),
        ASSSETS(3),
        URL(4);

        val value: Int

        constructor(value: Int) {
            this.value = value
        }
    }

    /**
     * STORAGE LOCATION TYPE
     * [<OL><LI>NAN : 0</LI><LI>LANDSCAPE_90 : 90</LI><LI>REVERSED : 180</LI><LI>LANDSCAPE_180 : 270</LI></OL>]
     */
    enum class ORIENTATION {
        NAN(0),
        LANDSCAPE_90(90),
        REVERSED(180),
        LANDSCAPE_180(270);

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