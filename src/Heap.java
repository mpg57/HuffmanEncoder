// Heap using an array list
// I used an ArrayList implementation instead of a LinkedList implementation because ArrayList
// uses an array data structure which maintains an index based system for its elements. This
// makes it faster to search an element in the list.

import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {

    private ArrayList<T> list = new ArrayList<>();

    // creates a heap
    public Heap() {}

    // creates a heap from array list
    public Heap(T[] items) {
        for (int i = 0; i < items.length; i++)
            insert(items[i]);
    }

    public void insert(T item) {
        list.add(item);
        int lastIndex = list.size() - 1;

        while (lastIndex > 0) {
            int parentIndex = (lastIndex - 1) / 2;
            // switch last index object if it is greater than its parent
            if (list.get(lastIndex).compareTo(list.get(parentIndex)) > 0) {
                T temp = list.get(lastIndex);
                list.set(lastIndex, list.get(parentIndex));
                list.set(parentIndex, temp);
            } else
                break; // the tree is a heap
            lastIndex = parentIndex;
        }
    }

    public T delete() {
        if (list.size() == 0) return null;

        T itemDeleted = list.get(0);
        list.set(0, list.get(list.size() - 1));
        list.remove(list.size() - 1);

        int index = 0;
        while (index < list.size()) {
            int leftIndex = 2 * index + 1;
            int rightIndex = 2 * index + 2;

            // is left or right child bigger?
            if (leftIndex >= list.size()) break;
            int maxIndex = leftIndex;
            if (rightIndex < list.size()) {
                if (list.get(maxIndex).compareTo(list.get(rightIndex)) < 0) {
                    maxIndex = rightIndex;
                }
            }

            // switch nodes if index node is less than max node
            if (list.get(index).compareTo(list.get(maxIndex)) < 0) {
                T temp = list.get(maxIndex);
                list.set(maxIndex, list.get(index));
                list.set(index, temp);
            } else
                break;
        }
        return itemDeleted;
    }

    public int getSize() {
        return list.size();
    }
}