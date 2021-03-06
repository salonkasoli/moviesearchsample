## TMDB movie search sample

Это игрушечное приложение, являющееся примером построения архитектуры. В качестве источника данных
используется АПИ от TMDB [(ссылка)](https://developers.themoviedb.org/3/).

Библиотеки, использованные в проекте:
*  [Dagger2](https://github.com/google/dagger) для dependency injection
*  [RxJava](https://github.com/ReactiveX/RxJava) для работы в фоне
*  Реализация [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) от гугла.
*  [Retrofit2](https://github.com/square/retrofit) для запросов к АПИ.
*  [Fresco](https://github.com/facebook/fresco) загрузки и отображения картинок.

В проекте используется архитектура **MVVM**. Основные элементы архитектуры:  
*  *Repository* - элемент, отвечающий за загрузку данных.
*  *ViewModel* - дергает репозитории, проставляет результат их работы в *LiveData*.
*  *Widget* - глупая абстракция над view. Предоставляет контракт "что показать", а сама решает "как показать".
*  *Activity* - точка входа в функционал. Подписывает *Widget* на *ViewModel*. Реагирует на события *Widget*, вызывает соответствующие методы *ViewModel*.

**Архитектура DI**

*AppComponent* это основной компонент в приложении. Создает и хранит все глобальные зависимости.  
Для каждого экрана существует *ScreenNameComponent*, являющийся сабкомпонентом *AppComponent*.
Активити получает основной компонент с помощью сервис локатора *App.instance.appComponent*,
а затем создает свой компонент с помощью фабрики сабкомпонента. Зависимости получает так:
```kotlin
val widget = App.instance.appComponent.movieSearchComponent()
    .create(MovieSearchUiModule(this))
    .widget()
```

**Проблемы, пришедшие с ViewModel**

К сожалению, [официальное решение](https://developer.android.com/topic/libraries/architecture/viewmodel-savedstate)
по сохранению состоянию ViewModel меня не устроило т.к. оно не позволяет делать кастомные фабрики.

**TODO**
*  В DI вместо сабкомпонентов заюзать обычные компоненты
*  Получше разрулить конфигурацию API и Жанров (см. GenresRepository)
*  Перенести обработку ввода в SearchView на RxJava