package os.dtakac.caffeine.tiles

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import os.dtakac.caffeine.KeepAwakeService

open class KeepAwakeTileService(
        private val intentKey: String
) : TileService() {
    override fun onTileAdded() {
        setInactive()
    }

    override fun onStartListening() {
        setInactive()
    }

    override fun onClick() {
        val intent = Intent(this, KeepAwakeService::class.java)
        intent.putExtra(intentKey, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun setInactive() {
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}