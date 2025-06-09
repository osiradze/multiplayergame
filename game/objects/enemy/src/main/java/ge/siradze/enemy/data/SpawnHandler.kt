package ge.siradze.enemy.data

import ge.siradze.glcore.EngineGlobals

internal class SpawnHandler(private val enemyNumber: Int) {
    private var counter: Int = 0
    var activeEnemyCount = enemyNumber

    fun update() {
        if(activeEnemyCount == enemyNumber) {
            return
        }
        counter++
        if(counter > EngineGlobals.fps * 0.05f) {
            counter = 0
            activeEnemyCount++
        }
    }
}