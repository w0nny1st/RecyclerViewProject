package com.example.recyclerviewproject.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewproject.R
import com.example.recyclerviewproject.adapter.MultiTypeAdapter
import com.example.recyclerviewproject.databinding.FragmentListBinding
import com.example.recyclerviewproject.model.ListItem
import com.example.recyclerviewproject.utils.RecyclerViewItemTouchHelper
import com.example.recyclerviewproject.viewmodel.ListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ListViewModel by viewModels()
    private lateinit var adapter: MultiTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        setupObservers()
        setupSwipeAndDrag()
    }

    private fun setupRecyclerView() {
        adapter = MultiTypeAdapter(
            onItemClick = { item ->
                showItemInfo(item)
            },
            onItemLongClick = { item ->
                showContextMenu(item)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutAnimation = android.view.animation.LayoutAnimationController(
            android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.item_animation_fall_down
            ),
            0.1f
        ).apply {
            order = android.view.animation.LayoutAnimationController.ORDER_NORMAL
        }

        val divider = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.purple_200))
        val itemDecoration = object : androidx.recyclerview.widget.DividerItemDecoration(
            requireContext(),
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
        ) {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: androidx.recyclerview.widget.RecyclerView,
                state: androidx.recyclerview.widget.RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(16, 8, 16, 8)
            }
        }
        binding.recyclerView.addItemDecoration(itemDecoration)
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun setupObservers() {
        viewModel.items.observe(viewLifecycleOwner, Observer { items ->
            adapter.submitList(items)
        })

        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            if (message.isNotEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.clearToastMessage()
            }
        })

        viewModel.showUndoSnackbar.observe(viewLifecycleOwner, Observer { deletedItem ->
            if (deletedItem != null) {
                showUndoSnackbar(deletedItem)
            }
        })
    }

    private fun setupSwipeAndDrag() {
        val touchHelper = RecyclerViewItemTouchHelper(
            adapter = adapter,
            onItemDeleted = { position, item ->
                viewModel.removeItem(position)
            },
            onItemMoved = { fromPosition, toPosition ->
                viewModel.moveItem(fromPosition, toPosition)
            }
        )

        val itemTouchHelper = ItemTouchHelper(touchHelper)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun showAddItemDialog() {
        val types = arrayOf("Тип 1 (с иконкой)", "Тип 2 (товар)")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Добавить новый элемент")
            .setItems(types) { _, which ->
                viewModel.addItem(which)

                val currentItems = adapter.itemCount
                if (currentItems > 0) {
                    binding.recyclerView.smoothScrollToPosition(currentItems - 1)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showItemInfo(item: ListItem) {
        val message = when (item) {
            is ListItem.Header -> "Заголовок: ${item.title}"
            is ListItem.ItemType1 -> "Элемент: ${item.title}\n${item.description}"
            is ListItem.ItemType2 -> "Товар: ${item.name}\nЦена: ${item.price}\nРейтинг: ${item.rating}"
        }

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun showContextMenu(item: ListItem) {
        val options = when (item) {
            is ListItem.Header -> arrayOf("Изменить заголовок", "Удалить раздел")
            is ListItem.ItemType1 -> arrayOf("Редактировать", "Удалить", "Дублировать")
            is ListItem.ItemType2 -> arrayOf("Редактировать", "Удалить", "В избранное", "Дублировать")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Действия")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditDialog(item)
                    1 -> {
                        val position = adapter.findPosition(item)
                        if (position != -1) {
                            viewModel.removeItem(position)
                        }
                    }
                    2 -> {
                        if (item is ListItem.ItemType2) {
                            Toast.makeText(requireContext(), "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.duplicateItem(item)
                        }
                    }
                    3 -> viewModel.duplicateItem(item)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showEditDialog(item: ListItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Редактирование")
            .setMessage("Редактирование элемента: ${getItemTitle(item)}")
            .setPositiveButton("Сохранить") { _, _ ->
                Toast.makeText(requireContext(), "Изменения сохранены", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun getItemTitle(item: ListItem): String {
        return when (item) {
            is ListItem.Header -> item.title
            is ListItem.ItemType1 -> item.title
            is ListItem.ItemType2 -> item.name
        }
    }

    private fun showUndoSnackbar(deletedItem: Pair<Int, ListItem>) {
        val snackbar = Snackbar.make(
            binding.root,
            "Элемент удален",
            Snackbar.LENGTH_LONG
        )
            .setAction("ОТМЕНИТЬ") {
                viewModel.undoDelete()
            }
            .setActionTextColor(Color.YELLOW)

        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event != DISMISS_EVENT_ACTION) {
                    viewModel.clearSnackbar()
                }
            }
        })

        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}