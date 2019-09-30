# MKR-ANDROID-LOGIN-UIL

#   AndroidManifest.xml
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

#	Project Level Gradle
		repositories {
			maven { url "https://api.bitbucket.org/2.0/repositories/THEMKR/android-libs/src/releases" }
		}

#	APP Level Gradle

        implementation 'com.lory.library:uil:1.0.9'

        implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
        implementation 'androidx.cardview:cardview:1.0.0'
        implementation 'com.lory.library:ui:1.0.0'
        implementation 'com.lory.library:storage:1.0.1'
        implementation 'com.google.code.gson:gson:2.8.5'
