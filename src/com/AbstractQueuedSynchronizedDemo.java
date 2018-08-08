package com;

import sun.misc.Unsafe;

import java.io.Serializable;

/**
 *Java JUC 并发包基础学习第一篇
 * Created by muweiliang on 2018/7/11.
 */

public class AbstractQueuedSynchronizedDemo implements Serializable{


    private static final long serialVersionUID = -8994195011091237590L;

    protected AbstractQueuedSynchronizedDemo () {}


    /**
     * 内部类用于包装线程
     * 形成链表的列队等待
     * 获得执行权
     */
    static final class Node {
        //根据业务场景不同获得执行权的线程可以分为(独占式和共享式 类比 测试坑位 和 音乐~~~~~~~~~~~~)
        /** 标识节点当前在共享模式下*/
        static final Node SHARED = new Node();
        /**标记表示节点正在独占模式下等待 */
        static final Node EXCLUSIVE = null;
        /**由于是为并发设计存在并发情况 涉及到多线程当前状态的情况 对应waitStatus*/
        /** waitStatus值表示线程已取消 */
        static final int CANCELLED =  1;
        /** waitStatus值表示需要通知后续线程*/
        static final int SIGNAL    = -1;
        /**waitStatus值表示线程正在等待条件*/
        static final int CONDITION = -2;
        /**
         *waitStatus值表示下一个acquireShared应该
         *无条件传播(暂时不理解？)
         */
        static final int PROPAGATE = -3;

        volatile int waitStatus;

        /**等待队列中链表前一个线程*/
        volatile Node prev;

        /**等待队列中链表下一个线程*/
        volatile Node next;
        /**线程本身*/
        volatile Thread thread;
        /**指向在条件队列中的下一个正在等待的节点，是给条件队列使用的。值得注意的是条件队列只有在独享状态下才使用*/
        Node nextWaiter;
        /**用于返回线程的竞争方式*/
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        Node() {    // Used to establish initial head or SHARED marker
        }

        Node(Thread thread ,Node node) {
            this.thread = thread;
            this.nextWaiter = node;
        }
        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }

    }
    private transient volatile Node head;

    private transient volatile  Node tail;

    /**线程根据此字段争夺或者*/
    private volatile int state;

    public int getState() {
        return state;
    }

    public void setState(int newState) {
        this.state = newState;
    }


    protected  final boolean compareAndSetState(int expect,int update) {
       return   unsafe.compareAndSwapInt(this,stateOffset,expect,update);
    }

    static final long spinForTimeoutThreshold = 1000L;


    private Node enq(final  Node node){
        for(;;) {
           Node t = tail; //保存多线程的链表的尾端
           if (t == null) {
               if (compareAndsetHead(new Node())) {
                   tail = head;
               }
           } else {
               node.prev = t;
               if (compareAndSetTail(t,node)) {
                   t.next = node;
                   return t;
               }

           }
        }
    }


    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }


    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }


    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;
    private static final long waitStatusOffset;
    private static final long nextOffset;

    static{
        try {
            stateOffset = unsafe.objectFieldOffset
                    (AbstractQueuedSynchronizedDemo.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset
                    (AbstractQueuedSynchronizedDemo.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset
                    (AbstractQueuedSynchronizedDemo.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset
                    (AbstractQueuedSynchronizedDemo.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset
                    (AbstractQueuedSynchronizedDemo.class.getDeclaredField("next"));

        } catch (Exception ex) {throw  new Error(ex);}
    }

    private final boolean compareAndsetHead (Node update) {
        return unsafe.compareAndSwapObject(this,headOffset,null,update);
    }
    private final boolean compareAndSetTail(Node expect,Node update) {
        return unsafe.compareAndSwapObject(this,tailOffset,expect,update);
    }
    /**
     * CAS waitStatus field of a node.
     */
    private static final boolean compareAndSetWaitStatus(Node node,
                                                         int expect,
                                                         int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset,
                expect, update);
    }

    /**
     * CAS next field of a node.
     */
    private static final boolean compareAndSetNext(Node node,
                                                   Node expect,
                                                   Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }
}
