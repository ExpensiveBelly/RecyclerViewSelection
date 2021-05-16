package com.example.recyclerviewselection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
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

    private val itemsAdapter: ItemsAdapter by lazy {
        ItemsAdapter(
            recyclerView = { recyclerView },
            itemClickListener = { item -> toast("$item clicked!") },
            onSelectionChangedListener = { selection ->
                when {
                    selection.isEmpty -> {
                        finishActionMode()
                        itemsAdapter.notifyItemsWithPredicate()
                    }
                    !selection.isEmpty -> {
                        if (actionMode == null) {
                            actionMode = createAndStartActionMode(this)
                            itemsAdapter.notifyItemsWithPredicate()
                        }
                        actionMode?.title = selection.size().toString()
                    }
                }
                button.isEnabled = !selection.isEmpty
                selectionSubject.onNext(selection.toList())
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById<MaterialToolbar>(R.id.action_bar))


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
    Item("0", "Finder", "Find files in your Mac"),
    Item("1", "Calendar", "Add events"),
    Item("2", "Microsoft Teams", "Chat with your teammates"),
    Item("3", "Google Chrome", "Browser"),
    Item("10", "Microsoft Messenger", "Chat", true),
    Item("4", "Launchpad", "Run your apps"),
    Item("5", "Evernote", "Take notes"),
    Item("6", "Android Studio", "Develop Android apps"),
    Item("7", "Settings", "Change the Settings in your computer"),
    Item("8", "Sublime Text", "Text editor"),
    Item("9", "Console", "Shell"),
    Item("10", "Skype", "Video-Chat", true)
)