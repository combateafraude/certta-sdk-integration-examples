# SDK Android Example - Liveness
Exemplo simples de integração do SDK de **Liveness** da Certta em um app Android.
## Objetivo
Este projeto demonstra:
- como adicionar o SDK no Gradle;
- como configurar o módulo de liveness;
- como inicializar e iniciar o fluxo;
## Requisitos
- Android Studio atualizado
- JDK 11
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
CafFaceLivenessLite.initialize(
    config = CafLivenessConfig(
        mobileToken = "",
        stage = CafStage.PROD,
        personId = "user-123",
        loading = true,
        executeFaceAuth = false,
        enableSecurity = false,
    )
)
```
## Como iniciar o fluxo na Activity
```kotlin
CafFaceLivenessLite.startLiveness(this, object : CafUnifiedCallback {
    //...
})
```
## Executando o projeto
1. Abra no Android Studio;
2. Atualize o `appKey` em `LivenessLiteActivity`;
3. Ajuste `userId`(opcional);
4. Ajuste o `enviroment` de acordo com o token (homologação, desenvolvimento, produção);
5. Rode em dispositivo com câmera;
6. Toque em **Open Liveness** para iniciar.