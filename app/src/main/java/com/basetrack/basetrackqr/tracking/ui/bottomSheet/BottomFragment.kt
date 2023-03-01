package com.basetrack.basetrackqr.tracking.ui.bottomSheet

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.basetrack.basetrackqr.R
import com.basetrack.basetrackqr.databinding.BottomSheetLayoutBinding
import com.basetrack.basetrackqr.qrScan.ui.utils.adapter.BottomFragmentAdapter
import com.basetrack.basetrackqr.tracking.ui.tracking.TrackingFragment.Companion.qrList
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


private const val COLLAPSED_HEIGHT = 138

class BottomFragment : BottomSheetDialogFragment() {

    // Можно обойтись без биндинга и использовать findViewById
    lateinit var binding: BottomSheetLayoutBinding

    private val viewModel: BottomViewModel by viewModels()

    private lateinit var adapter: BottomFragmentAdapter

    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    private var qrListIsEmpty = false
    // Переопределим тему, чтобы использовать нашу с закруглёнными углами
    override fun getTheme() = R.style.AppBottomSheetDialogTheme


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            BottomSheetLayoutBinding.bind(inflater.inflate(R.layout.bottom_sheet_layout, container))


        //добавляем элементы в адаптер


        showBottomSheet()
        return binding.root
    }

    // Я выбрал этот метод ЖЦ, и считаю, что это удачное место
    // Вы можете попробовать производить эти действия не в этом методе ЖЦ, а например в onCreateDialog()
    override fun onStart() {
        super.onStart()

        val oclBtnOk: View.OnClickListener = View.OnClickListener {
            closeBottomSheet()
        }

        // Плотность понадобится нам в дальнейшем
        val density = requireContext().resources.displayMetrics.density

        dialog?.let {

            // Находим сам bottomSheet и достаём из него Behaviour
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            behavior = BottomSheetBehavior.from(bottomSheet)

            // Выставляем высоту для состояния collapsed и выставляем состояние collapsed
            behavior.peekHeight = (COLLAPSED_HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED


            val coordinator =
                (it as BottomSheetDialog).findViewById<CoordinatorLayout>(com.google.android.material.R.id.coordinator)
            val containerLayout =
                it.findViewById<FrameLayout>(com.google.android.material.R.id.container)

            // Надуваем наш лэйаут с кнопкой

            val buttons = it.layoutInflater.inflate(R.layout.button, null)

            buttons.findViewById<Button>(R.id.button_a).setOnClickListener(oclBtnOk)

            // Выставляем параметры  кнопки
            buttons.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                height = (60 * density).toInt()
                gravity = Gravity.BOTTOM
            }

            // Добавляем кнопку в контейнер
            containerLayout?.addView(buttons)
            // Перерисовываем лэйаут
            buttons.post {
                (coordinator?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    buttons.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                    // Устраняем разрыв между кнопкой и скролящейся частью
                    this.bottomMargin = (buttons.measuredHeight - 8 * density).toInt()
                    containerLayout?.requestLayout()
                }
            }



            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // Нам не нужны действия по этому колбеку
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    with(binding) {
                        if (!qrListIsEmpty) {
                            // Нас интересует только положительный оффсет, тк при отрицательном нас устроит стандартное поведение - скрытие фрагмента
                            if (slideOffset > 0) {
                                // Делаем "свёрнутый" layout более прозрачным
                                layoutCollapsed.alpha = 1 - 2 * slideOffset
                                // И в то же время делаем "расширенный layout" менее прозрачным
                                layoutExpanded.alpha = slideOffset * slideOffset

                                // Когда оффсет превышает половину, мы скрываем collapsed layout и делаем видимым expanded
                                if (slideOffset > 0.5) {
                                    //  layoutCollapsed.visibility = View.GONE
                                    layoutExpanded.visibility = View.VISIBLE
                                }

                                // Если же оффсет меньше половины, а expanded layout всё ещё виден, то нужно скрывать его и показывать collapsed
                                if (slideOffset < 0.5 && binding.layoutExpanded.visibility == View.VISIBLE) {
                                    // layoutCollapsed.visibility = View.VISIBLE
                                    layoutExpanded.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun closeBottomSheet() {
        behavior.isHideable = true
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showBottomSheet() {
            if (qrList.isNotEmpty()) {
                viewModel.getAllQrResult(qrList)
                if(viewModel.qrNotFoundString.isNotEmpty()){
                    Toast.makeText(requireContext(), "Kits not found:\n${viewModel.qrNotFoundString}", Toast.LENGTH_LONG).show()
                }
                if (viewModel.qrToDisplay.isNotEmpty()) {
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    adapter = BottomFragmentAdapter(viewModel.qrToDisplay)
                    binding.recyclerView.adapter = adapter
                } else {
                    qrListIsEmpty()
                }
            } else {
                qrListIsEmpty()
            }
    }

    private fun qrListIsEmpty() {
        binding.emptyList.setText(R.string.empty_complect)
        qrListIsEmpty = true
    }
}