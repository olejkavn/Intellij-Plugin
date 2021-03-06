package com.thoughtworks.gauge.extract;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.thoughtworks.gauge.Constants;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ExtractConceptDialog extends JDialog {
    private JPanel contentPane;
    private com.intellij.ui.TextFieldWithAutoCompletion conceptName;
    private JTextArea steps;
    private JComboBox<String> files;
    private com.intellij.ui.TextFieldWithAutoCompletion newFile;
    private JButton OKButton;
    private JButton cancelButton;
    private JLabel errors;
    private Project project;
    private List<String> args;
    private List<String> dirNames;
    private boolean cancelled = true;
    private DialogBuilder builder;

    public ExtractConceptDialog(Project project, List<String> args, List<String> dirNames) {
        this.project = project;
        this.args = args;
        this.dirNames = dirNames;
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        setProperties();
    }

    public void setData(String data, List<String> files, DialogBuilder builder) {
        this.builder = builder;
        this.steps.setColumns(50);
        this.steps.setRows(10);
        this.steps.setEditable(false);
        this.steps.setText(data);
        for (String file : files) this.files.addItem(file);
    }

    public ExtractConceptInfo getInfo() {
        String fileName = this.files.getSelectedItem().toString();
        if (fileName.equals(ExtractConceptInfoCollector.CREATE_NEW_FILE)) fileName = this.newFile.getText();
        return new ExtractConceptInfo(this.conceptName.getText(), fileName.trim(), cancelled);
    }

    private void setProperties() {
        contentPane.registerKeyboardAction(getCancelAction(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.newFile.setVisible(false);
        this.conceptName.setPlaceholder("Enter Concept Name. Example: my new \"concept\"");
        this.newFile.setPlaceholder("Enter File Name");
        this.files.addActionListener(e -> {
            ExtractConceptDialog.this.newFile.setVisible(false);
            if (ExtractConceptDialog.this.files.getSelectedItem().toString().equals(ExtractConceptInfoCollector.CREATE_NEW_FILE))
                ExtractConceptDialog.this.newFile.setVisible(true);
        });
        this.cancelButton.addActionListener(getCancelAction());
        this.OKButton.addActionListener(getOKAction());
    }

    @NotNull
    private ActionListener getCancelAction() {
        return e -> onCancel();
    }

    @NotNull
    private ActionListener getOKAction() {
        return e -> {
            if (conceptName.getText().trim().equals(""))
                errors.setText("Please enter concept name.");
            else if (newFile.isVisible() && (FilenameUtils.removeExtension(newFile.getText().trim()).isEmpty() ||
                    !FilenameUtils.getExtension(newFile.getText().trim()).equals(Constants.CONCEPT_EXTENSION)))
                errors.setText("Please select filename from the dropdown or provide a new valid file name with `.cpt` extension.");
            else {
                cancelled = false;
                builder.getWindow().setVisible(false);
            }
        };
    }

    private void onCancel() {
        builder.getWindow().setVisible(false);
        dispose();
    }

    private void createUIComponents() {
        this.conceptName = new com.intellij.ui.TextFieldWithAutoCompletion<>(this.project, getAutoCompleteTextField(this.args), true, "");
        this.newFile = new com.intellij.ui.TextFieldWithAutoCompletion<>(this.project, getAutoCompleteTextField(this.dirNames), true, "");
    }

    private TextFieldWithAutoCompletionListProvider<String> getAutoCompleteTextField(final List<String> dirNames) {
        return new TextFieldWithAutoCompletionListProvider<String>(dirNames) {
            @Nullable
            @Override
            protected Icon getIcon(String o) {
                return null;
            }

            @NotNull
            @Override
            protected String getLookupString(String o) {
                return o;
            }

            @Nullable
            @Override
            protected String getTailText(String o) {
                return null;
            }

            @Nullable
            @Override
            protected String getTypeText(String o) {
                return null;
            }

            @Override
            public int compare(String o, String t1) {
                return 0;
            }
        };
    }
}