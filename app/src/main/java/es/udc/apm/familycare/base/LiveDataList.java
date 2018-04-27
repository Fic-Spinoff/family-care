package es.udc.apm.familycare.base;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gonzalo on 26/04/2018.
 */

public class LiveDataList<T> {

    public enum Event {
        ADDED, REMOVED, UPDATED
    }

    public interface ListObserver<T> {
        void onChanged(@Nullable List<T> t);
        void onUpdated(@NonNull Event event, int index, @Nullable T t);
    }

    private class InnerObserver {
        LifecycleOwner owner;
        ListObserver<T> observer;

        InnerObserver(LifecycleOwner owner, ListObserver<T> observer) {
            this.owner = owner;
            this.observer = observer;
        }
    }

    private List<T> mList;
    private List<InnerObserver> mObservers;

    public LiveDataList() {
        this(new ArrayList<>());
    }

    public LiveDataList(List<T> mList) {
        this.mList = mList;
        this.mObservers = new LinkedList<>();
    }

    public void postValue(List<T> value) {
        this.mList = value;
    }

    public void setValue(List<T> value) {
        this.mList = value;
        notifyObservers(value);
    }

    public void addValue(T itemValue) {
        int index = this.mList.add(itemValue) ? this.mList.size() - 1 : -1;
        notifyObservers(Event.ADDED, index, itemValue);
    }

    public void removeValue(T itemValue) {
        int index = this.mList.indexOf(itemValue);
        if(index != -1) {
            this.mList.remove(index);
        }
        notifyObservers(Event.REMOVED, index, itemValue);
    }

    public void updateValue(T itemValue) {
        int index = this.mList.indexOf(itemValue);
        if(index != -1) {
            this.mList.set(index, itemValue);
        }
        notifyObservers(Event.UPDATED, index, itemValue);
    }

    @Nullable
    public List<T> getValue() {
        return this.mList;
    }

    private void notifyObservers(List<T> value) {
        for (InnerObserver inObserver : this.mObservers) {
            if (inObserver.owner.getLifecycle().getCurrentState()
                    .isAtLeast(Lifecycle.State.STARTED)) {
                inObserver.observer.onChanged(value);
            }
        }
    }

    private void notifyObservers(Event event, int index, T value) {
        for (InnerObserver inObserver : this.mObservers) {
            if (inObserver.owner.getLifecycle().getCurrentState()
                    .isAtLeast(Lifecycle.State.STARTED)) {
                inObserver.observer.onUpdated(event, index, value);
            }
        }
    }

    public void observe(@NonNull LifecycleOwner owner, @NonNull ListObserver<T> observer) {
        this.mObservers.add(new InnerObserver(owner, observer));
        observer.onChanged(mList); // Send
    }

    public void removeObserver(@NonNull ListObserver<T>  observer) {
        Iterator<InnerObserver> it = this.mObservers.iterator();
        while(it.hasNext()) {
            InnerObserver inObserver = it.next();
            if(inObserver.observer == null || observer.equals(inObserver.observer)) {
                it.remove();
            }
        }
    }

    public void removeObservers(@NonNull LifecycleOwner owner) {
        Iterator<InnerObserver> it = this.mObservers.iterator();
        while(it.hasNext()) {
            InnerObserver inObserver = it.next();
            if(inObserver.owner == null || owner.equals(inObserver.owner)) {
                it.remove();
            }
        }
    }

    public boolean hasObservers() {
        return this.mObservers.size() > 0;
    }

/*    protected void onActive() {
        super.onActive();
    }

    protected void onInactive() {
        super.onInactive();
    }

    public boolean hasActiveObservers() {
        return super.hasActiveObservers();
    }*/
}
