### **1\. Введение**


#### Project deploy 

#### **1.1 Цель проекта**

Цель проекта — изучение и использование векторных баз данных, а также освоение Java и Vue.js 3. Разработана система для управления контактами с векторным поиском с применением SpringBoot, ElasticSearch и PostgreSQL. Проект фокусируется на эффективном поиске и анализе данных с помощью эмбеддингов.

#### **1.2 Описание задачи и значимость проекта**

Задача проекта — создание системы для поиска контактов с использованием векторных баз данных и нейросетевых эмбеддингов. Векторные базы данных помогают искать информацию по смыслу запроса, а не только по точному совпадению. Этот проект актуален в свете роста интереса к ИИ и машинному обучению, а также позволяет познакомиться с интеграцией нейросетей в приложения.

#### **1.3 Обоснование выбора технологий**

Для проекта были выбраны следующие технологии:

- **SpringBoot** — фреймворк для серверной части приложения.
- **PostgreSQL** — база данных для хранения информации.
- **Python + all-mpnet-v2** — для генерации эмбеддингов на локальной машине.
- **ElasticSearch** — система для хранения и поиска данных с векторными запросами.
- **Vue.js 3** — фреймворк для создания интерфейсов.

Эти технологии были выбраны за свою популярность, широкое применение и совместимость для создания эффективного и масштабируемого решения.

**2\. Общее описание архитектуры**

**2.1 Бэкенд архитектура**

Проект использует **двухсервисную архитектуру**, разделяя функциональность на два независимых приложения: одно для CRUD-операций с данными и другое для работы с векторными эмбеддингами и ElasticSearch. Они взаимодействуют между собой по HTTP.

Пользователь или клиент не имеет права директивно обращаться к внутреннему сервису пайтон. Поэтому приложение для работы с эмбедингами использует айпи проверку, чтобы избежать несанцкионированного вмешательства. Плюсом весь бэкэнд запускается в рамках одного docker-compose файла, что позволяет им общаться внутри единой сети, не открывая доступ к внешним приложениям.

**1\. Сервис для CRUD-операций (Java + SpringBoot):**

Этот сервис отвечает за управление данными о пользователях, контактах, заметках и тегах. Он реализует стандартный набор CRUD-операций для взаимодействия с реляционной базой данных (PostgreSQL). Все операции с данными выполняются через REST API, реализованное с использованием **Spring Boot** и **Spring Data JPA** для взаимодействия с базой данных.

**Основные функции**:

\- Управление пользователями.

\- Управление контактами.

\- Управление заметками.

\- Управление тегами.

\- Управление аутентификацией пользователей

\- Взаимодействие с внутренним сервисом, который работает с ElasticSearch

**2\. Сервис для работы с эмбеддингами и ElasticSearch (Python + sentence-transformers + all-mpnet-v2):**

Второй сервис реализован на **Python** и отвечает за генерацию и обработку эмбеддингов для векторного поиска. Этот сервис использует библиотеку **sentence-transformers** с моделью **all-mpnet-v2**, которая генерирует эмбеддинги для текстовых данных. Затем эти эмбеддинги индексируются в **ElasticSearch**, который используется для быстрого поиска и извлечения релевантных результатов.

**Основные функции**:

\- Генерация эмбеддингов с использованием модели all-mpnet-v2.

\- Индексация эмбеддингов в **ElasticSearch** для последующего поиска.

\- Обработка запросов на поиск, анализ векторных данных и предоставление результатов.

**Докеризация и оркестрация:**

Для контейниризации и оркестрации всех сервисов используется **Docker** и **Docker Compose**. Каждый из сервисов (Java для CRUD-операций и Python для работы с эмбеддингами и ElasticSearch) упакован в отдельный контейнер. Это позволяет легко развертывать и масштабировать систему.

**Docker Compose** используется для настройки взаимодействия между контейнерами и оркестрации сервисов, упрощая управление многосервисной системой.

**Схема архитектуры проекта:**

+----------------------------------------+
|            Frontend (Vue.js 3)         |
|    - Отправка запросов на сервер       |
+----------------------------------------+
|
v
+----------------------------------------+
|        Backend (SpringBoot CRUD)       |
|    - Управление данными (User, Contact)|
|    - Обработка запросов и операций    |
+----------------------------------------+
|
v
+----------------------------------------+
|      Backend (Python + ElasticSearch)  |
|    - Генерация и обработка эмбеддингов |
|    - Поиск векторных данных            |
+----------------------------------------+
|
v
+----------------------------------------+
|           PostgreSQL (CRUD DB)         |
|    - Хранение данных о пользователях,  |
|      контактах, заметках и тегах       |
+----------------------------------------+


