
import java.io.Serializable;
import java.util.*;

/**
 * Created by muweiliang on 2018/9/17.
 */
public class MyLinkedList<E> extends AbstractSequentialList<E>
           implements List<E>,Deque<E>,Cloneable,Serializable {

    private static final long serialVersionUID = 4477128925610607473L;

    transient  int size = 0 ;

    transient  Node<E> first;

    transient  Node<E> last;


    public MyLinkedList() {

    }

    public MyLinkedList(Collection<? extends  E> c) {
        this();
        addAll(c);
    }

    /**
     * 作为首节点加入
     * @param e
     */
    private void linkFirst(E e) {
        Node<E> f = first;
        Node<E> newNode = new Node<E>(null,e,f);
        first  =  newNode;
        if (f == null) {
            last = newNode;
        } else {
            f.prev = newNode;
        }
        /**
         * 添加元素一定要修改size,modCount
         */
        size++;
        modCount++;
    }

    /**
     * 作为尾节点加入
     * @param e
     */
     void linkLast(E e ) {
        Node<E> l = last;
        Node<E> newNode = new Node<E>(l,e,null);
        last =  newNode;
        if (l == null) {
           first =  newNode;
        } else {
            l.next = newNode;
        }
        size++;
        modCount++;
    }

    /**
     * 插入指定元素前面
     * @param e
     * @param succ
     */
    void linkBefore(E e , Node<E> succ) {
        Node<E> prev =  succ.prev;
        Node<E> newNode =  new Node<E>(prev,e,succ);
        succ.prev = newNode;
        if (prev == null) {
            first = newNode;
        }else  {
            prev.next = newNode;
        }
        size++;
        modCount++;
    }
    private E unlinkFirst(Node<E> f) {
         final E element = f.item;
         final Node<E> next = f.next;
         f.item = null;
         f.next = null;
         first = next;
         if (next == null) {
             last = null;
         } else {
             next.prev = null;
         }
         size--;
         modCount++;
        return element;
    }


    private E unlinkLast(Node<E> l) {
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.prev = null;
        l.item = null;
        last = prev;
        if (prev == null) {
            first = null;
        }else {
            prev.next = null;
        }
        size--;
        modCount++;
        return element;
    }

    /**
     * 删除指定节点
     * @param x
     * @return
     */
    E unlink(Node<E> x) {
        final  E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;
        if (prev == null) {
           first = next;
        } else {
          prev.next = next;
          x.prev = null;
        }
        if (next == null ) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }
        x.item = null;
        size--;
        modCount++;
        return element;

    }

    public E getFirst() {
        final Node<E> f = first;
        if (f == null) {
            throw new NoSuchElementException();
        }
        return first.item;
    }

    public E getLast() {
        final Node<E> l = last;
        if (l == null) {
            throw  new NoSuchElementException();
        }
        return l.item;
    }

    public E removeFirst() {
        final Node<E> f = first;
        if (f == null) {
            throw new NoSuchElementException();
        }
       return unlinkFirst(f);
    }

    public E removeLast() {
        final Node<E> l = last;
        if (l == null) {
            throw new NoSuchElementException();
        }
        return unlinkLast(l);

    }
    public void addFirst(E e) {
        linkFirst(e);
    }

    public void addLast(E e) {
        linkLast(e);
    }


    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    public int size() {
        return size;
    }

    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = first.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = first.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }


    public boolean addAll(Collection<? extends E> c){
     return addAll(size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);
        Object[] a = c.toArray();
        int newNum = a .length;
        if (newNum == 0) {
            return false;
        }
        Node<E> prev;
        Node<E> succ;
        if (index == size) {
            prev = last;
            succ = null;
        } else {
           succ = node(index);
            prev = succ.prev;
        }
        for (Object o : c) {
            E e = (E) o;
            Node<E> newNode = new Node<E>(prev,e,null);
            if (prev == null) {
               first =  newNode;
            } else {
                prev.next = newNode;
            }
            prev = newNode;
        }
        if (succ == null) {
            last = prev;
        } else {
            succ.prev = prev;
            prev.next = succ;
        }
        size+=newNum;
        modCount++;
        return true;
    }


    public void clear() {
       for (Node<E> x = first; x != null; ) {
           Node<E> next = x.next;
           x.item = null;
           x.next = null;
           x.prev = null;
           x = next;
       }
        first = last = null;
        size = 0;
        modCount++;
    }
    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E>node = node(index);
        E old = node.item;
        node.item = element;
        return  old;
    }

    public void add(int index, E element) {
        checkElementIndex(index);
        if (index == size) {
            addLast(element);
        } else {
            linkBefore(element,node(index));
        }

    }

    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }


    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }


    Node<E> node(int index) {
        if (index > size >> 1 ) {
            Node<E> x = first;
            for (int i = 0;i < index ; i++) {
                x  =  x.next;
            }
            return x;
        } else {
            Node<E> x = last;
            for (int i =size;i > index ; i--) {
                x  =  x.prev;
            }
            return x;
        }

    }


    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = first.next) {
                if (x.item == null) {
                   return index;
                }
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = first.next) {
                if (o.equals(x.item)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = last.prev) {
                if (x.item == null) {
                    return index;
                }
                index--;
            }
        } else {
            for (Node<E> x = last; x != null; x = last.prev) {
                if (o.equals(x.item)) {
                    return index;
                }
                index--;
            }
        }
        return -1;
    }




    private static class Node<E> {
         E item;
         Node<E> prev;
         Node<E> next;
        Node (Node prev,E element, Node next) {
            this.item = element;
            this.prev = prev;
            this.next = next;
        }
    }

}
