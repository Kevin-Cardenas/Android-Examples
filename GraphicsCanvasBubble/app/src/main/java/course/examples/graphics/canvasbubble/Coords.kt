package course.examples.graphics.canvasbubble

internal class Coords(val mX: Float, val mY: Float) {

    val coords: Coords
        @Synchronized get() = Coords(mX, mY)

    @Synchronized
    fun move(dxdy: Coords): Coords {
        return Coords(mX + dxdy.mX, mY + dxdy.mY)
    }

    override fun toString(): String {
        return "($mX,$mY)"
    }
}