**2.3 Схема базы данных для CRUD приложения**

Схема базы данных построена для поддержания данных о пользователях, контактах, заметках и тегах. Используется **PostgreSQL** для хранения реляционных данных.

![f1f66327-a9d3-48a4-924f-cd296b2ccd65](https://github.com/user-attachments/assets/d29518da-e5aa-4c86-a58a-9c02c082f05d)

**2.4 Swagger для взаимодействия с бэкендом**

Для удобства взаимодействия с REST API используется **Swagger**. Swagger автоматически генерирует документацию для всех доступных API-эндпоинтов, что позволяет быстро проверить доступные операции и параметры запросов. Удобно при разработке, и не требует написания дополнительной документации.

**2.5 Аутентификация и авторизация**

Для управления доступом к системе используется механизм **JWT (JSON Web Token)**. Аутентификация и авторизация реализованы следующим образом:

1. **Регистрация**:
    - Пользователь создаёт свою учетную запись, предоставляя email и пароль. Пароль хэшируется с использованием brcypt, это позволяет сохранить данные пользователя.
2. **Вход в систему**:
    - Пользователь вводит email и пароль, сервер проверяет их и генерирует **JWT-токен**, который содержит информацию о пользователе.
3. **JWT-токен**:
    - Токен отправляется клиенту и сохраняется в локальном хранилище, в LocaStorage
    - Токен используется для аутентификации всех последующих запросов.
4. **Проверка токена**:
    - Все защищенные эндпоинты на сервере требуют передачи JWT-токена в заголовке запроса.
    - Токен проверяется на сервере для подтверждения подлинности и определения прав доступа пользователя.
5. **Refresh JWT-токена**
    - Выдавать один токен без ограничения срока действия небезопасно. Поэтому в проекте был реализован механизм обновления токена.
    - При входе в приложение пользователь получает связано auth+refresh токен, и в случае истечения может обновить связку. Это позволяет избежать проблемы, если основной токен будет украден, то он просто истечет, и злоумышленник не получит доступ к приложению.

**3\. Описание CRUD операций**

**3.1.1 Описание CRUD операций с таблицами User и Contact**

Для работы с таблицами **User** и **Contact** используется стандартный набор операций CRUD: **Create**, **Read**, **Update**, **Delete**. Каждая операция реализована с помощью соответствующих методов в слоях **Model**, **Repository**, **Service**, **Resource**.

1. **Model** – Представление структуры данных.
    - В модели для таблицы User и Contact мы описываем их атрибуты как Java классы с аннотациями для работы с базой данных (например, @Entity, @Table, @Id).
2. **Repository** – Интерфейс, через который осуществляется доступ к базе данных.
    - Используется Spring Data JPA для создания репозиториев. Пример:

```Java
public interface ContactRepository extends JpaRepository<Contact, Integer> {

    Contact findFirstByTags(Tag tag);

    Contact findFirstByOwner(User user);

    List<Contact> findAllByOwner(User user);

    Contact findFirstByContactTag(Tag tag);

    List<Contact> findAllByContactTag(Tag tag);
}
```

1. **Service** – Логика работы с данными, которая включает в себя бизнес-логику.
    - Пример: в сервисе для User могут быть методы для создания, поиска или обновления данных пользователя.
```Java
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    public UserService(final UserRepository userRepository,
                       final ContactRepository contactRepository) {
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
    }

    public List<UserDTO> findAll() {
        final List<User> users = userRepository.findAll(Sort.by("id"));
        return users.stream()
                .map(user -> mapToDTO(user, new UserDTO()))
                .toList();
    }

    public UserDTO get(final Integer id) {
        return userRepository.findById(id)
                .map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public UserDTO get(final String username) {
        return userRepository.findByUsername(username).map(user -> mapToDTO(user, new UserDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UserDTO userDTO) {
        final User user = new User();
        mapToEntity(userDTO, user);
        return userRepository.save(user).getId();
    }

    public void update(final Integer id, final UserDTO userDTO) {
        final User user = userRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(userDTO, user);
        userRepository.save(user);
    }

    public void delete(final Integer id) {
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(final User user, final UserDTO userDTO) {
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }

    private User mapToEntity(final UserDTO userDTO, final User user) {
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        return user;
    }
}
```


1. **Resource (Controller)** – Контроллеры, которые обрабатывают HTTP-запросы от клиента.
    - Контроллер будет принимать запросы от фронтенда и передавать их в соответствующий сервис.
```Java
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserResource {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserResource(final JwtUtil jwtUtil, final UserService userService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    private UserDTO getUser(String token, int userId) {
        String strUserId = jwtUtil.getUserIdFromToken(token);

        UserDTO authenticatedUser = userService.get(Integer.parseInt(strUserId));
        if (authenticatedUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        if (!authenticatedUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own contacts");
        }
        return authenticatedUser;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable(name = "id") final Integer id,
                                           @RequestHeader("Authorization") String token) {
        getUser(token, id);
        return ResponseEntity.ok(userService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createUser(@RequestBody @Valid final UserDTO userDTO) {
        final Integer createdId = userService.create(userDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateUser(@PathVariable(name = "id") final Integer id,
                                              @RequestBody @Valid final UserDTO userDTO,
                                              @RequestHeader("Authorization") String token) {
        getUser(token, id);
        userService.update(id, userDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") final Integer id,
                                           @RequestHeader("Authorization") String token) {
        getUser(token, id);
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
```

**3.1.3 Пример запросов для работы с User и Contact**

Для каждой таблицы доступны следующие основные HTTP-методы:

- **User (POST /auth/register)**: Создает нового пользователя.
```
POST /auth/register

{

"username": "john_doe",

"password ": "example_password"

}
```
- **User (POST /auth/login)**: Вход в аккаунт пользователя.
```
POST /auth/register

{

"username": "john_doe",

"password ": "example_password"

}

200: {

"token":"auth_token",

"refreshToken":"refresh_token",

“username":"user12",

"userId":10003

}
```
- **User (PUT /users/{id})**: Обновляет информацию о пользователе.
```
PUT /users/1

{

"username": "johnny_doe",

"email": "<johnny@example.com>"

}
```

- **User (DELETE /users/{id})**: Удаляет пользователя.
```
DELETE /users/1
```
- **Contact (POST /contacts)**: Создает новый контакт.
```
POST /contacts

{

"name": "John Doe",

"jobTitle": "Software engineer",

"email": "<john@doe.com>",

"phone": "15608365748",

"ownerId": 12,

"contactTags": [20]

}
```
- **Contact (GET /contacts/{id})**: Получает информацию о контакте по ID.
```
GET /contacts/{id}

200:

{

"id": 0,

"name": "string",

"jobTitle": "string",

"email": "string",

"phone": "string",

"createdAt": "2025-02-26T02:34:35.461Z",

"updatedAt": "2025-02-26T02:34:35.461Z",

"tags": 0,

"owner": 0,

"contactTags": [0]

}
```
- **Contact (PUT /contacts/{id})**: Обновляет контакт.
```
PUT /contacts/{id}

{

"id": 0,

"name": "string",

"jobTitle": "string",

"email": "string",

"phone": "string",

"createdAt": "2025-02-26T02:34:35.461Z",

"updatedAt": "2025-02-26T02:34:35.461Z",

"tags": 0,

"owner": 0,

"contactTags": [0]

}
```
- **Contact (DELETE /contacts/{id})**: Удаляет контакт.
```
DELETE /contacts/1
```
**3.2 Таблица Note и Tag**

**3.2.1 Описание CRUD операций с этими таблицами**

Как и для предыдущих таблиц, для **Note** и **Tag** реализуются CRUD-операции, используя ту же структуру Model-Repositiry-Service-Resource.

**3.2.2 Пример запросов для работы с Note и Tag**

- **Note (POST /notes)**: Создает новую заметку.
```JSON
POST /notes

{

"contact": 1,

"content": "This is a note about contact."

}
```
- **Note (GET /notes/{id})**: Получает информацию о заметке.
```
GET /notes/1

200:

{

"id": 0,

"content": "string",

"createdAt": "2025-02-26T02:37:54.309Z",

"updatedAt": "2025-02-26T02:37:54.309Z",

"contact": 0

}
```
- **Note (PUT /notes/{id})**: Обновляет текст заметки.
```
PUT /notes/1

{

"id": 0,

"content": "string",

"createdAt": "2025-02-26T02:37:54.309Z",

"updatedAt": "2025-02-26T02:37:54.309Z",

"contact": 0

}
```
- **Note (DELETE /notes/{id})**: Удаляет заметку.
```
DELETE /notes/1
```
- **Tag (POST /tags)**: Создает новый тег.
```
POST /tags

{

"contact": 1,

"name ": "Important"

}
```
- **Tag (GET /tags/{id})**: Получает информацию о теге.
```
GET /tags/1
```
- **Tag (PUT /tags/{id})**: Обновляет текст тега.
```
PUT /tags/1

{

"tag_text": "Urgent"

}
```
- **Tag (DELETE /tags/{id})**: Удаляет тег.
```
DELETE /tags/1
```
**3.3 Резюме**

Для каждой таблицы (User, Contact, Note, Tag) доступны следующие роуты с методами POST, GET, PUT и DELETE. Архитектура Model-Repository-Service-Resource позволяет четко разделить логику работы с данными на четыре слоя, что обеспечивает ясность и удобство в поддержке и расширении приложения.

**4\. Основная часть — Поисковая система**

В данной главе подробно рассматривается поисковая система, реализованная в отдельном Python-сервисе, который занимается генерацией эмбеддингов и поиском по векторным представлениям данных в ElasticSearch. Ниже приведены ключевые аспекты работы системы.

![3569851e-cdfb-4545-b9b4-e1d866ed253b](https://github.com/user-attachments/assets/0b21755d-077b-453f-914b-07ce4d27edbb)


**4.1 Генерация эмбеддингов с использованием нейросетей**

**Логика работы Python-приложения:**  
Python-сервис реализован с использованием FastAPI и отвечает за генерацию эмбеддингов для контактных данных. Для этой задачи используется библиотека **sentence-transformers** с моделью **all-mpnet-v2**. При каждом добавлении или изменении контакта, заметки или тегов сервис собирает всю релевантную информацию об объекте (например, имя контакта, должность, заметки, теги) в одну строку с помощью функции join. Такой подход позволяет избежать генерации отдельного эмбеддинга для каждого поля и, вместо этого, получать единое представление для всего контакта.

**Почему выбран такой подход:**

- **Эффективность:** Объединение всех текстовых данных в одну строку позволяет сгенерировать один эмбеддинг, который отражает совокупную информацию о контакте.
- **Простота:** Это упрощает логику обновления – при любом изменении данных, генерируется новый эмбеддинг, соответствующий актуальной информации.
- **Согласованность данных:** Суммарное представление помогает системе лучше определять семантическую близость между запросом и данными контакта. Например, если пользователь ищет не только по заметкам, а еще по именам и другим параметрам.

**4.2 Обработка эмбеддингов в ElasticSearch**

**Работа с векторными представлениями данных:**  
ElasticSearch используется для хранения эмбеддингов, что позволяет реализовать векторный (knn) поиск. При индексировании каждого контакта, эмбеддинг сохраняется вместе с основными метаданными (имя, должность, теги, заметки). Это позволяет эффективно выполнять поиск по семантическому сходству.

**Алгоритм knn поиска:**

- **Индексация:** При каждом обновлении данных контакта, его эмбеддинг пересчитывается и индексируется в ElasticSearch.
- **Поиск:** При получении поискового запроса система генерирует эмбеддинг запроса, после чего ElasticSearch выполняет knn поиск. Он сравнивает вектор запроса с векторами всех контактов и возвращает k наиболее близких по косинусному сходству.

**Почему ElasticSearch:**

- **Производительность:** ElasticSearch оптимизирован для обработки больших объемов данных и позволяет выполнять поиск с минимальными задержками.
- **Масштабируемость:** Возможность горизонтального масштабирования делает его пригодным для продакшн-систем с большим числом записей.
- **Гибкость:** Встроенная поддержка векторных запросов (knn) упрощает реализацию семантического поиска.

**4.3 Примеры поисковых запросов**

Для каждой операции поиска через API доступны следующие запросы:

- **Поиск по тексту (semantic search):**  
    Клиент отправляет запрос с текстом, из которого генерируется эмбеддинг, и ElasticSearch возвращает наиболее релевантные контакты.  
    Пример запроса (JSON):
```
POST /api/query/search

{

"user_id": "400",

"query_text": "make a call with client on export spare parts",

"k": 5,

"num_candidates": 100

}
```
- **Поиск по тегам:**  
    Запрос, содержащий список тегов, позволяет отфильтровать контакты по указанным критериям.
```
POST /api/query/search/tags

{

"user_id": "400",

"tags": \["sales", "export"\]

}
```
- **Генерация эмбеддингов:**  
    Запрос для генерации эмбеддинга по произвольному тексту.
```
POST /api/query/embedding

{

"text": "Sample text for embedding generation"

}
```

**4.4 Выбор стека технологий и логика поиска**

**Почему выбран стек технологий:**

- **Python + FastAPI:**  
    Python предоставляет удобные библиотеки для работы с нейросетями (sentence-transformers) и обработки эмбеддингов. FastAPI позволяет быстро и эффективно создавать REST API, а также обладает высокой производительностью.
- **all-mpnet-v2:**  
    Она тяжелее конкурентов, однако позволяет генерировать более длинные вектора, что улучшает поиск.
- **ElasticSearch:**  
    Благодаря поддержке векторных запросов и масштабируемости, ElasticSearch является оптимальным выбором для продакшн-систем, где важна скорость и надежность поиска.
- **Docker + Docker Compose:**  
    Контейнеризация позволяет изолировать сервисы и упрощает их развёртывание и масштабирование, а Docker Compose помогает оркестрировать все компоненты системы.

**Логика поиска:**  
При каждом изменении контакта (или его заметок/тегов) происходит пересчёт эмбеддинга. Все текстовые поля объединяются в единую строку, что позволяет генерировать одно согласованное векторное представление. Это представление индексируется в ElasticSearch, и при поисковом запросе система генерирует эмбеддинг запроса и выполняет knn поиск, сравнивая его с индексированными векторами. Такой подход позволяет эффективно находить релевантные контакты даже при вариативности поисковых запросов.

**4.5 Архитектура приложения**

Архитектура приложения основана на модели **Repository → Service → Resource**, что характерно для Java-приложений и перенесено на оба бэкенд-сервиса:

- **Репозиторий (Repository):** Обеспечивает доступ к базе данных и инкапсулирует логику работы с данными.
- **Сервис (Service):** Реализует бизнес-логику, включая генерацию эмбеддингов и обновление данных.
- **Ресурс (Resource/Controller):** Обрабатывает HTTP-запросы и связывает их с соответствующими сервисами.

Такой подход позволяет четко разделить ответственность между различными слоями приложения, облегчает поддержку и расширение кода, а также повышает его читаемость.

**4.6 Преимущества локального генератора эмбеддингов**

Запуск нейронной сети для генерации эмбеддингов локально имеет ряд преимуществ:

- **Независимость от сторонних сервисов:** Это позволяет обеспечить стабильную работу сервиса без зависимости от внешних API, что критически важно в случае проблем с доступом к сторонним сервисам.
- **Доступность:** Локальное решение всегда доступно, не требуется настройка VPN или других туннелей для доступа к внешним API, особенно актуально для регионов с ограничениями, например, в Китае.
- **Контроль над данными:** Все вычисления выполняются локально, что повышает безопасность и конфиденциальность обрабатываемых данных.

**4.7. Работа с эмбеддингами**

Базово существует асбрактный класс, который выполняет роль интерфейса.

```Python
class IEmbeddingsRepository(ABC):
    @abstractmethod
    def generate_embedding(self, text: str) -> List[float]:
        """Generate an embedding for the given text."""
        pass

    @abstractmethod
    def store_embedding(self, document_id: str, embedding: List[float], metadata: Dict[str, any]) -> None:
        """Store an embedding in Elasticsearch."""
        pass

    @abstractmethod
    def get_embedding(self, document_id: str) -> Dict:
        """Retrieve embedding metadata from Elasticsearch."""
        pass
```

Использование интерфейса помогает создать модульность проекта. Сегодня мы генерируем эмбеддинге с использованием локальной нейросети. Но например завтра из-за большого наплыва пользователей мы решим повысить перфоманс нашего приложения, и уйти в сторону использования API. Таких как OpenAI, Alibaba cloud etc.

Реализация офлайн генерации эмбеддингов:
```Python
class OfflineEmbeddingRepository(IEmbeddingsRepository):
    def __init__(self, elastic_repository):
        self.model = SentenceTransformer(f"{sys.path[1]}/app/nn_model/all-mpnet-base-v2")
        self.elastic_repo = elastic_repository
        self.logger = get_logger()

    def generate_embedding(self, text: str) -> List[float]:
        self.logger.debug(f"Generating embedding for text: {text}")
        embedding = self.model.encode(text).tolist()
        self.logger.debug(f"Generated embedding: {embedding[:10]}...")  # Log first 10 floats for brevity
        return embedding

    def store_embedding(self, document_id: str, embedding: List[float], metadata: Dict[str, any]) -> None:
        """Store embedding in Elasticsearch."""
        self.logger.debug(f"Storing embedding for document ID: {document_id}")
        document = {
            **metadata,
            "embedding": embedding
        }
        self.elastic_repo.index_document(document_id=document_id, document=document)

    def get_embedding(self, document_id: str) -> Dict:
        """Retrieve embedding from Elasticsearch."""
        self.logger.debug(f"Retrieving embedding for document ID: {document_id}")
        document = self.elastic_repo.get_document(document_id)
        if not document:
            self.logger.warning(f"No embedding found for document ID: {document_id}")
            return None
        self.logger.debug(f"Retrieved embedding for document ID: {document_id}")
        return document
```

**4.8. Работа с ElasticSearch**

Так как это слой работы с данными, то он должен быть легко заменяемым. Поэтому было принято решение создать отдельный интерфейс и реализацию для него

```Python
class IElasticRepository:
    def index_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Index a contact document in Elasticsearch."""
        pass

    def get_contact(self, contact_id: str) -> Optional[Dict[str, Any]]:
        """Retrieve a contact document from Elasticsearch."""
        pass

    def update_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Update a contact document in Elasticsearch."""
        pass

    def delete_contact(self, contact_id: str) -> None:
        """Delete a contact document from Elasticsearch."""
        pass

    def search(self, search_body: Dict[str, Any]) -> List[Dict[str, Any]]:
        """Search in Elastic."""
        pass

```

Реализация с использованием ElasticSearch клиента:

```Python
class ElasticRepository(BaseRepository, IElasticRepository):
    def __init__(self):
        super().__init__()
        self.index_name = config.ELASTICSEARCH_INDEX_NAME

    def index_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Index a contact document in Elasticsearch."""
        self.index_document(index=self.index_name, document_id=contact_id, document=document)
        self.client.indices.refresh(index=self.index_name)

    def get_contact(self, contact_id: str) -> Optional[Dict[str, Any]]:
        """Retrieve a contact document from Elasticsearch."""
        return self.get_document(index=self.index_name, document_id=contact_id)

    def update_contact(self, contact_id: str, document: Dict[str, Any]) -> None:
        """Update a contact document in Elasticsearch."""
        self.update_document(index=self.index_name, document_id=contact_id, document=document)
        self.client.indices.refresh(index=self.index_name)

    def delete_contact(self, contact_id: str) -> None:
        """Delete a contact document from Elasticsearch."""
        self.delete_document(index=self.index_name, document_id=contact_id)
        self.client.indices.refresh(index=self.index_name)

    def search(self, search_body: Dict[str, Any]) -> List[Dict[str, Any]]:
        """
        Perform a search query in Elasticsearch.

        :param search_body: The body of the search query.
        :return: A list of matching documents.
        """
        logger = get_logger()
        logger.debug(f"Executing search on index {self.index_name} with query: {search_body}")
        try:
            response = self.client.search(index=self.index_name, body=search_body)
            hits = response.get("hits", {}).get("hits", [])
            results = [hit["_source"] for hit in hits]
            logger.info(f"Search returned {len(results)} results.")
            return results
        except Exception as e:
            logger.error(f"Error executing search: {e}")
            raise
```

**4.9 Тестирование**

В процессе разработки проводилось тестирование как отдельного Python-сервиса, так и интеграция с ElasticSearch:

```Python
class TestSearchService(unittest.TestCase):
    def setUp(self):
        dotenv.load_dotenv()
        print("STARTING...")
        self.elastic_repository = ElasticRepository()
        self.service = SearchService(elastic_repository=self.elastic_repository,
                                     embedding_repository=OfflineEmbeddingRepository(self.elastic_repository))
        self.elastic = Elasticsearch(
            hosts=[{"host": config.ELASTIC_HOST, "port": config.ELASTICSEARCH_PORT}],
            scheme="http",
            verify_certs=False
        )
        self.elastic.ping()
        preload_data(self.elastic, "test_index")

    def test_search_by_text(self):
        query = "make a video in instagram"
        results = self.service.search_contacts("400", query, 10, 100)
        self.assertTrue(len(results) > 0)
        i = 0
        for item in results:
            print(f"{i}. Name: {item['name']} jobTitle: {item['jobTitle']} tags: {item['tags']}")
            i += 1
```

![c9d2845c-2b88-4245-a89c-30bec58b18f7](https://github.com/user-attachments/assets/20005431-12eb-4dab-9664-f66473eff860)



Внешние тесты, которые показывают, что приложение отрабатывает свой функционал. А именно: сохраняет, ищет, индексирует. Используется заранее подготовленные данные, чтобы избежать неточности тестирования.

```Python
import json
import time
import requests

# Base URL of your FastAPI application
BASE_URL = "http://localhost:8000"

# Headers to be sent with every request (including user_id to satisfy the rate limiter)
HEADERS = {
    "Content-Type": "application/json",
    "X-User-Id": "400"
}

# Path to the contacts JSON file
CONTACTS_FILE = "/Users/frozo/PycharmProjects/external-contact-searcher/tests/data/data_without_embs.json"


def test_healthcheck():
    url = f"{BASE_URL}/healthcheck/health"
    response = requests.get(url, headers=HEADERS)
    print("Healthcheck response:", response.json())
    assert response.status_code == 200, f"Healthcheck failed with status {response.status_code}"
    assert response.json().get("status") == "healthy", "ElasticSearch is not healthy"


def load_contacts():
    with open(CONTACTS_FILE, "r") as f:
        contacts = json.load(f)
    return contacts


def test_upload_contacts(contacts):
    print("\nUploading contacts:")
    for contact in contacts:
        url = f"{BASE_URL}/contact/create"
        response = requests.post(url, json=contact, headers=HEADERS)
        print(f"Uploaded contact {contact['contactId']} - {contact['name']}: {response.json()}")
        time.sleep(0.1)  # Small delay between requests (optional)


def test_search_queries():
    print("\nTesting search queries:")
    test_cases = [
        ("make call with client", "Bob Johnson"),
        ("make reel in instagram", "Ivy Lee"),
        ("start marketing campaign", "Alice Smith"),
    ]
    for query, expected_name in test_cases:
        url = f"{BASE_URL}/contact/search"
        payload = {
            "user_id": "400",
            "query_text": query,
            "k": 5,
            "num_candidates": 100
        }
        response = requests.post(url, json=payload, headers=HEADERS)
        resp_json = response.json()
        print(f"Search query '{query}':", resp_json)
        assert response.status_code == 200, f"Search failed for query '{query}'"
        assert len(resp_json) > 0, f"No results returned for query '{query}'"
        first_contact = resp_json[0]
        assert first_contact.get("name") == expected_name, (
            f"Expected first contact name '{expected_name}', got '{first_contact.get('name')}'"
        )


def test_update_contacts(contacts):
    print("\nUpdating contacts:")
    for contact in contacts:
        # Modify the notesCombined field (or any other field to simulate an update)
        updated_contact = contact.copy()
        updated_contact["notesCombined"] = "Updated: " + updated_contact["notesCombined"]

        url = f"{BASE_URL}/contact/"
        response = requests.put(url, json=updated_contact, headers=HEADERS)
        print(f"Updated contact {contact['contactId']} - {contact['name']}: {response.json()}")
        assert response.status_code == 200, f"Update failed for contact {contact['contactId']}"
        assert "Contact updated successfully" in response.json().get("message", "")
        time.sleep(0.1)


def test_delete_contacts(contacts):
    print("\nDeleting contacts:")
    for contact in contacts:
        url = f"{BASE_URL}/contact/"
        payload = {
            "user_id": contact["userId"],
            "contactId": contact["contactId"]
        }
        response = requests.delete(url, json=payload, headers=HEADERS)
        print(f"Deleted contact {contact['contactId']} - {contact['name']}: {response.json()}")
        assert response.status_code == 200, f"Delete failed for contact {contact['contactId']}"
        time.sleep(0.1)


if __name__ == "__main__":
    print("Starting external tests...\n")
    test_healthcheck()
    contacts = load_contacts()
    test_upload_contacts(contacts)
    time.sleep(1)
    test_search_queries()
    test_update_contacts(contacts)
    test_delete_contacts(contacts)
    print("\nAll tests completed successfully.")
```

![8cb583f1-d072-45af-be28-dbb233ff402f](https://github.com/user-attachments/assets/02de50f2-d22d-4d58-a8eb-0fe92730c376)

![3d85b8cd-4f11-4055-8961-917ce6e064ee](https://github.com/user-attachments/assets/2866abd7-7605-40ea-8a80-ea87f9602b2c)

Таким образом, основная часть отчета подробно раскрывает, как работает поисковая система: от генерации эмбеддингов с помощью современных нейросетевых моделей до эффективного поиска в ElasticSearch, а также объясняет выбор технологий, архитектурные решения и методы тестирования.

**5\. Frontend (Vue.js 3)**:
![1b0aeaa4-7254-49be-afec-9871aaeb2fc8](https://github.com/user-attachments/assets/739fad50-4e2b-45d1-bff2-b9681df273fd)

Frontend часть приложения реализована с использованием **Vue.js 3**. Это позволяет создать динамичный интерфейс с поддержкой реактивности и удобной работы с данными. Для связи с бэкендом используется **Axios**, что позволяет отправлять запросы на сервер и получать данные в формате JSON.

Основные компоненты:

- **Компоненты для отображения данных**: Контакты, заметки, теги.
- **Форма поиска**: Обработка пользовательских запросов и отображение результатов поиска.
- **Модальные окна и формы для CRUD-операций**: Для добавления, редактирования и удаления данных.
- **Система уведомлений**: Для оповещений пользователя о статусе операций (успех/ошибка).


#### **5.1 Общая структура приложения**

Фронтенд-приложение организовано в виде модульной архитектуры с разделением на следующие основные директории:

- **src/components** – содержит переиспользуемые UI-компоненты (например, Header, Footer, ContactPreview, модальные окна и т.д.).
- **src/pages** – компоненты-страницы, каждая из которых отвечает за отдельный раздел приложения (HomeView, LoginPage, RegisterPage, MainPage, AddContactPage, NotFound и др.).
- **src/router** – настройка маршрутизации с использованием Vue Router для организации переходов между страницами.
- **src/store** – глобальное управление состоянием через Vuex, где хранятся данные аутентификации, список контактов и другие состояния приложения.
- **src/API** – слой для взаимодействия с бэкендом, реализованный с помощью Axios, который содержит классы для работы с различными эндпоинтами (AuthApi, UserApi, TagApi, NoteApi, ContactApi, QueryApi и HomeApi).
- **src/assets** – статические файлы (изображения, иконки, CSS и шрифты).

![a64462ea-91d5-4e15-8be9-e6a1654f1add](https://github.com/user-attachments/assets/53084932-8d99-40ae-8683-ff8243606745)

![821e4226-326a-485f-a591-4169d3d0557d](https://github.com/user-attachments/assets/7d733977-0753-4c0f-ae56-32104c70a3c4)

![9e30ef9a-b8fa-4c2e-bbab-c7334cff677b](https://github.com/user-attachments/assets/07fd8564-e420-419e-a3cd-56989c795352)

![5fd9c5e9-645b-4597-a005-f1b3537b6046](https://github.com/user-attachments/assets/6500b166-453f-434d-b4ac-004324c8af46)

![50b54d88-3355-46df-aa68-1436842c2723](https://github.com/user-attachments/assets/7351cef9-e170-4b72-a155-967043258fb7)

![e5bc79af-8cd6-4d74-81c9-c0709e5052bd](https://github.com/user-attachments/assets/07963e0a-3b74-43f6-84fe-12b15c34bfdc)

### **7\. Заключение и рекомендации**

В ходе реализации проекта была создана система для управления контактами, заметками и тегами с использованием векторных баз данных. Бэкенд, построенный на Java и Python, реализует функционал CRUD для работы с данными и генерирует эмбеддинги для поиска через ElasticSearch. Фронтенд с использованием Vue.js 3 позволяет пользователю удобно взаимодействовать с системой. Важным достижением является успешная интеграция нейросетевых моделей для генерации эмбеддингов и их дальнейшее использование в ElasticSearch для эффективного поиска по данным.

#### **7.2 Возможные улучшения и направления для дальнейшей разработки**

1. **Оптимизация поиска**: Улучшить точность и скорость поиска за счет использования более мощных нейросетевых моделей или внедрения дополнительных алгоритмов фильтрации.
2. **Интерфейс**: Добавить больше пользовательских функций, таких как группы контактов, расширенные фильтры и улучшенная визуализация данных.
3. **Мониторинг и логирование**: Реализовать систему мониторинга и логирования для отслеживания производительности и ошибок.
4. **Масштабируемость**: Рассмотреть возможность развертывания приложения в облаке с использованием масштабируемых решений для повышения производительности при большом объеме данных.
