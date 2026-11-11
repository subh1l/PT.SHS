
package com.mycompany.bioskop;

import com.mycompany.bioskop.view.LoginFrame;

public class Bioskop {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
