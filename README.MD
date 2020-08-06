## TMDB movie search sample

Это игрушечное приложение, являющееся примером построения архитектуры. В качестве источника данных
используется АПИ от TMDB [(ссылка)](https://developers.themoviedb.org/3/).

Библиотеки, использованные в проекте:
*  [Dagger2](https://github.com/google/dagger) для dependency injection
*  [Retrofit2](https://github.com/square/retrofit) для запросов к АПИ.
*  Coroutines + Architecture components для работы в фоне [(ссылка)](https://developer.android.com/topic/libraries/architecture/coroutines).
*  [Fresco](https://github.com/facebook/fresco) загрузки и отображения картинок.

Основные элементы архитектуры:
*  *Repository* - элемент, отвечающий за загрузку данных.
*  *Interactor* отвечает за вызов методов репозитория на корректном треде. Предоставляет наружу контракт для подписки на результат.
*  *Widget* - глупая абстракция над view. Предоставляет контракт "что показать", а сама решает "как показать".
*  *Controller* связывает виджет с интерактором. Реагирует на события виджета и говорит ему, как показать загруженные данные.

**Архитектура DI**

*AppComponent* это основной компонент в приложении. Создает и хранит все глобальные зависимости.  
Для каждого экрана существует *ScreenNameComponent*, являющийся сабкомпонентом *AppComponent*.
Активити получает основной компонент с помощью сервис локатора *App.instance.appComponent*,
а затем создает свой компонент с помощью фабрики сабкомпонента, у которого можно получить зависимости:
```kotlin
controller = App.instance.appComponent.movieSearchComponent()
    .create(MovieSearchUiModule(this))
    .controller()
```

**TODO**

*  Попробовать RxJava2
*  Попробовать ViewModel