# RecyclerView selection

![Snip20210503_5](https://user-images.githubusercontent.com/6824465/116916959-eafb7100-ac45-11eb-902f-d10571fdc380.png)

### Features:

1. recyclerview-selection using String as a way to identify items (this allows mutability in the list as opposed to using position of the item in the list. Position of an item might change when the list mutates)
2. ActionMode is displayed when items are activated.
3. Activation of an item happens when long-press and also when the delete icon is clicked (due to implementation of `inSelectionHotspot` to activate when a specific item is clicked)

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
user knows that the next click will be used for activating a non-item
