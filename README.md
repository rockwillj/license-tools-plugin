# Yet Another Android License Tools Plugin (YAALTP) [![CircleCI](https://circleci.com/gh/cookpad/license-tools-plugin.svg?style=svg)](https://circleci.com/gh/cookpad/license-tools-plugin) [ ![Download](https://api.bintray.com/packages/attosoft/maven/license-tools-plugin/images/download.svg) ](https://bintray.com/attosoft/maven/license-tools-plugin/_latestVersion)

English | [日本語](README.ja.md)

**YAALTP is a fork of [Android License Tools Plugin](https://github.com/cookpad/license-tools-plugin).**

Gradle Plugin to check library licenses and generate license pages.

![demo](art/yaaltp_demo.gif)

* `./gradlew checkLicenses` to check licenses in dependencies, **and generate `licenses.yml`**
* `./gradlew generateLicensePage` to generate a license page `licenses.html`
* `./gradlew generateLicenseJson` to generate a license json file `licenses.json`

## Setup

**This plugin does not require JDK8 (1.8.0 or later)**.

```gradle
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'info.attosoft.licensetools:license-tools-plugin:0.19.1_r1'
    }
}

apply plugin: 'com.cookpad.android.licensetools'
```

See [example/build.gradle](example/build.gradle) for example.

## How To Use

### Run the `checkLicenses` task

`app/licenses.yml` will be generated by `./gradlew checkLicenses`.
You will see the following texts in `app/licenses.yml`:

```yaml
- artifact: com.android.support:support-v4:+
  name: #NAME#
  copyrightHolder: #AUTHOR#
  license: No license found
- artifact: com.android.support:animated-vector-drawable:+
  name: #NAME#
  copyrightHolder: #AUTHOR#
  license: No license found
- artifact: io.reactivex:rxjava:+
  name: #NAME#
  copyrightHolder: #AUTHOR#
  license: apache2
 ```

### Modify library licenses in `app/licenses.yml`

Then, Modify `app/licenses.yml`, and add libraries if needed with required fields:

```yaml
- artifact: com.android.support:+:+
  name: Android Support Libraries
  copyrightHolder: The Android Open Source Project
  license: apache2
- artifact: io.reactivex:rxjava:+
  name: RxJava
  copyrightHolder: Netflix, Inc.
  license: apache2
```

You can use wildcards in artifact names and versions.
You'll know the Android support libraries are grouped in `com.android.support` so you use `com.android.support:+:+` here.

Then, `./gradlew checkLicenses` will passes. **(It does not have to pass in YAALTP.)**

### Generate `licenses.html` by the `generateLicensePage` task

`./gradlew generateLicensePage` generates `app/src/main/assets/licenses.html`.

This plugin does not provide `Activity` nor `Fragment` to show `licenses.html`. You should add it by yourself.

[example/MainActivity](example/src/main/java/com/cookpad/android/licensetools/example/MainActivity.java) is an example.

## DataSet Format

### Required Fields

* `artifact`
* `name`
* Either `copyrightHolder`, `author`, `authors` or `notice`

### Optional Fields

* `year` to indicate copyright years
* `skip` to skip generating license entries (for proprietary libraries)

### Example

```yaml
- artifact: com.android.support:+:+
  name: Android Support Libraries
  copyrightHolder: The Android Open Source Project
  license: apache2
- artifact: org.abego.treelayout:org.abego.treelayout.core:+
  name: abego TreeLayout
  copyrightHolder: abego Software
  license: bsd_3_clauses
- artifact: io.reactivex:rxjava:+
  name: RxJava
  copyrightHolder: Netflix, Inc.
  license: apache2
- artifact: com.tunnelvisionlabs:antlr4-runtime:4.5
  name: ANTLR4
  authors:
    - Terence Parr
    - Sam Harwell
  license: bsd_3_clauses
- artifact: com.github.gfx.android.orma:+:+
  name: Android Orma
  notice: |
    Copyright (c) 2015 FUJI Goro (gfx)
    SQLite.g4 is: Copyright (c) 2014 by Bart Kiers
  license: apache_2
- artifact: io.reactivex:rxandroid:1.1.0
  name: RxAndroid
  copyrightHolder: The RxAndroid authors
  license: apache2
- artifact: license-tools-plugin:example-dep:+
  skip: true
```

## See Also

- [オープンソースライセンスの管理を楽にする -Android アプリ編 - クックパッド開発者ブログ](http://techlife.cookpad.com/entry/2016/04/28/183000) (Japanese)

## For Developers

### Release Engineering

To test artifacts:

Run the following `bintrayUpload` task.

```
./gradlew --info --dry-run clean bintrayUpload
```

To publish artifacts:

Replace `BINTRAY_USERNAME` and `BINTRAY_KEY` with your Bintray user name and key in `metadata.gradle`.

```
bintrayUser: 'BINTRAY_USERNAME',
bintrayKey : 'BINTRAY_KEY'
```

Then, Run the following `bintrayUpload` task.

```sh
./gradlew --info clean bintrayUpload -PdryRun=false
```

## Copyright and License

Copyright (c) 2016-2017 rockwillj (modified part)<br>
Copyright (c) 2016 Cookpad Inc. (original part)

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Acknowledgments

Thank you Cookpad Inc. for publishing a great original plugin in open source :bow:
