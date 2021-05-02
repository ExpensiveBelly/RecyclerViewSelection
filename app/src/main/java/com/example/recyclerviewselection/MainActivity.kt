package com.example.recyclerviewselection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainActivity : AppCompatActivity() {

    private val selectionSubject = BehaviorSubject.create<List<String>>()

    private var disposable = Disposable.empty()

    private val button by lazy { findViewById<Button>(R.id.button) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById<MaterialToolbar>(R.id.action_bar))

        val itemsAdapter = ItemsAdapter(
            recyclerView = { recyclerView },
            itemClickListener = { item -> toast("$item clicked!") },
            onSelectionChangedListener = { selection ->
                when {
                    selection.isEmpty -> {
                        finishActionMode()
                    }
                    !selection.isEmpty -> {
                        if (actionMode == null) {
                            actionMode = createAndStartActionMode(this)
                        }
                        actionMode?.title = selection.size().toString()
                    }
                }
                button.isEnabled = !selection.isEmpty
                selectionSubject.onNext(selection.toList())
            }
        )
        recyclerView.adapter = itemsAdapter

        itemsAdapter.submitList(createFakeItems())
    }

    private fun finishActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    override fun onStart() {
        super.onStart()
        disposable = button.clicks().switchMapSingle { selectionSubject.firstOrError() }
            .subscribe {
                toast("$it selected!")
                finishActionMode()
            }
    }

    override fun onStop() {
        disposable.dispose()
        super.onStop()
    }

    private fun createAndStartActionMode(selectionTracker: SelectionTracker<String>) =
        startSupportActionMode(
            object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode, menu: Menu) = true
                override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = true
                override fun onActionItemClicked(
                    mode: ActionMode?,
                    item: MenuItem?
                ) = true

                override fun onDestroyActionMode(mode: ActionMode) {
                    selectionTracker.clearSelection()
                }
            }
        )
}

private fun createFakeItems() = listOf(
    Item("1", "Finder", "Find files in your Mac"),
    Item("2", "Calendar", "Add events"),
    Item("3", "Microsoft Teams", "Chat with your teammates"),
    Item("4", "Google Chrome", "Browser"),
    Item("5", "Launchpad", "Run your apps"),
    Item("6", "Evernote", "Take notes"),
    Item("7", "Android Studio", "Develop Android apps"),
    Item("8", "Settings", "Change the Settings in your computer"),
    Item("9", "Sublime Text", "Text editor"),
    Item("10", "Console", "Shell")
)