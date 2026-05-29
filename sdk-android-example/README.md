# SDK Android Example - Liveness
Exemplo simples de integração do SDK de **Liveness** da CAF em um app Android.
## Objetivo
Este projeto demonstra:
- como adicionar o SDK no Gradle;
- como configurar o módulo de liveness;
- como inicializar e iniciar o fluxo;
- como tratar eventos de sucesso, erro, falha e cancelamento.
## Requisitos
- Android Studio atualizado
- JDK 11
- Android `minSdk 26`
- Token válido do SDK (mobile token / `appKey`)
## Instalação
### 1) Configurar repositórios (arquivo `settings.gradle`)
Garanta que os repositórios estejam configurados corretamente:
```gradle
maven { url = uri("https://repo.combateafraude.com/android/release") }
```
### 2) Adicionar dependências (arquivo `app/build.gradle`)
```gradle
dependencies {
    implementation platform('io.caf.sdk:caf-sdk-bom:7.14.0')
    implementation 'io.caf.sdk:caffaceliveness'
    implementation 'io.caf.sdk:caffaceliveness-ui'
    implementation 'io.caf.sdk:caffaceliveness-providers-iproov-lite'
    implementation 'io.caf.sdk:caffaceliveness-providers-payface'
    implementation "io.caf.sdk:document-detector"

    implementation("io.caf.sdk:caf-face-liveness-lite:7.14.0")
}
```
> Se sua integração usar Protobuf JavaLite, siga a recomendação oficial para esse cenário.
## Permissões Android
No `AndroidManifest.xml`, mantenha:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.camera" />
```
## Configuração do SDK
```kotlin
val config = CerttaConfiguration(
   mobileToken = "",
   userId = "user-id",
   securityEnabled = false,
   environment = CafEnvironment.PROD
)

Certta.instance.configure(this, config)
Certta.instance.setLogListener { level: String, message: String ->
    //...
}
```
## Inicialização do Liveness
A configuração do módulo é feita em `buildLiveness()`:
- `presentationOrder` com `CafModuleType.FACE_LIVENESS`;
- `CafFaceLivenessConfig` para parâmetros de execução (loading, debug, retry etc.);
- associação da configuração via `setFaceLivenessConfig(...)`.
## Como iniciar o fluxo na Activity
```kotlin
val livenessConfiguration = LivenessConfiguration(
    maxRetryAttempts = 0,
    faceAuthEnabled = false,
    showLoading = true,
    useFaceLivenessUi = true
)
CerttaLiveness.instance.open(livenessConfiguration) { livenessEvent: LivenessEvent? ->
    //...
}
```
## Executando o projeto
1. Abra no Android Studio.
2. Atualize o `appKey` em `LivenessActivity` e/ou `LivenessLiteActivity`.
3. Ajuste `userId`(opcional).
4. Ajuste o `enviroment` de acordo com o token (homologação, desenvolvimento, produção).
5. Rode em dispositivo com câmera.
6. Toque em **Open Liveness** para iniciar.