package os.dtakac.caffeine

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class CaffeineTileService : TileService() {
    override fun onTileAdded() {
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    override fun onClick() {
        val caffeineKeepAwakeIntent = Intent(this, CaffeineKeepAwakeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(caffeineKeepAwakeIntent)
        } else {
            startService(caffeineKeepAwakeIntent)
        }
    }
}