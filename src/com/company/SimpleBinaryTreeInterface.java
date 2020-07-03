package com.company;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public interface SimpleBinaryTreeInterface<T> extends Iterable<T> {

    @FunctionalInterface
    interface Visitor<T> {
        void visit(T value, int level);
    }

    @FunctionalInterface
    interface NodeVisitor<T> {
        void visit(TreeNode<T> node, int level);
    }

    interface TreeNode<T> extends Iterable<T> {

        T getValue();                      //значение узла

        default TreeNode<T> getLeft() {    //левое поддерево
            return null;
        }

        default TreeNode<T> getRight() {   //праове поддерево
            return null;
        }

        default Color getColor() {
            return Color.BLACK;
        }

        /**
         * Пробую сделать итератор
         * @param visitor
         */
        default void preOrderNodeVisitor(NodeVisitor<T> visitor) {
            // данный класс нужен только для того, чтобы "спрятать" его метод (c 3-мя параметрами)
            class Inner {
                void preOrderVisit(TreeNode<T> node, NodeVisitor<T> visitor, int level) {
                    if (node == null) {
                        return;
                    }
                    visitor.visit(node, level);
                    preOrderVisit(node.getLeft(), visitor, level + 1);
                    preOrderVisit(node.getRight(), visitor, level + 1);
                }
            }
            // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
            new Inner().preOrderVisit(this, visitor, 0);
        }

        /**
         * Рекурсивынй обход поддерева с вершиной в данном узле
         * в прямом порядке.
         * @param visitor Посетитель
         */
        default void preOrderVisit(Visitor<T> visitor) {
            // данный класс нужен только для того, чтобы "спрятать" его метод (c 3-мя параметрами)
            class Inner {
                void preOrderVisit(TreeNode<T> node, Visitor<T> visitor, int level) {
                    if (node == null) {
                        return;
                    }
                    visitor.visit(node.getValue(), level);
                    preOrderVisit(node.getLeft(), visitor, level + 1);
                    preOrderVisit(node.getRight(), visitor, level + 1);
                }
            }
            // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
            new Inner().preOrderVisit(this, visitor, 0);
        }

        /**
         * Обход дерева с вершиной в данном узле
         * в виде итератора в прямом порядке.
         * @return Итератор
         */
        default Iterable<T> preOrderValues() {
            return () -> {
                Stack<TreeNode<T>> stack = new Stack<>();
                stack.push(this);

                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return stack.size() > 0;
                    }

                    @Override
                    public T next() {
                        TreeNode<T> node = stack.pop();
                        if (node.getRight() != null) {
                            stack.push(node.getRight());
                        }
                        if (node.getLeft() != null) {
                            stack.push(node.getLeft());
                        }
                        return node.getValue();
                    }

                };
            };
        }

        /**
         * Рекурсивный обход поддерева в симметричном порядке.
         * @param visitor Посетитель
         */
        default void inOrderVisit(Visitor<T> visitor) {
            // данный класс нужен только для того, чтобы "спрятать" его метод (c 3-мя параметрами)
            class Inner {
                void inOrderVisit(TreeNode<T> node, Visitor<T> visitor, int level) {
                    if (node == null) {
                        return;
                    }
                    inOrderVisit(node.getLeft(), visitor, level + 1);
                    visitor.visit(node.getValue(), level);
                    inOrderVisit(node.getRight(), visitor, level + 1);
                }
            }
            // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
            new Inner().inOrderVisit(this, visitor, 0);
        }

        /**
         * Обход поддерева в симметричном порядке в виде итератора
         * @return Итератор
         */
        default Iterable<T> inOrderValues() {
            return () -> {
                Stack<TreeNode<T>> stack = new Stack<>();
                TreeNode<T> node = this;
                while (node != null) {
                    stack.push(node);
                    node = node.getLeft();
                }

                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return !stack.isEmpty();
                    }

                    @Override
                    public T next() {
                        TreeNode<T> node = stack.pop();
                        T result = node.getValue();
                        if (node.getRight() != null) {
                            node = node.getRight();
                            while (node != null) {
                                stack.push(node);
                                node = node.getLeft();
                            }
                        }
                        return result;
                    }
                };
            };
        }

        /**
         * Обход поддерева с вершиной в данном узле
         * "посетителем" в обратном порядке рекурсивная реализация.
         *
         * @param visitor Посетитель
         */
        default void postOrderVisit(Visitor<T> visitor) {
            // данный класс нужен только для того, чтобы "спрятать" его метод (c 3-мя параметрами)
            class Inner {
                void postOrderVisit(TreeNode<T> node, Visitor<T> visitor, int level) {
                    if (node == null) {
                        return;
                    }
                    postOrderVisit(node.getLeft(), visitor, level + 1);
                    postOrderVisit(node.getRight(), visitor, level + 1);
                    visitor.visit(node.getValue(), level);
                }
            }
            // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
            new Inner().postOrderVisit(this, visitor, 0);
        }

        default Iterable<T> postOrderValues() {
            return () -> {
                // Реализация TreeNode<T>, где left = right = null
                TreeNode<T> emptyNode = () -> null;

                Stack<TreeNode<T>> stack = new Stack<>();
                Stack<T> valuesStack = new Stack<>();
                stack.push(this);

                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return stack.size() > 0;
                    }

                    @Override
                    public T next() {
                        for (TreeNode<T> node = stack.pop(); node != emptyNode; node = stack.pop()) {
                            if (node.getRight() == null && node.getLeft() == null) {
                                return node.getValue();
                            }
                            valuesStack.push(node.getValue());
                            stack.push(emptyNode);
                            if (node.getRight() != null) {
                                stack.push(node.getRight());
                            }
                            if (node.getLeft() != null) {
                                stack.push(node.getLeft());
                            }
                        }
                        return valuesStack.pop();
                    }
                };
            };
        }

        /**
         * Обход поддерева с вершиной в данном узле "посетителем" по уровням (обход в ширину)
         *
         * @param visitor Посетитель
         */
        default void byLevelVisit(Visitor<T> visitor) {
            class QueueItem {
                public SimpleBinaryTreeInterface.TreeNode<T> node;
                public int level;

                public QueueItem(SimpleBinaryTreeInterface.TreeNode<T> node, int level) {
                    this.node = node;
                    this.level = level;
                }
            }

            Queue<QueueItem> queue = new LinkedList<>();
            queue.add(new QueueItem(this, 0));
            while (!queue.isEmpty()) {
                QueueItem item = queue.poll();
                if (item.node.getLeft() != null) {
                    queue.add(new QueueItem(item.node.getLeft(), item.level + 1));
                }
                if (item.node.getRight() != null) {
                    queue.add(new QueueItem(item.node.getRight(), item.level + 1));
                }
                visitor.visit(item.node.getValue(), item.level);
            }
        }

        /**
         * Обход поддерева с вершиной в данном узле в виде итератора по уровням (обход в ширину)
         * (предполагается, что в процессе обхода дерево не меняется)
         *
         * @return Итератор
         */
        default Iterable<T> byLevelValues() {
            return () -> {
                class QueueItem {
                    public SimpleBinaryTreeInterface.TreeNode<T> node;
                    public int level;

                    public QueueItem(SimpleBinaryTreeInterface.TreeNode<T> node, int level) {
                        this.node = node;
                        this.level = level;
                    }
                }

                Queue<QueueItem> queue = new LinkedList<>();
                queue.add(new QueueItem(this, 0));

                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return queue.size() > 0;
                    }

                    @Override
                    public T next() {
                        QueueItem item = queue.poll();
                        if (item == null) {
                            // такого быть не должно, но на вский случай
                            return null;
                        }
                        if (item.node.getLeft() != null) {
                            queue.add(new QueueItem(item.node.getLeft(), item.level + 1));
                        }
                        if (item.node.getRight() != null) {
                            queue.add(new QueueItem(item.node.getRight(), item.level + 1));
                        }
                        return item.node.getValue();
                    }
                };
            };
        }

        /**
         * Реализация Iterable
         *
         * @return Итератор
         */
        @Override
        default Iterator<T> iterator() {
            return inOrderValues().iterator();
        }

    }

    /**
     * @return Корень (вершина) дерева
     */
    TreeNode<T> getRoot();

    default void preOrderNodeVisitor(NodeVisitor<T> visitor) {
        TreeNode<T> root = getRoot();
        if (root != null) {
            root.preOrderNodeVisitor(visitor);
        }
    }

    /**
     * Обход дерева "посетителем" в прямом/NLR порядке - рекурсивная реализпция
     *
     * @param visitor Посетитель
     */
    default void preOrderVisit(Visitor<T> visitor) {
        TreeNode<T> root = getRoot();
        if (root != null) {
            root.preOrderVisit(visitor);
        }
    }

    /**
     * Обход дерева в виде итератора в прямом/NLR порядке (предполагается, что в
     * процессе обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> preOrderValues() {
        TreeNode<T> root = getRoot();
        if (root == null) {
            return null;
        }
        return root.preOrderValues();
    }

    /**
     * Обход дерева "посетителем" в симметричном/поперечном/центрированном/LNR
     * порядке - рекурсивная реализпция
     *
     * @param visitor Посетитель
     */
    default void inOrderVisit(Visitor<T> visitor) {
        TreeNode<T> root = getRoot();
        if (root != null) {
            root.inOrderVisit(visitor);
        }
    }

    /**
     * Обход дерева в виде итератора в
     * симметричном/поперечном/центрированном/LNR порядке (предполагается, что в
     * процессе обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> inOrderValues() {
        TreeNode<T> root = getRoot();
        if (root == null) {
            return null;
        }
        return root.inOrderValues();
    }

    /**
     * Обход дерева "посетителем" в обратном/LRN порядке - рекурсивная
     * реализпция
     *
     * @param visitor Посетитель
     */
    default void postOrderVisit(Visitor<T> visitor) {
        TreeNode<T> root = getRoot();
        if (root != null) {
            root.postOrderVisit(visitor);
        }
    }

    /**
     * Обход дерева в виде итератора в обратном/LRN порядке (предполагается, что
     * в процессе обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> postOrderValues() {
        TreeNode<T> root = getRoot();
        if (root == null) {
            return null;
        }
        return root.postOrderValues();
    }

    /**
     * Обход дерева "посетителем" по уровням
     *
     * @param visitor Посетитель
     */
    default void byLevelVisit(Visitor<T> visitor) {
        TreeNode<T> root = getRoot();
        if (root != null) {
            root.byLevelVisit(visitor);
        }
    }

    /**
     * Обход дерева в виде итератора по уровням (предполагается, что в процессе
     * обхода дерево не меняется)
     *
     * @return Итератор
     */
    default Iterable<T> byLevelValues() {
        TreeNode<T> root = getRoot();
        if (root == null) {
            return null;
        }
        return root.byLevelValues();
    }

    /**
     * Реализация Iterable&lt;T&gt;
     *
     * @return Итератор
     */
    @Override
    default Iterator<T> iterator() {
        return inOrderValues().iterator();
    }


    /**
     * Представление дерева в виде строки в скобочной нотации
     *
     * @return дерево в виде строки
     */
    default String toBracketStr() {
        // данный класс нужен только для того, чтобы "спрятать" его метод (c 2-мя параметрами)
        class Inner {
            void printTo(TreeNode<T> node, StringBuilder sb) {
                if (node == null) {
                    return;
                }
                sb.append(node.getValue());
                if (node.getLeft() != null || node.getRight() != null) {
                    sb.append(" (");
                    printTo(node.getLeft(), sb);
                    if (node.getRight() != null) {
                        sb.append(", ");
                        printTo(node.getRight(), sb);
                    }
                    sb.append(")");
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        // класс приходится создавать, т.к. статические методы в таких класс не поддерживаются
        new Inner().printTo(getRoot(), sb);

        return sb.toString();
    }
}
