# RecyclerView selection

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