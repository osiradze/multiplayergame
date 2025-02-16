package ge.siradze.mutiplayergame.menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.core.network.BaseUrlProvider
import ge.siradze.mutiplayergame.menu.domain.model.Server
import ge.siradze.mutiplayergame.menu.domain.usecases.GetServersUseCase
import ge.siradze.mutiplayergame.menu.domain.usecases.HostGameUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MenuActivityVM(
    private val baseUrlProvider: BaseUrlProvider,
    private val getServersUseCase: GetServersUseCase,
    private val hostGameUseCase: HostGameUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<MenuActivityState> = MutableStateFlow(
        MenuActivityState.Main(baseUrlProvider.get())
    )
    val state = _state.asStateFlow()


    private val _effect: MutableSharedFlow<MenuEffect> = MutableSharedFlow()
    val effect = _effect.asSharedFlow()


    fun event(event: MenuEvent) {
        when(event) {
            is MenuEvent.IpChanged -> setIp(event.ip)
            MenuEvent.HostClicked -> host()
            MenuEvent.JoinClicked -> join()
            MenuEvent.OnBackPress -> backPress()
        }
    }

    private fun setIp(ip: String) {
        baseUrlProvider.set(ip)
        _state.value = MenuActivityState.Main(baseUrlProvider.get())
    }

    private fun host() = viewModelScope.launch {
        val result = hostGameUseCase.invoke()
        when (result) {
            is ResultFace.Error -> {
                _effect.emit(MenuEffect.ShowToast(result.error.toString()))
            }
            is ResultFace.Success -> {
                _effect.emit(MenuEffect.StartGame(result.value))
            }
        }

    }

    private fun join() = viewModelScope.launch {
        when (val result = getServersUseCase.invoke()) {
            is ResultFace.Error -> {
                _effect.emit(MenuEffect.ShowToast(result.error))
            }
            is ResultFace.Success -> {
                _state.value = MenuActivityState.Servers(result.value)
            }
        }
    }

    private fun backPress() = viewModelScope.launch {
        if(_state.value is MenuActivityState.Servers) {
            _state.value = MenuActivityState.Main(baseUrlProvider.get())
        } else {
            _effect.emit(MenuEffect.Finish)
        }
    }




    sealed class MenuActivityState {
        data class Main(val ip: String) : MenuActivityState()
        data class Servers(val servers: List<Server>) : MenuActivityState()
    }

    sealed class MenuEvent {
        data class IpChanged(val ip: String) : MenuEvent()
        data object HostClicked : MenuEvent()
        data object JoinClicked : MenuEvent()
        data object OnBackPress : MenuEvent()
    }

    sealed class MenuEffect {
        data class StartGame(val port: Int) : MenuEffect()
        data class ShowToast(val message: String) : MenuEffect()
        data object Finish : MenuEffect()
    }

}