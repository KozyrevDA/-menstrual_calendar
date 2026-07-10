# ── kotlinx.serialization: сериализаторы @Serializable-классов проекта ──
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keep,includedescriptorclasses class com.kozyrevda.menstrualcalendar.**$$serializer { *; }
-keepclassmembers class com.kozyrevda.menstrualcalendar.** {
    *** Companion;
}
-keepclasseswithmembers class com.kozyrevda.menstrualcalendar.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Ktor / OkHttp: подавить предупреждения об опциональных зависимостях ──
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.slf4j.**

# ── запрет утечки данных в Logcat: вырезать все вызовы Log в release ──
# (медицинские данные пользовательницы не должны попадать в логи)
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}
