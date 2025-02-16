package ge.siradze.mutiplayergame.core.network

import org.koin.dsl.module


val networkModule = module {
    single { provideHttpClient() }
    single { provideConverterFactory() }

    single { provideRetrofit(get(),get()) }
    single { provideServerService(get()) }
    single<BaseUrlProvider> { BaseUrlProviderImpl() }

}
