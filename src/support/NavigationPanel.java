/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package support;

import hms.HMS101;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author Rushabh
 */
public abstract class NavigationPanel extends javax.swing.JPanel {

    public abstract void callSave() throws Exception;

    public abstract void callDelete() throws Exception;

    public abstract void callView();

    public abstract void callPrint();

    public abstract void callClose();

    public abstract void setVoucher(String seek);

    public abstract void setComponentText();

    public abstract void setComponentEnabled(boolean bFlag);

    public abstract void setComponentTextFromRs() throws Exception;

    public abstract boolean checkEdit();

    public abstract boolean validateForm();
    public ResultSet viewDataRs = null;
    private Connection dataConnection = HMS101.connMpAdmin;
    private String editMode = "";
    private boolean saveFlag = true;
    Library lb = new Library();
    /**
     * Creates new form smallNavigationPanel
     */
    String Syspath = System.getProperty("user.dir");
    String msg = "ctrl+N -> New, ctrl+E -> Edit, ctrl+S -> save, ctrl+D -> Delete, ESC -> close, ctrl+P -> Print, Home -> First, PageUp -> Previous, PageDown -> Next, End -> Last, Alt+V -> View";

    public NavigationPanel() {
        initComponents();
        registerShortcutKey();
        setButtonEnable(true);
        setMessage(msg);
    }

    private void registerShortcutKey() {
        KeyStroke newKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK, false);
        Action newKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnNew.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(newKeyStroke, "New");
        this.getActionMap().put("New", newKeyAction);

