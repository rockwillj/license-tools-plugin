# Yet Another Android License Tools Plugin (YAALTP) [![CircleCI](https://circleci.com/gh/cookpad/license-tools-plugin.svg?style=svg)](https://circleci.com/gh/cookpad/license-tools-plugin) [ ![Download](https://api.bintray.com/packages/attosoft/maven/license-tools-plugin/images/download.svg) ](https://bintray.com/attosoft/maven/license-tools-plugin/_latestVersion)

[English](license-tools-plugin/blob/master/README.md) | 日本語

**YAALTP は [Android License Tools Plugin](cookpad/license-tools-plugin) のフォークです。**

ライブラリーのライセンスをチェックしてライセンスページを生成する Gradle プラグインです。

* `./gradlew checkLicenses` - 依存ライブラリーのライセンスをチェックして **`licenses.yml` を生成する**
* `./gradlew generateLicensePage` - ライセンスページ `licenses.html` を生成する
* `./gradlew generateLicenseJson` - ライセンス JSON `licenses.json` を生成する

## セットアップ

**このプラグインは JDK8 (1.8.0 以降) を必要としません。**

```gradle
buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath 'info.attosoft.licensetools:license-tools-plugin:0.17.0_r1'
    }
}

apply plugin: 'com.cookpad.android.licensetools'
```

記述例は [example/build.gradle](license-tools-plugin/blob/master/example/build.gradle) を参照してください。

## 使い方

### `checkLicenses` タスクを実行する

`./gradlew checkLicenses` によって `app/licenses.yml` が生成されます。
`app/licenses.yml` には以下のようなテキストが出力されます：

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

### `app/licenses.yml` のライブラリーライセンスを編集する

次に、`app/licenses.yml` を編集します。必要であれば必須フィールドとあわせてライブラリーを追加します：

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

artifact の名前やバージョンではワイルドカードが利用できます。
ご存知のように Android サポートライブラリーは `com.android.support` 内に分類されているため、ここでは `com.android.support:+:+` が使えます。

そして、`./gradlew checkLicenses` がチェックをパスします。**(YAALTP ではパスしなくても構いません。)**

### `generateLicensePage` タスクにより `licenses.html` を生成する

`./gradlew generateLicensePage` で `app/src/main/assets/licenses.html` を生成します。

このプラグインは `licenses.html` を表示するための `Activity` や `Fragment` を提供しません。自分で追加する必要があります。

記述例は [example/MainActivity](license-tools-plugin/blob/master/example/src/main/java/com/cookpad/android/licensetools/example/MainActivity.java) です。

## データセットフォーマット

### 必須フィールド

* `artifact`
* `name`
* `copyrightHolder`, `author`, `authors`, `notice` のいずれか

### 任意フィールド

* `year` - 著作権の年号を示す
* `skip` - ライセンス項目の生成をスキップする (プロプライエタリなライブラリー向け)

### 記述例

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

## 参照

- [オープンソースライセンスの管理を楽にする -Android アプリ編 - クックパッド開発者ブログ](http://techlife.cookpad.com/entry/2016/04/28/183000)

## 開発者向け

### リリースエンジニアリング

生成物のテスト：

次の `bintrayUpload` タスクを実行します。

```
./gradlew --info --dry-run clean bintrayUpload
```

生成物の公開：

`metadata.gradle` の `BINTRAY_USERNAME` と `BINTRAY_KEY` を Bintray のユーザー名とキーで置き換えます。

```
bintrayUser: 'BINTRAY_USERNAME',
bintrayKey : 'BINTRAY_KEY'
```

そして、次の `bintrayUpload` タスクを実行します。

```sh
./gradlew --info clean bintrayUpload -PdryRun=false
```

## 著作権とライセンス

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

## 謝辞

素敵なオリジナルプラグインをオープンソースで公開してくださっている Cookpad さんに感謝です :bow:
