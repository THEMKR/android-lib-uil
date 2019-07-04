# MKR-ANDROID-LOGIN-UIL

#   AndroidManifest.xml
        <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
        <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
        <uses-permission android:name="android.permission.INTERNET"/>
        <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

#	Project Level Gradle
		repositories {
			maven { url 'https://jitpack.io' }
		}

#	APP Level Gradle
        implementation"org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
        implementation 'androidx.core:core-ktx:1.0.2'
        implementation 'com.google.android.material:material:1.0.0-rc01'
        implementation 'androidx.appcompat:appcompat:1.0.0'
        implementation 'androidx.recyclerview:recyclerview:1.0.0'
        implementation 'androidx.legacy:legacy-support-v4:1.0.0'
        implementation 'androidx.browser:browser:1.0.0'
        implementation 'androidx.cardview:cardview:1.0.0'

        implementation 'com.github.THEMKR:android-lib-ui:1.0.0'
        implementation 'com.github.THEMKR:android-lib-asynctask:1.0.0'
        implementation 'com.github.THEMKR:android-lib-sqlite:1.0.0'
        implementation 'com.github.THEMKR:android-lib-storage:1.0.0'
        implementation 'com.google.code.gson:gson:2.8.5'