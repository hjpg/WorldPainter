/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProgressDialog.java
 *
 * Created on 17-okt-2011, 14:52:21
 */
package org.pepsoft.util.swing;

import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 *
 * @author pepijn
 */
public class ProgressDialog<T> extends javax.swing.JDialog implements ComponentListener, ProgressComponent.Listener {
    /** Creates new form ProgressDialog */
    public ProgressDialog(Window parent, ProgressTask<T> task, boolean cancelable) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initComponents();
        setTitle(task.getName());
        progressComponent1.setListener(this);
        progressComponent1.setTask(task);
        if (! cancelable) {
            progressComponent1.setCancelable(false);
        }
        setLocationRelativeTo(parent);
        addComponentListener(this);
    }

    /**
     * When invoked with <code>true</code>, displays the dialog and starts the
     * configured {@link ProgressTask} in a background thread, then blocks until
     * the task has completed and the dialog is disposed of. Events are
     * dispatched while this method is blocked.
     *
     * @param b <code>true</code> to show the dialog and start the task in a
     *          background thread.
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    /**
     * Execute a task in the background with progress reporting via a modal
     * dialog with a progress bar. The Cancel button is enabled.
     *
     * @param parent The parent window for the modal dialog.
     * @param task The task to execute.
     * @param <T> The return type of the task. Use {@link Void} for tasks which
     *     don't return a value.
     * @return The result of the task, or <code>null</code> if the task does not
     *     return a result.
     */
    public static <T> T executeTask(Window parent, ProgressTask<T> task) {
        return executeTask(parent, task, true);
    }
    
    /**
     * Execute a task in the background with progress reporting via a modal
     * dialog with a progress bar. The task is executed on a separate thread.
     * This method blocks until the task has completed, but events are
     * dispatched while the method is blocked. If the task throws an exception,
     * that exception will be rethrown by this method.
     *
     * @param parent The parent window for the modal dialog.
     * @param task The task to execute.
     * @param cancelable Whether the Cancel button should be enabled.
     * @param <T> The return type of the task. Use {@link Void} for tasks which
     *     don't return a value.
     * @return The result of the task, or <code>null</code> if the task does not
     *     return a result or if it was cancelled.
     * @throws Error If the task threw an {@link Error}.
     * @throws RuntimeException If the task threw a {@link RuntimeException}.
     */
    public static <T> T executeTask(Window parent, ProgressTask<T> task, boolean cancelable) {
        ProgressDialog<T> dialog = new ProgressDialog<>(parent, task, cancelable);
        dialog.setVisible(true);
        if (dialog.cancelled) {
            return null;
        } else if (dialog.exception != null) {
            if (dialog.exception instanceof Error) {
                throw (Error) dialog.exception;
            } else if (dialog.exception instanceof RuntimeException) {
                throw (RuntimeException) dialog.exception;
            } else {
                throw new RuntimeException("Checked exception thrown by task", dialog.exception);
            }
        } else {
            return dialog.result;
        }
    }
    
    // ComponentListener
    
    @Override
    public synchronized void componentShown(ComponentEvent e) {
        progressComponent1.start();
    }

    @Override public void componentResized(ComponentEvent e) {}
    @Override public void componentMoved(ComponentEvent e) {}
    @Override public void componentHidden(ComponentEvent e) {}

    // ProgressComponent.Listener

    @Override
    public void exceptionThrown(Throwable exception) {
        this.exception = exception;
        dispose();
    }

    @Override @SuppressWarnings("unchecked")
    public void done(Object result) {
        this.result = (T) result;
        dispose();
    }

    @Override
    public void cancelled() {
        cancelled = true;
        dispose();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressComponent1 = new org.pepsoft.util.swing.ProgressComponent();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressComponent1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressComponent1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.pepsoft.util.swing.ProgressComponent progressComponent1;
    // End of variables declaration//GEN-END:variables

    private boolean cancelled;
    private Throwable exception;
    private T result;
    
    private static final long serialVersionUID = 2011101701L;
}