# Report Portal integration for Android Espresso and JUnit5

> **DISCLAIMER**: We use Google Analytics for sending anonymous usage information such as agent's and client's names, and their versions
> after a successful launch start. This information might help us to improve both ReportPortal backend and client sides. It is used by the
> ReportPortal team only and is not supposed for sharing with 3rd parties.

[![Maven Central](https://img.shields.io/maven-central/v/com.epam.reportportal/agent-android-junit5.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.epam.reportportal%22%20AND%20a:%22agent-android-junit5%22)
[![CI Build](https://github.com/reportportal/agent-android/actions/workflows/ci.yml/badge.svg)](https://github.com/reportportal/agent-android/actions/workflows/ci.yml)
[![Join Slack chat!](https://img.shields.io/badge/slack-join-brightgreen.svg)](https://slack.epmrpp.reportportal.io/)
[![stackoverflow](https://img.shields.io/badge/reportportal-stackoverflow-orange.svg?style=flat)](http://stackoverflow.com/questions/tagged/reportportal)
[![Build with Love](https://img.shields.io/badge/build%20with-❤%EF%B8%8F%E2%80%8D-lightgrey.svg)](http://reportportal.io?style=flat)


The latest version: 5.3.0. Please use `Maven Central` link above to get the agent. Minimal supported API version: 26

## Overview: How to Add ReportPortal integration to Your Android Project
Report Portal supports Android Espresso JUnit 5 tests. The integration is built on top of
[android-junit5](https://github.com/mannodermaus/android-junit5) library by 
[Marcel Schnelle](https://github.com/mannodermaus).

To start using Report Portal with Android please do the following steps:

1. [Configuration](#configuration)
   * Add test module
   * Build system configuration
   * Create `reportportal.properties` configuration file
   * JUnit 5 configuration
   * Debug Manifest file
2. Logging configuration
   * [Logback Framework](#logback-framework)
      * Add Logback dependencies
      * Create/update the `logback.xml` file
3. [Running tests](#test-run)
   * Build system commands
4. Examples
   * [Kotlin](#kotlin-examples)
   * [Java](#java-examples)

## Configuration
### Add test module
To start implementation of Android integration tests you need a separate module inside your project.
Open Android Studio and create a Library module:
1. Right-click on Android project view
2. Select `New -> Module`
3. Select `Android Library` and click `Next`
4. Specify your module name, E.G: `:junit5-integration-tests`
5. Specify `Language` and `Bytecode level`
6. Specify `Minimum SDK` - currently it is `API 26: Android 8.0 (Oreo)`
7. Click `Finish`

### Build system configuration
Before you can start test implementation you need to update your `build.gradle` file with the 
following steps. Please notice that library versions change rapidly and you should use the latest 
available versions instead of copy-pasting them from here.
1. Change library plugin `com.android.library` on test plugin `com.android.test`
2. Add `repositories` section with our official repository
3. In the `android` section:
   * Add `targetProjectPath ':your_app_module_name'`
   * Add `packagingOptions` section because Android does not allow duplicate files:
   ```groovy
   packagingOptions {
       exclude "META-INF/LICENSE*"
   }
   ```
   * Add `testOptions` which disables animation on emulators to avoid test failures:
   ```groovy
   testOptions {
       animationsDisabled=true
   }
   ``` 
4. In the `android.defaultConfig` section:
   * Change `testInstrumentationRunner` on `testInstrumentationRunner 'com.epam.reportportal.android.junit5.AndroidJUnit5ReportPortalRunner'`
5. In the `dependencies` section:
   * Add your application module dependency: `implementation project(':your_app_module_name')`
   * You can remove these dependencies:
     * `implementation 'androidx.core:core-ktx:1.3.2'`
     * `implementation 'com.google.android.material:material:1.3.0'`
     * `testImplementation 'junit:junit:4.+'`
   * Change `androidTestImplementation` keyword on `implementation` for Espresso libraries:
   ```groovy
   implementation 'androidx.test.ext:junit:1.1.5'
   implementation 'androidx.test.espresso:espresso-core:3.5.1'
   ```
   * Add Report Portal agent dependency:
   ```groovy
   implementation ('com.epam.reportportal:agent-android-junit5:5.3.0') {
       exclude group: 'org.aspectj' // AspectJ usually already included by Android
   }
   ```
   * Add Android-JUnit5 dependencies:
   ```groovy
   implementation 'androidx.test:runner:1.5.2'
   implementation 'de.mannodermaus.junit5:android-test-core:1.4.0'
   implementation 'de.mannodermaus.junit5:android-test-runner:1.4.0'
   ```
   * Add JUnit 5 dependencies:
   ```groovy
    implementation "org.junit.platform:junit-platform-runner:1.10.0"
    implementation "org.junit.jupiter:junit-jupiter-engine:5.10.0"

    // JUnit5 (Optional) If you need "Parameterized Tests"
    implementation "org.junit.jupiter:junit-jupiter-params:5.10.0"
   ```

Here is a full example of `build.gradle` file for Kotlin-based project (remember update library versions):
```groovy
plugins {
    id 'com.android.test'
    id 'kotlin-android'
}

repositories {
    mavenCentral()
}

android {
    compileSdkVersion 33
    buildToolsVersion "30.0.3"

    targetProjectPath ':app'

    defaultConfig {
        minSdkVersion 26
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner 'com.epam.reportportal.android.junit5.AndroidJUnit5ReportPortalRunner'

        consumerProguardFiles "consumer-rules.pro"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude "META-INF/LICENSE*"
    }
    testOptions {
        animationsDisabled=true
    }
}

dependencies {
    implementation project(':app') // your application project

    // Standard android libraries
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"

    // Android libraries for testing Activities
    implementation 'androidx.appcompat:appcompat:1.6.1'

    // Android Espresso libraries
    implementation 'androidx.test.ext:junit:1.1.5'
    implementation 'androidx.test.espresso:espresso-core:3.5.1'

    // Report Portal libraries
    implementation ('com.epam.reportportal:agent-android-junit5:5.3.0') {
        exclude group: 'org.aspectj' // AspectJ usually already included by Android
    }
    implementation 'com.epam.reportportal:logger-java-logback:5.2.2'

    // Logging support, newer versions of logback do not support Android
    implementation 'ch.qos.logback:logback-classic:1.3.12'

    // android-junit5 necessary libraries
    implementation 'androidx.test:runner:1.5.2'
    implementation 'de.mannodermaus.junit5:android-test-core:1.4.0'
    implementation 'de.mannodermaus.junit5:android-test-runner:1.4.0'

    // JUnit5 libraries, 'junit-jupiter-api' is inherited from agent
    implementation "org.junit.platform:junit-platform-runner:1.10.0"
    implementation "org.junit.jupiter:junit-jupiter-engine:5.10.0"

    // JUnit5 (Optional) If you need "Parameterized Tests"
    implementation "org.junit.jupiter:junit-jupiter-params:5.10.0"
}
```
### Create `reportportal.properties` configuration file
You also need to create a file named `reportportal.properties` in your project in a source folder
`src/main/resources` (create this folder if necessary):
```properties
# '10.0.2.2' - is a special alias to your dev host loopback interface (i.e., 127.0.0.1 on your development machine)
rp.endpoint = http://10.0.2.2:8080
rp.api.key = your_api_key
rp.launch = Android JUnit 5 Tests
rp.project = default_personal
```

### JUnit 5 configuration
JUnit 5 requires additional configuration to work seamlessly on Android:
1. Create `META-INF/services` directory inside `src/main/resources`
2. Put a file named `org.junit.jupiter.api.extension.Extension` into the `META-INF/services` 
directory with this content:
```
com.epam.reportportal.android.junit5.AndroidReportPortalExtension
```
3. Create a file named `junit-platform.properties` inside `src/main/resources` folder with the 
following content:
```properties
junit.jupiter.extensions.autodetection.enabled=true
```
4. To enable parallel execution you can add in the `junit-platform.properties` the following lines:
```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.config.strategy=fixed
junit.jupiter.execution.parallel.config.fixed.parallelism=5
```

### Debug Manifest file
Report Portal agent uses Rest API calls to report test, but Android does not allow plain text 
requests by default. To do this please add `debug` folder inside `src` folder of your application 
module and put `AndroidManifest.xml` file there with the following content 
(change `package` attribute on your own):
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools" package="com.epam.test">

   <uses-permission android:name="android.permission.INTERNET" />

   <!-- Allow plaintext traffic and disable backups for debug runs-->
   <application
           android:allowBackup="false"
           android:usesCleartextTraffic="true"
           tools:replace="android:allowBackup">
      <activity
              android:name=".path.to.your.Activity"
              android:exported="true">
         <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
         </intent-filter>
      </activity>
   </application>
</manifest>
```
This overrides some properties in your original manifest allowing plain text requests during the 
test phase only.

## Logging configuration
### Logback Framework
#### Logback dependencies
Put this dependency into `dependencies` section of your `build.gradle` file of integration test 
module:
```groovy
implementation 'com.epam.reportportal:logger-java-logback:5.2.2'
``` 

#### 'logback.xml' file
To add logging configuration put a file named `logback.xml` into `src/main/resources` folder:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- Send debug messages to System.out -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- By default, encoders are assigned the type ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{5} - %thread - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="RP" class="com.epam.reportportal.logback.appender.ReportPortalAppender">
        <encoder>
            <!--Best practice: don't put time and logging level to the final message. Appender do this for you-->
            <pattern>%d{HH:mm:ss.SSS} [%t] %-5level - %msg%n</pattern>
            <pattern>[%t] - %msg%n</pattern>
        </encoder>
    </appender>

    <!--'additivity' flag is important! Without it logback will double-log log messages-->
    <logger name="binary_data_logger" level="TRACE" additivity="false">
        <appender-ref ref="RP"/>
    </logger>

    <!-- Mute Report Portal messages -->
    <logger name="com.epam.reportportal.service" level="WARN"/>
    <logger name="com.epam.reportportal.utils" level="WARN"/>

    <!-- By default, the level of the root level is set to DEBUG -->
    <root level="DEBUG">
        <appender-ref ref="RP"/>
        <!-- Uncomment if you want to see console logs -->
        <!-- <appender-ref ref="STDOUT"/> -->
    </root>
</configuration>
```
## Test run
### Build system commands
To run tests from command-line execute:
```shell script
gradlew junit5-integration-tests:connectedAndroidTest
```

## Examples
### Kotlin examples
See: https://github.com/reportportal/android-kotlin-example
### Java examples
See: https://github.com/reportportal/android-java-example
