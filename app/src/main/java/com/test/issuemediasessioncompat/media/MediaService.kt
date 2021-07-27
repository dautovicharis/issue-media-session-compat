package com.test.issuemediasessioncompat.media

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.test.issuemediasessioncompat.R

class MediaService : MediaBrowserServiceCompat() {
    companion object {
        private val TAG = MediaService::class.java.simpleName
    }

    private var mediaSession: MediaSessionCompat? = null
    private var packageValidator: PackageValidator? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(this, TAG).also {
            it.isActive = true
        }
        packageValidator = PackageValidator(this@MediaService, R.xml.allowed_media_browser_callers)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return if (packageValidator?.isKnownCaller(clientPackageName, clientUid) == true) {
            BrowserRoot("__ROOT__", null)
        } else {
            BrowserRoot("__EMPTY_ROOT__", null)
        }
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(ArrayList())
    }
}