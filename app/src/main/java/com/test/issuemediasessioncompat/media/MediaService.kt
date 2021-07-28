package com.test.issuemediasessioncompat.media

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.test.issuemediasessioncompat.R

class MediaService : MediaBrowserServiceCompat() {

    companion object {
        private val TAG = MediaService::class.java.simpleName
    }

    private lateinit var mediaSession: MediaSessionCompat
    private var packageValidator: PackageValidator? = null

    override fun onCreate() {
        super.onCreate()

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        val pendingItent = PendingIntent.getBroadcast(
            baseContext,
            0, mediaButtonIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val component = getMediaButtonReceiverComponent(baseContext)

        mediaSession = MediaSessionCompat(baseContext, TAG, component, pendingItent).also {
            it.isActive = true
        }

        sessionToken = mediaSession.sessionToken
        packageValidator = PackageValidator(this@MediaService, R.xml.allowed_media_browser_callers)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return if (packageValidator?.isKnownCaller(clientPackageName, clientUid) == true) {
            BrowserRoot("__ROOT__", null)
        } else {
            BrowserRoot("__EMPTY_ROOT__", null)
        }
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(ArrayList())
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
    }

    private fun getMediaButtonReceiverComponent(context: Context): ComponentName? {
        val queryIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        queryIntent.setPackage(context.packageName)
        val pm = context.packageManager
        val resolveInfos = pm.queryBroadcastReceivers(queryIntent, 0)
        if (resolveInfos.size == 1) {
            val resolveInfo = resolveInfos[0]
            return ComponentName(
                resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name
            )
        } else if (resolveInfos.size > 1) {
            Log.w(
                TAG, "More than one BroadcastReceiver that handles "
                        + Intent.ACTION_MEDIA_BUTTON + " was found, returning null."
            )
        }
        return null
    }
}