package pl.arsonproject.koleodistancer.model

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class StationTest {

    private lateinit var firstStation : Station
    private lateinit var secondStation : Station

    fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

    @Before
    fun setUp(){
        firstStation = Station(16725,"Bydgoszcz Bielawy","bydgoszcz-bielawy",53.1315935438625,18.0551940870895,2835,5071)
        secondStation = Station(10645,"Jesionowiec","jesionowiec",53.4460589994395,20.9962427402891,551,5248)
    }

    @Test
    fun correctDistance(){
        Assert.assertEquals( 198.57020395220817,firstStation.calculateDistance(secondStation),0.0)
    }


    @Test
    fun incorrectDistance(){
        Assert.assertNotEquals(200,firstStation.calculateDistance(secondStation))
    }
}