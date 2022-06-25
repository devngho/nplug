# nplug
Papermc 라이브러리
## 기능
- 구조물
- 가상 FallingBlock, FallingBlock 묶음
## 불러오기
다음 코드를 build.gradle.kts 에 넣어주세요.
```kotlin
repositories {
	//...
	maven { url 'https://jitpack.io' }
}
```
``` kotlin
dependencies {
    //...
    implementation("com.github.devngho:nplug:[VERSION]")
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
        unzipFolder.listFiles().find { (!it.nameWithoutExtension.endsWith("-dev")) && (!it.nameWithoutExtension.endsWith("-all")) }?.copyTo(file(folder.absolutePath + File.separator + "nplug.jar"))
    }
}
```
