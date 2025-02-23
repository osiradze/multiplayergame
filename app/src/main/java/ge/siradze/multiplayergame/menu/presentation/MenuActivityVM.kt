package ge.siradze.multiplayergame.menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.siradze.multiplayergame.core.ResultFace
import ge.siradze.multiplayergame.core.network.BaseUrlProvider
import ge.siradze.multiplayergame.menu.domain.model.Server
import ge.siradze.multiplayergame.menu.domain.usecases.GetServersUseCase
import ge.siradze.multiplayergame.menu.domain.usecases.HostGameUseCase
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

    private val _state: MutableStateFlow<MenuState> = MutableStateFlow(
        MenuState.Main(baseUrlProvider.get())
    )
    val state = _state.asStateFlow()


    private val _effect: MutableSharedFlow<MenuEffect> = MutableSharedFlow()
    val effect = _effect.asSharedFlow()


    fun event(event: MenuEvent) {
        when(event) {
            is MenuEvent.IpChanged -> setIp(event.ip)
            is MenuEvent.HostClicked -> host(event.name)
            MenuEvent.JoinClicked -> join()
            MenuEvent.OnBackPress -> backPress()
            MenuEvent.PlayClicked -> play()
        }
    }

    private fun setIp(ip: String) {
        baseUrlProvider.set(ip)
        _state.value = MenuState.Main(baseUrlProvider.get())
    }

    private fun host(name: String) = viewModelScope.launch {
        when (val result = hostGameUseCase.invoke(name)) {
            is ResultFace.Failure -> {
                _effect.emit(MenuEffect.ShowToast(result.error))
            }
            is ResultFace.Success -> {
                _effect.emit(MenuEffect.StartGame(result.value.port))
            }
        }

    }

    private fun join() = viewModelScope.launch {
        when (val result = getServersUseCase.invoke()) {
            is ResultFace.Failure -> {
                _effect.emit(MenuEffect.ShowToast(result.error))
            }
            is ResultFace.Success -> {
                _state.value = MenuState.Servers(result.value)
            }
        }
    }

    private fun play() = viewModelScope.launch  {
        _effect.emit(MenuEffect.StartGame(null))
    }

    private fun backPress() = viewModelScope.launch {
        if(_state.value is MenuState.Servers) {
            _state.value = MenuState.Main(baseUrlProvider.get())
        } else {
            _effect.emit(MenuEffect.Finish)
        }
    }




    sealed class MenuState {
        data class Main(val ip: String) : MenuState()
        data class Servers(val servers: List<Server>) : MenuState()
    }

    sealed class MenuEvent {
        data class IpChanged(val ip: String) : MenuEvent()
        data class HostClicked(val name: String) : MenuEvent()
        data object JoinClicked : MenuEvent()
        data object PlayClicked : MenuEvent()
        data object OnBackPress : MenuEvent()
    }

    sealed class MenuEffect {
        data class StartGame(val port: Int?) : MenuEffect()
        data class ShowToast(val message: String) : MenuEffect()
        data object Finish : MenuEffect()
    }

}