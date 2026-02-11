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
repositories {
    // Caf Repository
    maven { url = uri("https://repo.combateafraude.com/android/release") }
    
    // iProov Repository
    maven { url = uri("https://raw.githubusercontent.com/iProov/android/master/maven/") }
    
    // FingerPrintJS Repository
    maven { setUrl("https://maven.fpregistry.io/releases") }
    
    // JitPack
    maven { setUrl("https://jitpack.io") }

    // Fortface Repository - required to use the PayFace provider
    maven {
        url = uri("https://cdn-fortface-sdk.fortface.com.br")
        credentials(HttpHeaderCredentials::class) {
            name = "X-Sdk"
            value = "CAF"
        }
        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
    
}
```
### 2) Adicionar dependências (arquivo `app/build.gradle`)
```gradle
dependencies {
    implementation platform("io.caf.sdk:caf-sdk-bom:7.8.0")
    implementation "io.caf.sdk:caffaceliveness-lite"
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
No arquivo `app/src/main/java/io/certta/sdk/example/SdkExampleConfig.java`:
1. Defina seu token:
   - `appKey` (mobile token)
2. Defina o identificador da pessoa:
   - `personId` no `CafSdkProvider.Builder`
3. Escolha ambiente:
   - `CafEnvironment.PROD` para produção
Trecho principal da inicialização:
```java
CafSdkProvider.Builder sdkBuilder = new CafSdkProvider.Builder(
    appKey,
    "person-id",
    CafEnvironment.PROD,
    sdkConfiguration,
    event -> {
        handleEvent(event);
        return Unit.INSTANCE;
    }
);
```
## Inicialização do Liveness
A configuração do módulo é feita em `buildLiveness()`:
- `presentationOrder` com `CafModuleType.FACE_LIVENESS`;
- `CafFaceLivenessConfig` para parâmetros de execução (loading, debug, retry etc.);
- associação da configuração via `setFaceLivenessConfig(...)`.
## Como iniciar o fluxo na Activity
No `MainActivity`, o exemplo inicia o SDK ao clicar no botão:
```java
SdkExampleConfig config = new SdkExampleConfig("SEU_APP_KEY");
button.setOnClickListener(view -> {
    config.buildLiveness().start(this.getApplicationContext(), this);
});
```
## Tratamento de eventos
O método `handleEvent(...)` trata os retornos do SDK:
- `Loading`
- `Loaded`
- `Success`
- `Failure`
- `Error`
- `Cancelled`
- `Log`
No exemplo, os eventos são exibidos com `Log.d(...)`. Adicione o filtro `CerttaSdkEvent` para visualizar os logs.
## Executando o projeto
1. Abra no Android Studio.
2. Atualize o `appKey` em `MainActivity` (ou em uma camada segura de configuração).
3. Ajuste `personId` em `SdkExampleConfig` (opcional).
4. Rode em dispositivo com câmera.
5. Toque em **Open Liveness** para iniciar.
## Observações
- Não versionar tokens reais no repositório.
- Para ambiente de homologação/desenvolvimento, ajuste o `CafEnvironment` conforme necessário.
- Este repositório é um exemplo base; adapte logs, UI e tratamento de retorno para seu fluxo de negócio.
