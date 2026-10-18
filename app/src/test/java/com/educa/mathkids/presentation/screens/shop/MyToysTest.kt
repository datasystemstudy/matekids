package com.educa.mathkids.presentation.screens.shop

import com.educa.mathkids.core.shop.CoinShopManager
import com.educa.mathkids.presentation.localization.EnglishStrings
import com.educa.mathkids.presentation.localization.SpanishStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MyToysTest {

    @Test
    fun `test toys catalog contains 20 unique toys with valid prices and details`() {
        val catalog = CoinShopManager.TOYS_CATALOG
        assertEquals(20, catalog.size)

        val uniqueIds = catalog.map { it.id }.toSet()
        assertEquals(20, uniqueIds.size)

        catalog.forEach { toy ->
            assertTrue("Price should be between 20 and 300", toy.price in 20..300)
            assertTrue("Name should not be empty", toy.name.isNotBlank())
            assertTrue("Emoji should not be empty", toy.emoji.isNotBlank())
            assertTrue("Description should not be empty", toy.description.isNotBlank())
            assertNotNull("Category should not be null", toy.category)
        }
    }

    @Test
    fun `test filtering my toys only returns purchased toys`() {
        val catalog = CoinShopManager.TOYS_CATALOG
        val purchasedIds = setOf("toy_pelota", "toy_robot", "toy_cohete")

        val myToys = catalog.filter { purchasedIds.contains(it.id) }
        assertEquals(3, myToys.size)
        assertTrue(myToys.any { it.id == "toy_pelota" })
        assertTrue(myToys.any { it.id == "toy_robot" })
        assertTrue(myToys.any { it.id == "toy_cohete" })
    }

    @Test
    fun `test string localization includes my toys strings in spanish and english`() {
        val es = SpanishStrings
        val en = EnglishStrings

        assertEquals("MIS JUGUETES", es.btnMyToys)
        assertEquals("MY TOYS", en.btnMyToys)
        assertEquals("JUEGOS CON MIS JUGUETES", es.toyGamesTitle)
        assertEquals("PLAY WITH MY TOYS", en.toyGamesTitle)
        assertTrue(es.toyMemoryTitle.isNotBlank())
        assertTrue(es.toyCatcherTitle.isNotBlank())
        assertTrue(es.toyRunnerTitle.isNotBlank())
        assertTrue(es.myToysSubtitle.isNotBlank())
        assertTrue(en.myToysSubtitle.isNotBlank())
        assertTrue(es.noToysYetPrompt.isNotBlank())
        assertTrue(en.noToysYetPrompt.isNotBlank())
    }

    @Test
    fun `test toy games screen route creation`() {
        val routeHub = com.educa.mathkids.presentation.navigation.Screen.ToyGames.createRoute("HUB", "")
        assertEquals("toy_games?initialGame=HUB&toyId=", routeHub)

        val routeRunner = com.educa.mathkids.presentation.navigation.Screen.ToyGames.createRoute("RUNNER", "toy_robot")
        assertEquals("toy_games?initialGame=RUNNER&toyId=toy_robot", routeRunner)
    }
}
