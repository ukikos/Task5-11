package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class AppFrame extends JFrame{
    private JLabel inputTreeLabel;
    private JTextField textFieldBracketNotationTree;
    private JButton buttonInOrderTraverse;
    private JButton buttonPostOrderTraverse;
    private JButton buttonByLevelTraverse;
    private JButton createTreeButton;
    private JTextArea outputResultTextArea;
    private JPanel mainPanel;
    private JPanel panelPaintArea;
    private JButton toBracketNotationButton;
    private JButton buttonPreOrderTraverse;
    private JButton deleteNodesButton;
    private JButton saveImageButton;
    private JButton loadTreeButton;

    private JPanel paintPanel = null;

    private JFileChooser fileChooserOpen;
    private JFileChooser fileChooserSave = null;

    SimpleBinaryTree<Integer> tree = new SimpleBinaryTree<>();

    public AppFrame(String title) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        paintPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                Point size = BinaryTreePainter.paint(tree, gr);
                ru.vsu.cs.util.SwingUtils.setFixedSize(paintPanel, size.x, size.y);
            }
        };
        JScrollPane paintJScrollPane = new JScrollPane(paintPanel);
        panelPaintArea.add(paintJScrollPane);

        fileChooserSave = new JFileChooser();
        fileChooserSave.setCurrentDirectory(new File("./images"));
        FileFilter filter = new FileNameExtensionFilter("SVG images", "svg");
        fileChooserSave.addChoosableFileFilter(filter);
        fileChooserSave.setAcceptAllFileFilterUsed(false);
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setApproveButtonText("Save");

        fileChooserOpen = new JFileChooser();
        fileChooserOpen.setCurrentDirectory(new File("./examples"));
        FileFilter txtFilter = new FileNameExtensionFilter("Text files (*.txt)", "txt");
        fileChooserOpen.addChoosableFileFilter(txtFilter);

        /*
         * Построить дерево
         */
        createTreeButton.addActionListener(actionEvent -> {
            try {
                SimpleBinaryTree<Integer> tree = new SimpleBinaryTree<>(s -> Integer.parseInt(s));
                tree.fromBracketNotation(textFieldBracketNotationTree.getText());
                this.tree = tree;
                repaintTree();
            } catch (Exception ex) {
                ru.vsu.cs.util.SwingUtils.showErrorMessageBox(ex);
            }
        });

        /*
         * В скобочную нотацию
         */
        toBracketNotationButton.addActionListener(actionEvent -> {
            if (tree == null) {
                return;
            }
            textFieldBracketNotationTree.setText(tree.toBracketStr());
        });

        /*
         * Загрузить скобочную нотацию
         */
        loadTreeButton.addActionListener(actionEvent -> {
            if (fileChooserOpen.showOpenDialog(AppFrame.this) == JFileChooser.APPROVE_OPTION) {
                try (Scanner scan = new Scanner(fileChooserOpen.getSelectedFile())) {
                    scan.useDelimiter("\\Z");
                    textFieldBracketNotationTree.setText(scan.next());
                } catch (Exception ex) {
                    textFieldBracketNotationTree.setText("Error.");
                }
            }
        });

        /*
         * Сохранить изображение в svg
         */
        saveImageButton.addActionListener(actionEvent -> {
            if (tree == null) {
                return;
            }
            try {
                if (fileChooserSave.showSaveDialog(AppFrame.this) == JFileChooser.APPROVE_OPTION) {
                    String filename = fileChooserSave.getSelectedFile().getPath();
                    if (!filename.toLowerCase().endsWith(".svg")) {
                        filename += ".svg";
                    }
                    BinaryTreePainter.saveIntoFile(tree, filename);
                    JOptionPane.showMessageDialog(AppFrame.this,
                            "Файл '" + fileChooserSave.getSelectedFile() + "' успешно сохранен");
                }
            } catch (Exception e) {
                ru.vsu.cs.util.SwingUtils.showErrorMessageBox(e);
            }
        });

        /*
         * "Прямой обход"
         */
        buttonPreOrderTraverse.addActionListener(actionEvent -> {
            showSystemOut(() -> {
                System.out.println("Посетитель:");
                tree.preOrderVisit((value, level) -> {
                    System.out.println(value + " (уровень " + level + ")");
                });
                System.out.println();
                System.out.println("Итератор:");
                for (Integer i : tree.preOrderValues()) {
                    System.out.println(i);
                }
            });
        });

        /*
         * "Симметричный обход"
         */
        buttonInOrderTraverse.addActionListener(actionEvent -> {
            showSystemOut(() -> {
                System.out.println("Посетитель:");
                tree.inOrderVisit((value, level) -> {
                    System.out.println(value + " (уровень " + level + ")");
                });
                System.out.println();
                System.out.println("Итератор:");
                for (Integer i : tree.inOrderValues()) {
                    System.out.println(i);
                }
            });
        });

        /*
         * "Обратный обход"
         */
        buttonPostOrderTraverse.addActionListener(actionEvent -> {
            showSystemOut(() -> {
                System.out.println("Посетитель:");
                tree.postOrderVisit((value, level) -> {
                    System.out.println(value + " (уровень " + level + ")");
                });
                System.out.println();
                System.out.println("Итератор:");
                for (Integer i : tree.postOrderValues()) {
                    System.out.println(i);
                }
            });
        });

        /*
         * "Обход в ширину"
         */
        buttonByLevelTraverse.addActionListener(actionEvent -> {
            showSystemOut(() -> {
                System.out.println("Посетитель:");
                tree.byLevelVisit((value, level) -> {
                    System.out.println(value + " (уровень " + level + ")");
                });
                System.out.println();
                System.out.println("Итератор:");
                for (Integer i : tree.byLevelValues()) {
                    System.out.println(i);
                }
            });
        });

        /*
         * Кнопка "удалить узлы с 1 потомком"
         */
        deleteNodesButton.addActionListener(actionEvent -> {
            for(int i = 0; i < 100; i++){
                tree.deleteNodesWithOneDescendant(tree);
            }
            repaintTree();
        });
    }

    /**
     * Перерисовка дерева
     */
    public void repaintTree() {
        paintPanel.repaint();
    }

    /**
     * Выполнение действия с выводом стандартного вывода в окне (textAreaSystemOut)
     *
     * @param action Выполняемое действие
     */
    private void showSystemOut(Runnable action) {
        PrintStream oldOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(baos, true, "UTF-8"));

            action.run();

            outputResultTextArea.setText(baos.toString("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            ru.vsu.cs.util.SwingUtils.showErrorMessageBox(e);
        }
        System.setOut(oldOut);
    }

}
