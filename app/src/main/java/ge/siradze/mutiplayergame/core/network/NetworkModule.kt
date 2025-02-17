package ge.siradze.mutiplayergame.core.network

import ge.siradze.mutiplayergame.core.network.interceptors.DynamicUrlInterceptor
import org.koin.dsl.module


val networkModule = module {
    single<BaseUrlProvider> { BaseUrlProviderImpl() }
    single { DynamicUrlInterceptor(get()) }
    single { provideHttpClient(
        get()
    ) }
    single { provideConverterFactory() }

    single { provideRetrofit(get(),get()) }
    single { provideServerService(get()) }

}
