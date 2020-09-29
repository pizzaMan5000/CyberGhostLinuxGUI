package org.swampsoft.cyberghostgui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class main {
	
	static TrayIcon trayIcon;
	static Image imageIcon;
	static PopupMenu popup;
	static SystemTray tray;
	static MainWindow mainWindow;
	
	static boolean isRoot;
	static String user;

	public static void main(String[] args) {
		
		File imageFile = new File("images/cyberghost_logo_64.png");
		//System.out.println("\nIcon loaded from 'images/cyberghost_logo_64.png' - " + imageFile.exists());
		
		URL url = main.class.getResource("/cyberghost_logo_64.png");
		//System.out.println("URL = " + url);
		
		if (!url.equals(null)) {
			try {
				imageIcon = ImageIO.read(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (imageFile.exists()) {
			imageIcon = Toolkit.getDefaultToolkit().getImage("images/cyberghost_logo_64.png");
		} else {
			imageFile = new File("cyberghost_logo_64.png");
			//System.out.println("Icon loaded from 'cyberghost_logo_64.png' - " + imageFile.exists());
			imageIcon = Toolkit.getDefaultToolkit().getImage("cyberghost_logo_64.png");
		}
		
		trayIcon = new TrayIcon(imageIcon, "CyberGhostGUI");
		
		user = System.getenv().get("USER");
		System.out.println("User who started this app: " + user);
		if (!user.equals("root")) {
			// root not found
			isRoot = false;
			System.out.println("User is not root so you cannot connect or disconnect, but you can look up info on streams, countries, and servers");
		} else {
			isRoot = true;
		}
		
		if (SystemTray.isSupported()) {
			tray = SystemTray.getSystemTray();
			
			trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        //trayIcon.displayMessage("Testing", "Action performed: " + e.getActionCommand(), TrayIcon.MessageType.INFO);
		        if (mainWindow.mainFrame.getExtendedState() == JFrame.ICONIFIED) {
		        	// is minimized
		        	mainWindow.mainFrame.setExtendedState(JFrame.NORMAL); // set window to normal
		        	mainWindow.mainFrame.setVisible(true);
		        	if (tray.getTrayIcons().length > 0) {
				    	tray.remove(trayIcon);
				    }
		        } else {
		        	// not minimized
		        	mainWindow.mainFrame.setExtendedState(JFrame.ICONIFIED); // set window to minimized
		        }
		      }
		    });

		    popup = new PopupMenu();
	        MenuItem exitItem = new MenuItem("Exit");
	        exitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mainWindow.mainFrame.dispatchEvent(new WindowEvent(mainWindow.mainFrame, WindowEvent.WINDOW_CLOSING));
				}
	        });
	        popup.add(exitItem);
	        trayIcon.setPopupMenu(popup);
		}
		
		mainWindow = new MainWindow();
	}

}
