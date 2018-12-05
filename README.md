# MKR-ANDROID-LOGIN-UIL

#   AndroidManifest.xml
		<uses-permission android:name="android.permission.INTERNET" />
	    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />        

#	Project Level Gradle
		repositories {
			maven {
				url "https://api.bitbucket.org/1.0/repositories/THEMKR/android-lib-uil/raw/releases"
				credentials {
					username 'THEMKR'
					password '<PASSWORD>'
				}
			}
		}

#	APP Level Gradle
		implementation 'com.lory.library:uil:1.0.0'        
        
#   USE