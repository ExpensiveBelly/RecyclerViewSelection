[![Build](https://github.com/ExpensiveBelly/RecyclerViewSelection/actions/workflows/android-github-actions-build.yml/badge.svg)](https://github.com/ExpensiveBelly/RecyclerViewSelection/actions/workflows/android-github-actions-build.yml)

# RecyclerView selection

![Snip20210504_5](https://user-images.githubusercontent.com/6824465/116990253-c39ab780-acca-11eb-9f20-eb0f985b529c.png)
![Snip20210504_4](https://user-images.githubusercontent.com/6824465/116990264-c4cbe480-acca-11eb-905c-09fcdce3bc45.png)


### Features:

1. recyclerview-selection using String as a way to identify items (this allows mutability in the list as opposed to using position of the item in the list. Position of an item might change when the list mutates)
2. ActionMode is displayed when items are activated.
3. Activation of an item happens when long-press and also when the delete icon is clicked (due to implementation of `inSelectionHotspot` to activate when a specific item is clicked)
4. Only certain items that meet the criteria get activated, others don't and those get "disabled" by making them less visible when ActionMode gets activated (`SelectionPredicate`)
5. If the list mutates while the user is in ActionMode the amount of selected items is updated and if there are none selected anymore then the ActionMode finishes.
6. Support for multiple ViewTypes, which behave differently upon click.

Useful links:

- https://androidkt.com/recyclerview-selection-28-0-0/
- https://proandroiddev.com/a-guide-to-recyclerview-selection-3ed9f2381504


### FAQ

* Why `inSelectionHotspot(e)` is called ONLY if there is no items activated?

Because of this code in the library:
```
// Touch events select if they occur in the selection hotspot,
// otherwise they activate.
return item.inSelectionHotspot(e)
    ? selectItem(item)
    : mOnItemActivatedListener.onItemActivated(item, e);
```

Using this library it makes sense to startActionMode once the first item has been activated, so the
user knows that the next click will be used for activating a non-activated item or to deselect an already activated item.
