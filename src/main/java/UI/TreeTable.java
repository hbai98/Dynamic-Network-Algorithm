package UI;

import UI.Tree.ChildNode;
import UI.Tree.Node;
import UI.Tree.RootNode;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeTable {
    private List<String> columns;
    private Node root;
    private DefaultTreeTableModel model;
    private JXTreeTable treeTable;
    private List<String[]> content;

    public TreeTable(List<String> columns) {
        content = new ArrayList<>();
        this.columns = columns;
    }

    public JXTreeTable getTreeTable() {
        root = new RootNode(new Object[]{"Root"});
        ChildNode childNode = null;
        for (String[] data : content) {
            ChildNode cNode = new ChildNode(data);
            if (data.length <= 1) {
                root.add(cNode);
                childNode = cNode;
            } else {
                childNode.add(cNode);
            }
        }
        model = new DefaultTreeTableModel(root, columns);
        treeTable = new JXTreeTable(model);
        treeTable.setShowGrid(false);
        treeTable.setColumnControlVisible(true);
        treeTable.packAll();
        return treeTable;
    }

    public List<String[]> getContent() {
        return content;
    }

    public void setContent(List<String[]> content) {
        this.content = content;
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        List<String[]> content = new ArrayList<>();
        content.add(new String[]{"Heading"});
        content.add(new String[]{"sub1","sub2"});
        content.add(new String[]{"Heading2"});
        content.add(new String[]{"sub1","sub2"});
        TreeTable table = new TreeTable(Arrays.asList("Name", "Description"));
        table.setContent(content);
        jFrame.setSize(500, 500);
        jFrame.setLayout(new BorderLayout());
        jFrame.add(new JScrollPane(table.getTreeTable()), BorderLayout.CENTER);
        jFrame.setVisible(true);
    }
}
