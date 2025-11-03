package com.ertugrulmuhsin.kazanamazlari

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Bildirimleri zamanlamak ve iptal etmek için kullanılan yardımcı nesne (singleton).
 */
object NotificationScheduler {

    // WorkManager'daki görevimizi tanımak için kullanılacak benzersiz bir etiket.
    private const val WORK_TAG = "kazaNotificationWork"

    /**
     * Kullanıcının ayarlarını okur ve buna göre periyodik bildirim görevini kurar veya iptal eder.
     * @param context Bu işlemi başlatmak için gereken uygulama context'i.
     */
    fun scheduleNotification(context: Context) {
        // Kullanıcının kaydettiği ayarlara ulaşmak için SharedPreferences'i kullan.
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        // Ayarlardaki "notifications_enabled" anahtarının değerini oku (varsayılan: true).
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        // Ayarlardaki "notification_frequency" anahtarının değerini oku (varsayılan: "daily").
        val frequency = sharedPreferences.getString("notification_frequency", "daily")

        // Arkaplan görevlerini yöneten WorkManager'a ulaş.
        val workManager = WorkManager.getInstance(context)

        // Eğer ayarlarda bildirimler kapatılmışsa VEYA sıklık "asla" olarak seçilmişse...
        if (!notificationsEnabled || frequency == "never") {
            // Daha önce kurulmuş olan tüm bildirim görevlerini etiketini kullanarak iptal et.
            workManager.cancelUniqueWork(WORK_TAG)
            // ve fonksiyondan çık.
            return
        }

        // Seçilen sıklığa göre tekrarlama aralığını (gün cinsinden) belirle.
        val repeatInterval = when (frequency) {
            "daily" -> 1L   // Her gün için 1 gün
            "weekly" -> 7L  // Haftada bir için 7 gün
            "monthly" -> 30L // Ayda bir için 30 gün (yaklaşık)
            else -> 0L      // Geçersiz bir değer varsa 0
        }

        // Eğer aralık 0 ise (geçersiz bir durum), bir şey yapmadan çık.
        if (repeatInterval == 0L) {
            return
        }

        // Periyodik olarak çalışacak olan Work Request'i (görev talebini) oluştur.
        // NotificationWorker sınıfımızı, belirlenen aralıklarla (gün cinsinden) çalıştıracak.
        val periodicWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(repeatInterval, TimeUnit.DAYS)
            .build()

        // Bu görevi, WorkManager'ın sırasına ekle.
        // `enqueueUniquePeriodicWork` metodu, aynı etiketle (`WORK_TAG`) birden fazla görev oluşturulmasını engeller.
        // Eğer zaten bu etiketle bir görev varsa, `REPLACE` politikası sayesinde eskisini iptal edip yenisini kurar.
        workManager.enqueueUniquePeriodicWork(
            WORK_TAG,
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }
}