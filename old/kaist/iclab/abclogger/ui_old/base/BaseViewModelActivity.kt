package old.kaist.iclab.abclogger.ui_old.base

import androidx.viewbinding.ViewBinding

abstract class BaseViewModelActivity<T : ViewBinding, VM : BaseViewModel> : BaseActivity<T>() {
    abstract val viewModel: VM
}