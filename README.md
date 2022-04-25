### Geckoin
Geckoin is a cryptocurrency simple app for basic market info, top coins list and search in all available tokens by consuming "CoinGecko" API.
In this sample project I tried to develop a standard and modern android app with best practices and some the most useful jetpack components.  

<p float="left">
<img src="screenshots/1_dark_screen_home.jpg" width="30%"/>
<img src="screenshots/2_dark_screen_list.jpg" width="30%"/>
<img src="screenshots/3_dark_screen_search.jpg" width="30%"/>
</p>


### Features
* Kotlin
* Clean Architecture: Keep In mind, clean architecture starts from domain layer by implementing business logic(data models and abstract repository interfaces). so domain layer says to data layer what it should do and also domain layer provides proper data for UI layer.
* Offline-First with Repository Pattern: This pattern act on data layer. Repository Pattern Wraps around those data that provide by "BOTH network and Database". If your data provides only by network or only by database you can omit repository and do relavant tasks in UseCase.
* MVVM Design Pattern: this design act on Presentation layer, no problem with configuration changes :)
* View Binding: I prefered viewbinding over databinding because of code rigidity and an easeir path to migrate to jetpack compose.
* Kotlin Coroutines and Kotlin Flow for concurrency, observer patten and data streaming.
* Room and DataStore For Data Persistence and caching. 
* Dagger Hilt: for Dependency injection. 
* WorkManager: for background task and syncing coins database.
* Paging 3.0: for pagination and endless list. 
* Dark Mode and Material design
* Network Response Adapter for handling and wrapping different api response types (https://github.com/haroldadmin/NetworkResponseAdapter).
* CoinGecko Free Api: All data provided by coingecko public api (https://www.coingecko.com/en)

***video shot link***: https://youtube.com/shorts/pl28XA9hnvE?feature=share

<!-- [![work doesn't happen at work](http://i.imgur.com/ASYsjjX.png)](https://youtube.com/shorts/pl28XA9hnvE "Geckoin Preview - Click to Watch!")
https://youtu.be/pl28XA9hnvE?t=2 -->

