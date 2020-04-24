package course.examples.graphics.canvasbubblesurfaceview

internal class Coords(val mX: Float, val mY: Float) {

    @Synchronized
    fun move(dxdy: Coords): Coords {
        return Coords(mX + dxdy.mX, mY + dxdy.mY)
    }

    override fun toString(): String {
        return "($mX,$mY)"
    }
}