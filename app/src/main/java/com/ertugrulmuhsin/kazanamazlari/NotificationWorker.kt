package com.ertugrulmuhsin.kazanamazlari

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    /**
     * WorkManager bu işçiyi çalıştırdığında bu fonksiyon tetiklenir.
     * Görevinin başarılı olup olmadığını döndürür.
     */
    override fun doWork(): Result {
        // Bildirim gönderme işlemini başlat.
        sendNotification()
        // Görevin başarıyla tamamlandığını sisteme bildir.
        return Result.success()
    }

    /**
     * Asıl bildirimi oluşturan ve gönderen fonksiyon.
     */
    private fun sendNotification() {
        val channelId = "KAZA_NAMAZI_CHANNEL"
        // Bildirim kanalını oluştur (sadece Android 8 ve üstü için gerekli).
        createNotificationChannel(channelId)

        // Bildirime tıklandığında uygulamanın ana ekranını açacak olan "niyeti" hazırla.
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Veritabanına bağlan ve toplam kaza sayısını al.
        val db = DatabaseHelper(context)
        val kazaCount = db.getKazaCount() // Bu fonksiyonu bir sonraki aşamada ekleyeceğiz.

        // Eğer kılınacak kaza namazı yoksa, bildirim gönderme ve işlemi bitir.
        if (kazaCount == 0) {
            return
        }

        val notificationTitle = "Kaza Namazı Hatırlatması"
        val notificationText = "Unutma, kılınacak $kazaCount adet kaza namazın var."

        // Bildirimin kendisini oluştur: ikon, başlık, metin, tıklama davranışı vb.
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Uygulamanın ana ikonu
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Tıklama niyeti
            .setAutoCancel(true) // Tıklanınca bildirimin otomatik kaybolmasını sağlar.

        // Bildirim gönderme iznimiz var mı diye kontrol et.
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Eğer izin yoksa, bildirim gönderme. İzin isteme işini ana ekranda yapacağız.
            return
        }

        // Oluşturulan bildirimi sisteme göndererek kullanıcıya göster.
        with(NotificationManagerCompat.from(context)) {
            // `1` bu bildirimin benzersiz ID'sidir. Aynı ID ile yeni bir bildirim gönderirseniz, eskisi güncellenir.
            notify(1, builder.build())
        }
    }

    /**
     * Android 8 (API 26) ve üstü için bildirim kanalı oluşturan fonksiyon.
     * Eski sürümlerde bu gerekli değildir.
     */
    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Kaza Namazı Hatırlatmaları"
            val descriptionText = "Kaza namazlarını hatırlatmak için periyodik bildirimler."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Oluşturulan kanalı Android bildirim sistemine kaydet.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
