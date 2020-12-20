package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private val NOTIFICATION_ID = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)
        Timber.plant(Timber.DebugTree())

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentView.customButton.setOnClickListener {
            getUrl()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        // Check to see if the API Level is a API Level 26 as it requires a channel to be created
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download is complete!"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getUrl() {
        val btnId = binding.contentView.rgUrl.checkedRadioButtonId
        val rb = findViewById<RadioButton>(btnId)
        if (rb != null) {
            when (rb.text.toString()) {
                binding.contentView.rbGlide.text -> download(getString(R.string.glide))
                binding.contentView.rbStarterProject.text -> download(getString(R.string.starter))
                binding.contentView.rbRetrofit.text -> download(getString(R.string.retrofit_url))
            }
        }else{
            Toast.makeText(applicationContext, "Select an Option", Toast.LENGTH_SHORT).show()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            notificationManager.sendNotification("Test", applicationContext, "Complete")
        }
    }

    private fun download(url:String) {

        notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
        createChannel(getString(R.string.download_notification_channel_id), getString(R.string.download_notification_channel_name))

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
               // .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        Timber.i(downloadID.toString())
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }


    fun NotificationManager.sendNotification(messageBody: String, appContext:Context, status:String){

        val intent = Intent(appContext, DetailActivity::class.java)
        intent.putExtra("fileName", messageBody)
        intent.putExtra("status", status)
        pendingIntent = PendingIntent.getActivity(appContext, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(appContext, appContext.getString(R.string.download_notification_channel_id))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(appContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .addAction(R.drawable.ic_baseline_play_arrow_24,"View", pendingIntent)

        notify(NOTIFICATION_ID, builder.build())
    }
}
