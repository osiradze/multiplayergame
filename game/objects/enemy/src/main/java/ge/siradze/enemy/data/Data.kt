package ge.siradze.enemy.data

import ge.siradze.glcore.EngineGlobals

internal class Data(private val enemyNumber: Int) {
    private var counter: Int = 0
    var activeEnemyCount = 0

    fun update() {
        if(activeEnemyCount == enemyNumber){
            return
        }
        counter++
        if(counter > EngineGlobals.fps) {
            counter = 0
            activeEnemyCount++
        }
    }
}