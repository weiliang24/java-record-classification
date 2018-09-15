import java.io.Serializable;
import java.util.*;

/**
 * 收获
 * 1：学习了数组拷贝和扩容
 * 2：学习了编写方法时注意事项
 * Created by muweiliang on 2018/9/15.
 */
public class MyArrayList<E> extends AbstractList<E>
               implements List<E>, RandomAccess, Cloneable,Serializable{

    private static final long serialVersionUID = 8630639129407227713L;

    private static final int DEFAULT_CAPACITY = 10;

    private static final Object[] EMPTY_ELEMENTDATA = {};   //区别

    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {}; //仅仅构造方法中入参为空时使用该值

    transient Object[] elementData;

    private int size;

    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0 ) {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            this.elementData = new Object[initialCapacity];
        }
    }

    public  MyArrayList() {  this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA; }

    public MyArrayList (Collection<? extends E> c ) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            if (elementData.getClass() != Object.class) {
                elementData =   Arrays.copyOf(elementData, size, Object[].class);
            }
        } else {
            elementData = EMPTY_ELEMENTDATA;
        }
    }
        /**
         * 修剪多余动态扩容时申请的内存(ex: size= 1000, length内存为1200
         *  将多余200剔除掉，内存较紧张时使用)
         */
    public void trimToSize() {
       modCount++;
        if (size < elementData.length) {
             elementData = (size == 0 )?
                     EMPTY_ELEMENTDATA :
                     Arrays.copyOf(elementData,size);
        }
    }

    /**
     * 确保容量手动扩容
     * @param minCapacity
     */
   public void ensureCapacity(int minCapacity) {
       int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) ? 0 : DEFAULT_CAPACITY;//个人理解为避免输入的值如果小于10
       if (minExpand < minCapacity)  {
           ensureExplicitCapacity(minCapacity);
       }
   }

    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)  {
            minCapacity = Math.max(DEFAULT_CAPACITY,minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }

    private void ensureExplicitCapacity(int minCapacity) {
       modCount++;
        if (minCapacity - elementData.length > 0) {
            //操作
        }
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 扩容操作
     * @param minCapacity
     */
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1); //优先级
        if (newCapacity - minCapacity < 0 ) {
            newCapacity = minCapacity;
        }
        if ((newCapacity - MAX_ARRAY_SIZE) > 0) {
            newCapacity =  hugeCapacity(minCapacity);
        }
        elementData = Arrays.copyOf(elementData,newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return minCapacity > MAX_ARRAY_SIZE ?
                Integer.MAX_VALUE:
                MAX_ARRAY_SIZE;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public  boolean contains(Object o) {return indexOf(o) > 0;}
    public int indexOf(Object o) {
        if (o == null) {
              for (int i =0; i < size; i++) {
                  if (elementData[i] == null) {
                      return i;
                  }
              }
        } else {
            for (int i =0; i < size; i++) {
                if (o.equals(elementData[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
      if (o == null ) {
          for (int i = size - 1 ; i > 0;i--) {
              if (elementData[i] == null) {
                  return i;
              }
          }
      } else {
          for (int i = size - 1 ; i > 0;i--) {
              if (o.equals(elementData[i])) {
                  return i;
              }
          }
      }
      return -1;
    }

    public Object clone() {
        try{
            MyArrayList<?> v = (MyArrayList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    E elementData(int index) {return (E)elementData[index];}

    public E get(int index) {
        rangeCheck(index);
        return elementData(index);
    }



    public E set (int index,E element) {
        rangeCheck(index);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    public boolean add(E e) {
        ensureCapacityInternal(size+1);
        elementData[size++] = e;
        return true;
    }
    public void add (int index, E e) {
        rangeCheckForAdd(index);
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                size - index);
        elementData[index] = e;
        size++;
    }

    /**
     *
     * @param index
     * @return
     */
    public E remove(int index) {
        rangeCheck(index);
        modCount++;
        E oldValue = elementData(index);
        int numMoved = size - index - 1;//删除元素时候元素少了一位所以要减1
        if (numMoved > 0) {
            System.arraycopy(elementData, index+1, elementData, index ,
                    numMoved);
        }
        elementData[--size] = null;// clear to let GC do its work
        return oldValue;
    }
    private void fastRemove(int index) {
          modCount++;
          int numMoved = size - index - 1;//删除元素时候元素少了一位所以要减1
          if (numMoved > 0 ) {
              System.arraycopy (elementData,index+1,elementData,index,numMoved) ;
          }
        elementData[--size] = null;// clear to let GC do its work
    }

    public void clear() {
      modCount++;
        // clear to let GC do its work
      for (int i = 0 ; i < size; i++) {
          elementData[i] = null;
      }
      size = 0;
    }

    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size+numNew);
        System.arraycopy(a, 0, elementData, size, numNew);
        size+=numNew;
        return numNew != 0;
    }
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                numMoved);

        // clear to let GC do its work
        int newSize = size - (toIndex-fromIndex);
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }


    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }


    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }
}






