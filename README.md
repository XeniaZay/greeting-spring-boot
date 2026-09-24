# Greeting Spring Boot Starter

Кастомный Spring Boot starter для приветствий. Автоматически регистрирует бин `GreetingService`, если пользователь не объявил свою реализацию. Настройки выносятся в `application.yml` через префикс `greeting`.

## Содержание

- [Требования](#требования)
- [Подключение](#подключение)
- [Быстрый старт](#быстрый-старт)
- [Свойства конфигурации](#свойства-конфигурации)
- [Примеры использования](#примеры-использования)
- [Переопределение дефолтного бина](#переопределение-дефолтного-бина)
- [Структура проекта](#структура-проекта)
- [Как это устроено](#как-это-устроено)
- [Сборка и тесты](#сборка-и-тесты)
- [Лицензия](#лицензия)

## Требования

| Компонент | Версия |
|-----------|--------|
| Java | 23+ |
| Spring Boot | 4.1.1+ |
| Gradle | 8.x+ |

## Подключение

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'com.frompegatojava:greeting-spring-boot-starter:0.0.1-SNAPSHOT'
}
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.frompegatojava:greeting-spring-boot-starter:0.0.1-SNAPSHOT")
}
```

### Maven

```xml
<dependency>
    <groupId>com.frompegatojava</groupId>
    <artifactId>greeting-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

> **Примечание:** до публикации в Maven Central используйте `mavenLocal()` или подключение через `project(':greeting-spring-boot-starter')` в multi-module сборке.

## Быстрый старт

### 1. Добавьте зависимость

```groovy
implementation 'com.frompegatojava:greeting-spring-boot-starter:0.0.1-SNAPSHOT'
```

### 2. Укажите обязательные свойства в `application.yml`

```yaml
greeting:
  enabled: true
  prefix: "Hello"
  suffix: "!"
```

Свойство `greeting.prefix` **обязательно** — без него приложение не стартует.

### 3. Используйте `GreetingService`

```java
import com.frompegatojava.greeting.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/greet/{name}")
    public String greet(@PathVariable String name) {
        return greetingService.greet(name);
    }
}
```

Запрос:

```bash
curl http://localhost:8080/greet/World
# Hello, World!
```

## Свойства конфигурации

| Свойство | Тип | По умолчанию | Обязательное | Описание |
|----------|-----|--------------|--------------|----------|
| `greeting.enabled` | `boolean` | `true` | нет | Включает или выключает авто-конфигурацию. При `false` бин `GreetingService` **не создаётся**. |
| `greeting.prefix` | `String` | — | **да** | Префикс приветствия. Не может быть `null`, пустым или состоять из пробелов. |
| `greeting.suffix` | `String` | `"!"` | нет | Суффикс приветствия. Добавляется в конец сообщения. |

### Полный пример `application.yml`

```yaml
greeting:
  enabled: true
  prefix: "Hello"
  suffix: "!"
```

Результат: `greetingService.greet("World")` → `"Hello, World!"`

### Пример с отключённой авто-конфигурацией

```yaml
greeting:
  enabled: false
  prefix: "Hello"
```

Бин `GreetingService` **не создаётся**. Если он где-то инжектится — приложение упадёт с ошибкой `No qualifying bean of type 'GreetingService'`.

### Ошибки конфигурации

Если `greeting.prefix` не задан, пуст или состоит из пробелов — приложение **не стартует**:

```
***************************
APPLICATION FAILED TO START
***************************

Binding validation errors on greeting
   - Field error in object 'greeting' on field 'prefix':
     rejected value [null];
     default message [greeting.prefix must be set and not blank]
```

Это сознательное поведение: лучше упасть при старте, чем отдать клиенту `null, World!` в рантайме.

## Примеры использования

### Пример 1. Дефолтное поведение

```yaml
greeting:
  prefix: "Hello"
  suffix: "!"
```

```java
greetingService.greet("World"); // "Hello, World!"
```

### Пример 2. Кастомный префикс и суффикс

```yaml
greeting:
  prefix: "Привет"
  suffix: "!!!"
```

```java
greetingService.greet("Мир"); // "Привет, Мир!!!"
```

### Пример 3. Через профили

`application-dev.yml`:

```yaml
greeting:
  prefix: "[DEV] Hello"
```

`application-prod.yml`:

```yaml
greeting:
  prefix: "Hello"
```

Активация: `--spring.profiles.active=dev`

## Переопределение дефолтного бина

Если нужно своё поведение — объявите свой бин `GreetingService`. Авто-конфигурация **отступит** благодаря `@ConditionalOnMissingBean`:

```java
import com.frompegatojava.greeting.GreetingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyGreetingConfig {

    @Bean
    public GreetingService myGreetingService() {
        return name -> "Custom hello, " + name;
    }
}
```

Результат: `greetingService.greet("World")` → `"Custom hello, World"`.

Дефолтная реализация `DefaultGreetingService` **не создаётся**. Это подтверждается в отчёте условий при `debug: true`:

```
Negative matches:
-----------------
   GreetingAutoConfiguration#greetingService:
      Did not match:
         - @ConditionalOnMissingBean ... found beans of type 'GreetingService' myGreetingService
```

## Структура проекта

```
greeting-spring-boot/
├── settings.gradle
├── build.gradle
├── README.md
├── greeting-spring-boot-autoconfigure/       ← логика: авто-конфигурация
│   ├── build.gradle
│   └── src
│       ├── main
│       │   ├── java/com/frompegatojava/greeting/
│       │   │   ├── GreetingService.java
│       │   │   ├── DefaultGreetingService.java
│       │   │   ├── GreetingProperties.java
│       │   │   └── GreetingAutoConfiguration.java
│       │   └── resources/META-INF/spring/
│       │       └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│       └── test/java/com/frompegatojava/greeting/
│           └── GreetingPropertiesValidationTest.java
├── greeting-spring-boot-starter/             ← только зависимости, без кода
│   └── build.gradle
└── demo/                                     ← приложение-потребитель
    ├── build.gradle
    └── src/main
        ├── java/com/frompegatojava/demo/
        │   ├── DemoApplication.java
        │   └── GreetingController.java
        └── resources/application.yml
```

### Назначение модулей

| Модуль | Роль | Что содержит |
|--------|------|--------------|
| `greeting-spring-boot-autoconfigure` | Логика библиотеки | Авто-конфигурацию, свойства, валидацию, тесты |
| `greeting-spring-boot-starter` | «Таблетка» для потребителя | Только `build.gradle` с транзитивными зависимостями |
| `demo` | Пример использования | Приложение с REST-контроллером |

## Как это устроено

### Авто-конфигурация

Класс `GreetingAutoConfiguration` помечен `@AutoConfiguration` и активируется при условиях:

```java
@AutoConfiguration
@ConditionalOnProperty(prefix = "greeting", name = "enabled",
                       havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(GreetingProperties.class)
public class GreetingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(GreetingService.class)
    public GreetingService greetingService(GreetingProperties props) {
        return new DefaultGreetingService(props.getPrefix(), props.getSuffix());
    }
}
```

| Аннотация | Что делает |
|-----------|-----------|
| `@AutoConfiguration` | Помечает класс как авто-конфигурацию Spring Boot |
| `@ConditionalOnProperty(..., matchIfMissing = true)` | Включает конфиг, если `greeting.enabled=true` или свойство отсутствует |
| `@ConditionalOnMissingBean` | Не создаёт бин, если пользователь уже определил свой `GreetingService` |
| `@EnableConfigurationProperties` | Регистрирует `GreetingProperties` и связывает с `application.yml` |

### Регистрация

Файл `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
com.frompegatojava.greeting.GreetingAutoConfiguration
```

Именно этот файл Spring Boot читает при старте, чтобы узнать о существовании авто-конфигурации.

### Валидация свойств

`GreetingProperties` использует **Jakarta Validation**:

```java
@Validated
@ConfigurationProperties(prefix = "greeting")
public class GreetingProperties {

    private boolean enabled = true;

    @NotBlank(message = "greeting.prefix must be set and not blank")
    private String prefix;

    private String suffix = "!";

    // getters и setters
}
```

При нарушении ограничений приложение не стартует с `BindValidationException`.

## Сборка и тесты

### Сборка всего проекта

```bash
gradlew.bat build
```

После сборки появятся три jar-файла:

```
greeting-spring-boot-autoconfigure/build/libs/greeting-spring-boot-autoconfigure-0.0.1-SNAPSHOT.jar
greeting-spring-boot-starter/build/libs/greeting-spring-boot-starter-0.0.1-SNAPSHOT.jar
demo/build/libs/demo-0.0.1-SNAPSHOT.jar
```

### Проверка содержимого starter-модуля

```bash
jar tf greeting-spring-boot-starter/build/libs/greeting-spring-boot-starter-0.0.1-SNAPSHOT.jar
```

Ожидаемый вывод — только манифест, **без Java-классов**:

```
META-INF/
META-INF/MANIFEST.MF
```

### Запуск тестов

```bash
gradlew.bat :greeting-spring-boot-autoconfigure:test
```

### Запуск demo-приложения

```bash
gradlew.bat :demo:bootRun
```

### Публикация в локальный Maven

```bash
gradlew.bat publishToMavenLocal
```

(требует подключения плагина `maven-publish`)

## Лицензия

MIT License. Свободно используйте в учебных и коммерческих проектах.