        KeyStroke editKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK, false);
        Action editKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnEdit.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(editKeyStroke, "Edit");
        this.getActionMap().put("Edit", editKeyAction);

        KeyStroke saveKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK, false);
        Action saveKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnSave.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveKeyStroke, "Save");
        this.getActionMap().put("Save", saveKeyAction);

        KeyStroke delKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK, false);
        Action delKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnDelete.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delKeyStroke, "Delete");
        this.getActionMap().put("Delete", delKeyAction);

        KeyStroke viewKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.ALT_MASK, false);
        Action viewKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnView.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(viewKeyStroke, "View");
        this.getActionMap().put("View", viewKeyAction);

        KeyStroke firstKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0, false);
        Action firstKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnFirst.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(firstKeyStroke, "First");
        this.getActionMap().put("First", firstKeyAction);

        KeyStroke prevKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, false);
        Action prevKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnPrevious.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(prevKeyStroke, "Previous");
        this.getActionMap().put("Previous", prevKeyAction);

        KeyStroke nextKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, false);
        Action nextKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnNext.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(nextKeyStroke, "Next");
        this.getActionMap().put("Next", nextKeyAction);

        KeyStroke lastKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_END, 0, false);
        Action lastKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnLast.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(lastKeyStroke, "Last");
        this.getActionMap().put("Last", lastKeyAction);

        KeyStroke closeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action closeKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!PickList.isVisible) {
                    jbtnClose.doClick();
                }
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(closeKeyStroke, "Close");
        this.getActionMap().put("Close", closeKeyAction);

        KeyStroke printKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK, false);
        Action printKeyAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jbtnPrint.doClick();
            }
        };
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(printKeyStroke, "Print");
        this.getActionMap().put("Print", printKeyAction);
    }

    public void setNewEnable(boolean flag) {
        jbtnNew.setEnabled(flag);
    }

    public void setEditEnable(boolean flag) {
        jbtnEdit.setEnabled(flag);
    }

    public void setSaveEnable(boolean flag) {
        jbtnSave.setEnabled(flag);
    }

    public void setDeleteEnable(boolean flag) {
        jbtnDelete.setEnabled(flag);
    }

    public void setViewEnable(boolean flag) {
        jbtnView.setEnabled(flag);
    }

    public void setFirstEnable(boolean flag) {
        jbtnFirst.setEnabled(flag);
    }

    public void setPreviousEnable(boolean flag) {
        jbtnPrevious.setEnabled(flag);
    }

    public void setNextEnable(boolean flag) {
        jbtnNext.setEnabled(flag);
    }

    public void setLastEnable(boolean flag) {
        jbtnLast.setEnabled(flag);
    }

    public void setPrintEnable(boolean flag) {
        jbtnPrint.setEnabled(flag);
    }

    public ResultSet fetchData(String sql) {
        try {
            viewDataRs = null;
            PreparedStatement pstLocal = dataConnection.prepareStatement(sql);
            viewDataRs = pstLocal.executeQuery();
        } catch (Exception ex) {
            ex.getMessage();
        }
        return viewDataRs;
    }

    public void setNewFocus() {
        jbtnNew.requestFocusInWindow();
    }

    public void setEditFocus() {
        jbtnEdit.requestFocusInWindow();
    }

    public void setSaveFocus() {
        jbtnSave.requestFocusInWindow();
    }

    public void setDeleteFocus() {
        jbtnDelete.requestFocusInWindow();
    }

    public void setCloseFocus() {
        jbtnClose.requestFocusInWindow();
    }

    public void setViewFocus() {
        jbtnView.requestFocusInWindow();
    }

    public void setPrintFocus() {
        jbtnPrint.requestFocusInWindow();
    }

    public void setFirstFocus() {
        jbtnFirst.requestFocusInWindow();
    }

    public void setPreviousFocus() {
        jbtnPrevious.requestFocusInWindow();
    }

    public void setNextFocus() {
        jbtnNext.requestFocusInWindow();
    }

    public void setLastFocus() {
        jbtnLast.requestFocusInWindow();
    }

    public void setMode(String mode) {
        this.editMode = mode;
    }

    public String getMode() {
        return this.editMode;
    }

    public void setConnection(Connection con) {
        dataConnection = con;
    }

    public void setSaveFlag(boolean flag) {
        saveFlag = flag;
    }

    public boolean getSaveFlag() {
        return saveFlag;
    }

    public void setMessage(String msg) {
        jlblMsg.setText(msg);
    }

    public void setButtonEnable(boolean flag) {
        jbtnNew.setEnabled(flag);
        jbtnEdit.setEnabled(flag);
        jbtnSave.setEnabled(!flag);
        jbtnDelete.setEnabled(flag);
        jbtnPrint.setEnabled(flag);
        jbtnFirst.setEnabled(flag);
        jbtnPrevious.setEnabled(flag);
        jbtnNext.setEnabled(flag);
        jbtnLast.setEnabled(flag);
        jbtnView.setEnabled(flag);

        if (editMode.equalsIgnoreCase("N") || editMode.equalsIgnoreCase("E")) {
            jbtnClose.setText("Cancel");
        } else {
            jbtnClose.setText("Close");
        }
    }

    private void callButton(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            evt.consume();
            JButton button = (JButton) evt.getSource();
            button.doClick();
        }
    }

    public void setEnableNew(boolean flag) {
        jbtnNew.setEnabled(flag);
    }

    public void setEnableEdit(boolean flag) {
        jbtnEdit.setEnabled(flag);
    }

    public void setEnableDelete(boolean flag) {
        jbtnDelete.setEnabled(flag);
    }

    public void setEnableSave(boolean flag) {
        jbtnSave.setEnabled(flag);
    }

    public void setEnableClose(boolean flag) {
        jbtnClose.setEnabled(flag);
    }

    public void setEnablePrint(boolean flag) {
        jbtnPrint.setEnabled(flag);
    }

    public void setEnableFirst(boolean flag) {
        jbtnFirst.setEnabled(flag);
    }

    public void setEnablePrevious(boolean flag) {
        jbtnPrevious.setEnabled(flag);
    }

    public void setEnableNext(boolean flag) {
        jbtnNext.setEnabled(flag);
    }

    public void setEnableLast(boolean flag) {
        jbtnLast.setEnabled(flag);
    }

    public void setEnableView(boolean flag) {
        jbtnView.setEnabled(flag);
    }

    public void callNew() {
        setMessage(" ");
        editMode = "N";
        setButtonEnable(false);
        setComponentText();
        setComponentEnabled(true);
        setSaveFlag(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jbtnNew = new javax.swing.JButton();
        jbtnEdit = new javax.swing.JButton();
        jbtnDelete = new javax.swing.JButton();
        jbtnClose = new javax.swing.JButton();
        jbtnSave = new javax.swing.JButton();
        jbtnFirst = new javax.swing.JButton();
        jbtnPrevious = new javax.swing.JButton();
        jbtnNext = new javax.swing.JButton();
        jbtnLast = new javax.swing.JButton();
        jbtnView = new javax.swing.JButton();
        jbtnPrint = new javax.swing.JButton();
        jlblMsg = new javax.swing.JLabel();

        jbtnNew.setText("New");
        jbtnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnNewActionPerformed(evt);
            }
        });
        jbtnNew.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnNewKeyPressed(evt);
            }
        });

        jbtnEdit.setText("Edit");
        jbtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditActionPerformed(evt);
            }
        });
        jbtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnEditKeyPressed(evt);
            }
        });

        jbtnDelete.setText("Delete");
        jbtnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDeleteActionPerformed(evt);
            }
        });
        jbtnDelete.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnDeleteKeyPressed(evt);
            }
        });

        jbtnClose.setBackground(new java.awt.Color(240, 0, 0));
        jbtnClose.setText("Close");
        jbtnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCloseActionPerformed(evt);
            }
        });
        jbtnClose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnCloseKeyPressed(evt);
            }
        });

        jbtnSave.setBackground(new java.awt.Color(0, 102, 0));
        jbtnSave.setText("Save");
        jbtnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSaveActionPerformed(evt);
            }
        });
        jbtnSave.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnSaveKeyPressed(evt);
            }
        });

        jbtnFirst.setText("|<");
        jbtnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnFirstActionPerformed(evt);
            }
        });
        jbtnFirst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnFirstKeyPressed(evt);
            }
        });

        jbtnPrevious.setText("<<");
        jbtnPrevious.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPreviousActionPerformed(evt);
            }
        });
        jbtnPrevious.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPreviousKeyPressed(evt);
            }
        });

        jbtnNext.setText(">>");
        jbtnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnNextActionPerformed(evt);
            }
        });
        jbtnNext.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnNextKeyPressed(evt);
            }
        });

        jbtnLast.setText(">|");
        jbtnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnLastActionPerformed(evt);
            }
        });
        jbtnLast.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnLastKeyPressed(evt);
            }
        });

        jbtnView.setText("View");
        jbtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnViewActionPerformed(evt);
            }
        });
        jbtnView.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnViewKeyPressed(evt);
            }
        });

        jbtnPrint.setText("Print");
        jbtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnPrintActionPerformed(evt);
            }
        });
        jbtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jbtnPrintKeyPressed(evt);
            }
        });

        jlblMsg.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jlblMsg.setForeground(new java.awt.Color(0, 153, 153));
        jlblMsg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlblMsg.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jlblMsg.setPreferredSize(new java.awt.Dimension(33, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnNew, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnSave, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnFirst, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnPrevious, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnNext, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnLast, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnView, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jlblMsg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jlblMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnNew)
                    .addComponent(jbtnEdit)
                    .addComponent(jbtnDelete)
                    .addComponent(jbtnSave)
                    .addComponent(jbtnClose)
                    .addComponent(jbtnPrint)
                    .addComponent(jbtnFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnPrevious, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnLast, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnView)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNewActionPerformed
        callNew();
    }//GEN-LAST:event_jbtnNewActionPerformed

    private void jbtnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnLastActionPerformed
        try {
            setMessage(msg);
            dataConnection.setAutoCommit(false);
            setVoucher("Last");
            dataConnection.setAutoCommit(true);
        } catch (Exception ex) {
            lb.printToLogFile("Error at Navigate Last", ex);
        }
    }//GEN-LAST:event_jbtnLastActionPerformed

    private void jbtnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNextActionPerformed
        try {
            setMessage(msg);
            dataConnection.setAutoCommit(false);
            setVoucher("Next");
            dataConnection.setAutoCommit(true);
        } catch (Exception ex) {
            lb.printToLogFile("Error at Navigate Next", ex);
        }
    }//GEN-LAST:event_jbtnNextActionPerformed

    private void jbtnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCloseActionPerformed
        if (saveFlag) {
            if (!PickList.isVisible) {
                callClose();
            }
        } else {
            try {
                setMessage(msg);
                if (editMode.equalsIgnoreCase("N")) {
                    saveFlag = true;
                    setVoucher("Last");
                    setComponentEnabled(false);
                    editMode = "";
                    setButtonEnable(true);
                } else if (editMode.equalsIgnoreCase("E")) {
                    saveFlag = true;
                    setVoucher("Edit");
                    setComponentEnabled(false);
                    editMode = "";
                    setButtonEnable(true);
                }
            } catch (Exception ex) {
                lb.printToLogFile("Error at Close Navigation", ex);
            }

        }
    }//GEN-LAST:event_jbtnCloseActionPerformed

    private void jbtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditActionPerformed
        if (checkEdit()) {
            setMessage(" ");
            editMode = "E";
            setButtonEnable(false);
//        setComponentText();
            setComponentEnabled(true);
            setSaveFlag(false);
        }
    }//GEN-LAST:event_jbtnEditActionPerformed

    private void jbtnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnFirstActionPerformed
        try {
            setMessage(msg);
            dataConnection.setAutoCommit(false);
            setVoucher("First");
            dataConnection.setAutoCommit(true);
        } catch (Exception ex) {
            lb.printToLogFile("Error at Navigate First", ex);
        }
    }//GEN-LAST:event_jbtnFirstActionPerformed

    private void jbtnPreviousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPreviousActionPerformed
        try {
            setMessage(msg);
            dataConnection.setAutoCommit(false);
            setVoucher("Previous");
            dataConnection.setAutoCommit(true);
        } catch (Exception ex) {
            lb.printToLogFile("Error at Navigate Previous", ex);
        }
    }//GEN-LAST:event_jbtnPreviousActionPerformed

    private void jbtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnViewActionPerformed
        setMessage(msg);
        callView();
    }//GEN-LAST:event_jbtnViewActionPerformed

    private void jbtnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSaveActionPerformed
        if (validateForm()) {
            try {
                setMessage(" ");
                dataConnection.setAutoCommit(false);
                callSave();
                dataConnection.commit();
                dataConnection.setAutoCommit(true);
                setSaveFlag(true);
                if (editMode.equalsIgnoreCase("N")) {
                    setVoucher("Edit");
                } else if (editMode.equalsIgnoreCase("E")) {
                    setVoucher("Edit");
                }
                editMode = "";
                setComponentEnabled(false);
                setButtonEnable(true);
                setMessage(msg);

            } catch (Exception ex) {
                try {
                    lb.printToLogFile("Error at Save Voucher", ex);
                    dataConnection.rollback();
                    dataConnection.setAutoCommit(true);
                } catch (SQLException ex1) {
                    lb.printToLogFile("Error at Rollback Save Voucher", ex);
                }
            }
            jbtnLast.requestFocusInWindow();
        }
    }//GEN-LAST:event_jbtnSaveActionPerformed

    private void jbtnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDeleteActionPerformed
        try {
            editMode = "D";
            setMessage(" ");
            dataConnection.setAutoCommit(false);
            callDelete();
            dataConnection.commit();
            dataConnection.setAutoCommit(true);
            setSaveFlag(true);
            setVoucher("Previous");
            setComponentEnabled(false);
            editMode = "";
            setMessage(msg);
        } catch (Exception ex) {
            try {
                lb.printToLogFile("Error at Delete Voucher", ex);
                dataConnection.rollback();
                dataConnection.setAutoCommit(true);
            } catch (SQLException ex1) {
                lb.printToLogFile("Error at Rollback Delete Voucher", ex);
            }
        }
        jbtnLast.requestFocusInWindow();
    }//GEN-LAST:event_jbtnDeleteActionPerformed

    private void jbtnNewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnNewKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnNewKeyPressed

    private void jbtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnEditKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnEditKeyPressed

    private void jbtnSaveKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnSaveKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnSaveKeyPressed

    private void jbtnDeleteKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnDeleteKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnDeleteKeyPressed

    private void jbtnCloseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnCloseKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnCloseKeyPressed

    private void jbtnFirstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnFirstKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnFirstKeyPressed

    private void jbtnPreviousKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPreviousKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnPreviousKeyPressed

    private void jbtnNextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnNextKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnNextKeyPressed

    private void jbtnLastKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnLastKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnLastKeyPressed

    private void jbtnViewKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnViewKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnViewKeyPressed

    private void jbtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnPrintActionPerformed
        setMessage(msg);
        callPrint();
    }//GEN-LAST:event_jbtnPrintActionPerformed

    private void jbtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jbtnPrintKeyPressed
        callButton(evt);
    }//GEN-LAST:event_jbtnPrintKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbtnClose;
    private javax.swing.JButton jbtnDelete;
    private javax.swing.JButton jbtnEdit;
    private javax.swing.JButton jbtnFirst;
    private javax.swing.JButton jbtnLast;
    private javax.swing.JButton jbtnNew;
    private javax.swing.JButton jbtnNext;
    private javax.swing.JButton jbtnPrevious;
    private javax.swing.JButton jbtnPrint;
    private javax.swing.JButton jbtnSave;
    private javax.swing.JButton jbtnView;
    private javax.swing.JLabel jlblMsg;
    // End of variables declaration//GEN-END:variables
}
