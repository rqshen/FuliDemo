
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

#==================gson==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#==================protobuf======================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}

-keepattributes InnerClasses
-keep class **.R$* {
    <fields>;
}
#==================litepal=======================
-keep class org.litepal.** {
*;
}
-keep class * extends org.litepal.crud.DataSupport {
*;
}