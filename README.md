# nplug
Papermc 라이브러리
## 기능
- 구조물
- 가상 FallingBlock, FallingBlock 묶음
## 불러오기
다음 코드를 build.gradle.kts 에 넣어주세요. gradle task downloadPlug는 가장 마지막에 빌드된 nplug를 불러옵니다.
```kotlin
fun downloadFile(url: URL, fileName: String) {
    url.openStream().use { Files.copy(it, Paths.get(fileName)) }
}

fun unZip(zipFilePath: String, targetPath: String) {
    ZipFile(zipFilePath).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                File(targetPath, entry.name).outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}
```
``` kotlin
dependencies {
    implementation(files("lib/nplug.jar"))
}
```
``` kotlin
tasks {
    task("downloadPlug") {
        val folder = file(project.projectDir.absolutePath + File.separator + "lib")
        if (folder.exists()) {
            for (file in folder.listFiles()) {
                if (!file.isDirectory) {
                    file.delete()
                }
            }
        }
        if (!folder.exists()) folder.mkdir()
        val unzipFolder = file(folder.absolutePath + File.separator + "nplug")
        if (!unzipFolder.exists()) unzipFolder.mkdir()
        downloadFile(uri("https://nightly.link/devngho/nplug/workflows/gradle/master/Package.zip").toURL(), folder.absolutePath + File.separator + "nplug.zip")
        unZip(folder.absolutePath + File.separator + "nplug.zip", unzipFolder.absolutePath)
        unzipFolder.listFiles()[0].copyTo(file(folder.absolutePath + File.separator + "nplug.jar"))
    }
}
```
